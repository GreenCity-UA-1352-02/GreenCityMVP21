package greencity.service;

import greencity.dto.user.FriendDto;
import greencity.entity.Friend;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.FriendRepository;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    private final FriendRepository friendRepo;

    public FriendServiceImpl(UserRepo userRepo, FriendRepository friendRepo) {
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
    }

    /**
     * Retrieves a list of all friends for the specified user.
     *
     * <p>
     * This method first checks if the user exists in the database. If the user is
     * not found, it throws an {@link UserNotFoundException}. If the user exists,
     * the method retrieves and returns all friends associated with the given
     * {@code userId} from the friend repository.
     * </p>
     *
     * @param userId the ID of the user whose friends are being retrieved.
     * @return a list of {@link FriendDto} objects representing the user's friends.
     * @throws UserNotFoundException if the user with the specified {@code userId}
     *                               does not exist.
     */
    @Override
    public List<FriendDto> getFriends(Long userId) {
        Optional<User> user = userRepo.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }
        return friendRepo.findAllFriendsByUserId(userId);
    }

    /**
     * Method for searching new friends by name or email, excluding current user's
     * existing friends and already sent friend requests.
     *
     * @param searchTerm    the string to search by (part of name or email).
     * @param currentUserId the ID of the user who is performing the search.
     * @return list of users matching the search criteria and not already connected.
     */
    @Override
    public List<FriendDto> searchNewFriends(String searchTerm, Long currentUserId) {
        List<User> allMatches = userRepo
            .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm);

        List<Friend> existingRelations = friendRepo.findAllByUserId(currentUserId);

        Set<Long> excludedIds = existingRelations.stream()
            .map(friend -> friend.getFriend().getId())
            .collect(Collectors.toSet());

        excludedIds.add(currentUserId);

        return allMatches.stream()
            .filter(user -> !excludedIds.contains(user.getId()))
            .map(user -> new FriendDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePicturePath()))
            .collect(Collectors.toList());
    }

    /**
     * Sends a friend request from one user to another. This method checks the
     * following conditions before sending a friend request:
     *
     * <ul>
     * <li>The current authenticated user must match the provided
     * {@code userId}.</li>
     * <li>A user cannot send a request to themselves.</li>
     * <li>Both the sender and recipient must exist in the database.</li>
     * <li>If the recipient has blocked the sender, the request is denied.</li>
     * <li>If a friend request already exists, or they are already friends, a
     * request is not sent.</li>
     * </ul>
     *
     * @param userId   the ID of the user sending the friend request.
     * @param friendId the ID of the user to whom the friend request is being sent.
     * @throws RuntimeException    if the current user does not match
     *                             {@code userId}, or if {@code userId} equals
     *                             {@code friendId}, or if either user is not found,
     *                             or if the sender is blocked by the recipient.
     * @throws BadRequestException if a friend request already exists, or they are
     *                             already friends.
     */
    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!isCurrentUser(userId)) {
            throw new IllegalStateException("You cannot send friend requests on behalf of another user.");
        }

        if (userId.equals(friendId)) {
            throw new RuntimeException("You cannot add yourself as a friend.");
        }

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId)
            .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friend blockEntry = friendRepo.findByUserIdAndFriendId(friendId, userId);
        if (blockEntry != null && blockEntry.getStatus() == FriendStatus.BLOCKED) {
            throw new RuntimeException("You have been blocked by this user.");
        }

        Friend existing = friendRepo.findByUserIdAndFriendId(userId, friendId);
        if (existing == null) {
            Friend newRequest = new Friend(user, friend, FriendStatus.REQUESTED);
            friendRepo.save(newRequest);
        } else {
            throw new IllegalArgumentException("The request already exists or you are already friends.");
        }
    }

    private boolean isCurrentUser(Long userId) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserName)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        return currentUser.getId().equals(userId);
    }

    /**
     * Removes a friend from the current user's friend list.
     *
     * <p>
     * This method ensures that the current authenticated user is attempting to
     * remove a friend from their own friend list. It first checks if the current
     * user exists and is the same as the {@code userId} provided. Then, it checks
     * if a friendship exists between the user and the specified friend. If a valid
     * friendship exists, it removes both directions of the friendship (from
     * {@code userId} to {@code friendId} and from {@code friendId} to
     * {@code userId}).
     * </p>
     *
     * @param userId   the ID of the user who is removing a friend.
     * @param friendId the ID of the friend to be removed.
     * @throws RuntimeException if the current user is not found, or if the current
     *                          user is not the one attempting to remove the friend,
     *                          or if the users are not friends, or if the
     *                          friendship has already been removed.
     */
    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("Current user not found."));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("You can only remove your own friends.");
        }

        Friend direct = friendRepo.findByUserIdAndFriendId(userId, friendId);
        Friend reverse = friendRepo.findByUserIdAndFriendId(friendId, userId);

        if (direct == null || direct.getStatus() != FriendStatus.FRIEND) {
            throw new RuntimeException("You are not friends.");
        }

        friendRepo.delete(direct);
        if (reverse != null && reverse.getStatus() == FriendStatus.FRIEND) {
            friendRepo.delete(reverse);
        }
    }

    /**
     * Confirms a friend request from the specified requester.
     *
     * <p>
     * This method verifies that the current authenticated user is the recipient of
     * the friend request and that the request is in the "REQUESTED" status. If the
     * conditions are met, the method changes the status of the request to "FRIEND"
     * to confirm the friendship. It also updates or creates the reverse friendship
     * entry for the requester.
     * </p>
     *
     * @param userId      the ID of the user confirming the friend request.
     * @param requesterId the ID of the user who sent the friend request.
     * @throws RuntimeException if the current user is not found, or if the current
     *                          user is not the recipient of the friend request, or
     *                          if the request is not in the "REQUESTED" status, or
     *                          if the friend request is not found.
     */
    @Override
    @Transactional
    public void confirmFriend(Long userId, Long requesterId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserEmail)
            .orElseThrow(() -> new RuntimeException("Current user not found."));

        if (!currentUser.getId().equals(userId)) {
            throw new RuntimeException("Only the recipient of the friend request can confirm it.");
        }

        Friend request = friendRepo.findByUserIdAndFriendId(requesterId, userId);
        if (request == null || request.getStatus() != FriendStatus.REQUESTED) {
            throw new RuntimeException("Friend request not found or already confirmed.");
        }

        request.setStatus(FriendStatus.FRIEND);
        friendRepo.save(request);

        Friend reverse = friendRepo.findByUserIdAndFriendId(userId, requesterId);
        if (reverse == null) {
            reverse = new Friend(currentUser,
                userRepo.findById(requesterId).orElseThrow(),
                FriendStatus.FRIEND);
        } else {
            reverse.setStatus(FriendStatus.FRIEND);
        }
        friendRepo.save(reverse);
    }

    /**
     * Blocks a user by creating a "BLOCKED" friendship status between the current
     * user and the user to block.
     *
     * <p>
     * This method first verifies that both the current user and the user to be
     * blocked exist in the database. It then deletes any existing friendship
     * records between the two users. Finally, it creates a new friendship record
     * with a "BLOCKED" status, effectively blocking the user.
     * </p>
     *
     * @param userId    the ID of the user who is performing the block action.
     * @param toBlockId the ID of the user to be blocked.
     * @throws RuntimeException if either the current user or the user to be blocked
     *                          is not found.
     */
    @Override
    @Transactional
    public void blockUser(Long userId, Long toBlockId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User toBlock = userRepo.findById(toBlockId)
            .orElseThrow(() -> new RuntimeException("User to block not found"));

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
