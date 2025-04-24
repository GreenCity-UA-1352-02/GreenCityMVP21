package greencity.service;

import greencity.dto.user.FriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.Friend;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.repository.FriendRepository;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    private final FriendRepository friendRepo;

    public FriendServiceImpl(UserRepo userRepo, FriendRepository friendRepo) {
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
    }

    @Override
    public List<FriendDto> getFriends(Long userId) {
        Optional<User> user = userRepo.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id " + userId + " not found.");
        }
        return friendRepo.findAllFriendsByUserId(userId);
    }

    @Override
    public List<UserVO> searchNewFriends(String searchTerm, Long currentUserId) {
        return List.of();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        if (!isCurrentUser(userId)) {
            throw new RuntimeException("You cannot send friend requests on behalf of another user.");
        }

        if (userId.equals(friendId)) {
            throw new RuntimeException("You cannot add yourself as a friend.");
        }

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId)
            .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Проверка, не заблокирован ли user у friend
        Friend blockEntry = friendRepo.findByUserIdAndFriendId(friendId, userId);
        if (blockEntry != null && blockEntry.getStatus() == FriendStatus.BLOCKED) {
            throw new RuntimeException("You have been blocked by this user.");
        }

        Friend existing = friendRepo.findByUserIdAndFriendId(userId, friendId);
        if (existing == null) {
            Friend newRequest = new Friend(user, friend, FriendStatus.REQUESTED);
            friendRepo.save(newRequest);
        } else {
            throw new RuntimeException("The request already exists or you are already friends.");
        }
    }

    private boolean isCurrentUser(Long userId) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserName)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        return currentUser.getId().equals(userId);
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        // Удаляем обе записи: A → B и B → A
        Friend direct = friendRepo.findByUserIdAndFriendId(userId, friendId);
        Friend reverse = friendRepo.findByUserIdAndFriendId(friendId, userId);

        // Удалить можно только если существует хотя бы одна запись
        if (direct != null && direct.getUser().getId().equals(userId)) {
            friendRepo.delete(direct);
        }

        if (reverse != null && reverse.getUser().getId().equals(friendId)) {
            friendRepo.delete(reverse);
        }

        if (direct == null && reverse == null) {
            throw new RuntimeException("Friendship does not exist.");
        }
    }

    @Override
    @Transactional
    public void confirmFriend(Long userId, Long requesterId) {
        Friend request = friendRepo.findByUserIdAndFriendId(requesterId, userId);
        if (request == null || request.getStatus() != FriendStatus.REQUESTED) {
            throw new RuntimeException("Friend request not found.");
        }

        // Подтверждаем запрос
        request.setStatus(FriendStatus.FRIEND);
        friendRepo.save(request);

        // Добавляем обратную связь
        Friend reverse = friendRepo.findByUserIdAndFriendId(userId, requesterId);
        if (reverse == null) {
            reverse = new Friend(
                userRepo.findById(userId).orElseThrow(),
                userRepo.findById(requesterId).orElseThrow(),
                FriendStatus.FRIEND
            );
            friendRepo.save(reverse);
        } else {
            reverse.setStatus(FriendStatus.FRIEND);
            friendRepo.save(reverse);
        }
    }

    @Override
    @Transactional
    public void blockUser(Long userId, Long toBlockId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User toBlock = userRepo.findById(toBlockId)
            .orElseThrow(() -> new RuntimeException("User to block not found"));

        // Удалим все существующие связи
        Friend existing = friendRepo.findByUserIdAndFriendId(userId, toBlockId);
        if (existing != null) {
            friendRepo.delete(existing);
        }

        Friend reverse = friendRepo.findByUserIdAndFriendId(toBlockId, userId);
        if (reverse != null) {
            friendRepo.delete(reverse);
        }

        Friend blocked = new Friend(user, toBlock, FriendStatus.BLOCKED);
        friendRepo.save(blocked);
    }
}

