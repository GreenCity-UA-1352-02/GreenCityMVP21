package greencity.service;

import greencity.dto.friend.FriendCardDto;
import greencity.dto.friend.FriendDto;
import greencity.dto.friend.FriendSearchRequest;
import greencity.entity.Friend;
import greencity.entity.User;
import greencity.entity.UserSpecification;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.FriendRequestException;
import greencity.exception.exceptions.FriendshipNotFoundException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.FriendRepository;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
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
     *
     * @author Dmytro Kravchuk
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
     * Checks whether the provided user ID matches the currently authenticated user.
     * This method retrieves the current user's email from the security context,
     * fetches the corresponding user from the database, and compares their ID
     * with the provided user ID.
     * @param userId the ID to check against the currently authenticated user
     * @return true if the provided ID matches the current user's ID; false otherwise
     * @throws RuntimeException if the current user is not found in the repository
     *
     * @author Dmytro Kravchuk
     */
    private boolean isCurrentUser(Long userId) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserName)
            .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        return currentUser.getId().equals(userId);
    }

    /**
     * Removes a friendship between the currently authenticated user and the specified friend.
     * This method first retrieves the current user's ID from the security context, then searches
     * for a direct friendship record where the current user is the sender. If such a record exists
     * and the status is FRIEND, it deletes that record. It also checks for and deletes the reverse
     * friendship record (where the friend is the sender) if it exists and has the status FRIEND.
     * @param friendId the ID of the user to remove from the current user's friend list
     * @throws RuntimeException if the friendship does not exist or the current user is not allowed to remove it
     *
     * @author Dmytro Kravchuk
     */
    @Override
    @Transactional
    public void removeFriend(Long friendId) {
        Long currentUserId = getCurrentUserId();

        Friend direct = friendRepo.findByUserIdAndFriendId(currentUserId, friendId);

        if (direct == null || direct.getStatus() != FriendStatus.FRIEND) {
            throw new FriendshipNotFoundException("Friendship not found or you are not allowed to remove this friend.");
        }

        friendRepo.delete(direct);

        Friend reverse = friendRepo.findByUserIdAndFriendId(friendId, currentUserId);
        if (reverse != null && reverse.getStatus() == FriendStatus.FRIEND) {
            friendRepo.delete(reverse);
        }
    }

    /**
     * Confirms a friend request from the specified requester.
     *
     * <p>
     * This method is used by the currently authenticated user to confirm a friend
     * request that was previously sent by another user (the requester). It performs
     * the following actions:
     * </p>
     * <ul>
     * <li>Verifies that the {@code requesterId} is not null.</li>
     * <li>Retrieves the currently authenticated user from the security
     * context.</li>
     * <li>Finds the friend request where the requester is the sender and the
     * current user is the recipient.</li>
     * <li>Checks that the friend request exists and is in the {@code REQUESTED}
     * status.</li>
     * <li>Updates the friend request status to {@code FRIEND} to confirm the
     * friendship.</li>
     * <li>Creates or updates the reverse friendship entry from the current user to
     * the requester.</li>
     * </ul>
     *
     * @param friendId the ID of the user who sent the friend request.
     * @throws IllegalArgumentException if {@code requesterId} is null.
     * @throws RuntimeException         if the current user is not found, or if the
     *                                  friend request is not found or already
     *                                  confirmed.
     *
     * @author Dmytro Kravchuk
     */
    @Override
    @Transactional
    public void confirmFriend(Long friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("RequesterId cannot be null.");
        }
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Current user not found."));

        Long currentUserId = currentUser.getId();

        Friend request = friendRepo.findByUserIdAndFriendId(friendId, currentUserId);
        if (request == null) {
            throw new UserNotFoundException("Friend request not found.");
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new FriendRequestException("Friend request already confirmed or in another state.");
        }

        request.setStatus(FriendStatus.FRIEND);
        friendRepo.save(request);

        Friend reverse = friendRepo.findByUserIdAndFriendId(currentUserId, friendId);
        if (reverse == null) {
            reverse = new Friend(currentUser,
                userRepo.findById(friendId).orElseThrow(),
                FriendStatus.FRIEND);
        } else {
            reverse.setStatus(FriendStatus.FRIEND);
        }
        friendRepo.save(reverse);
    }

    /**
     * Searches for potential new friends based on the given search request and pagination settings.
     * This method applies filters such as search term, city, and mutual friends depending on the provided
     * {@link FriendSearchRequest}. It constructs a {@code UserSpecification} to query users from the database
     * and maps the results to {@link FriendCardDto} objects containing user summary data.
     * If the filter for city is not explicitly set but a city is provided, the filter is disabled by default.
     * The mutual friends filter is also disabled by default if not specified.
     * @param request  the search request containing filtering criteria like userId, city, searchTerm, etc.
     * @param pageable the pagination information (page number, size, sorting)
     * @return a paginated list of {@code FriendCardDto} with user information and friend status
     *
     * @author Dmytro Kravchuk
     */
    @Override
    public Page<FriendCardDto> searchNewFriends(FriendSearchRequest request, Pageable pageable) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (request.getFilterByCity() == null && request.getCity() != null) {
            request.setFilterByCity(false);
        }
        if (request.getFilterByMutualFriends() == null) {
            request.setFilterByMutualFriends(false);
        }

        UserSpecification spec = new UserSpecification(
            request.getUserId(),
            request.getSearchTerm(),
            request.getFilterByCity(),
            request.getFilterByMutualFriends(),
            request.getCity(),
            request.getFriendId());

        Page<User> users = userRepo.findAll(spec, pageable);

        List<FriendCardDto> friendCards = users.getContent().stream()
            .map(user -> {
                Long friendCount = friendRepo.countByUserId(user.getId());
                Boolean isFriend = friendRepo.existsByUserIdAndFriendIdAndStatus(request.getUserId(), user.getId());

                return new FriendCardDto(
                    user.getId(),
                    user.getName(),
                    user.getCity(),
                    user.getProfilePicturePath(),
                    user.getRating(),
                    friendCount,
                    isFriend);
            })
            .collect(Collectors.toList());

        return new PageImpl<>(friendCards, pageable, users.getTotalElements());
    }

    /**
     * Declines a friend request from the specified user.
     *
     * <p>
     * This method allows the currently authenticated user to decline a pending
     * friend request. It performs the following actions:
     * </p>
     * <ul>
     * <li>Verifies that the {@code friendId} is not null.</li>
     * <li>Retrieves the currently authenticated user from the security
     * context.</li>
     * <li>Finds the friend request where the sender is {@code friendId} and the
     * current user is the recipient.</li>
     * <li>Checks that the request exists and is in the {@code REQUESTED}
     * status.</li>
     * <li>Deletes the friend request from the database (the friendship is not
     * created).</li>
     * </ul>
     *
     * @param friendId the ID of the user who sent the friend request.
     * @throws IllegalArgumentException if {@code friendId} is null.
     * @throws RuntimeException         if the current user is not found or the
     *                                  request is not found or not in
     *                                  {@code REQUESTED} status.
     *
     * @author Dmytro Kravchuk
     */
    @Override
    @Transactional
    public void declineFriend(Long friendId) {
        if (friendId == null) {
            throw new IllegalArgumentException("FriendId cannot be null.");
        }

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(currentUserEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Current user not found."));

        Long currentUserId = currentUser.getId();

        Friend request = friendRepo.findByUserIdAndFriendId(friendId, currentUserId);
        if (request == null) {
            throw new UsernameNotFoundException("Friend request not found.");
        }

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new FriendRequestException("Friend request already handled.");
        }

        friendRepo.delete(request);
    }

    /**
     * Sends a friend request from the specified user to another user.
     * This method performs the following checks before creating a friend request:
     * - Ensures the current user is sending the request on their own behalf.
     * - Validates that both user IDs are not null and are not the same.
     * - Checks that the users exist in the database.
     * - Prevents sending a request if the sender has blocked the recipient or vice versa.
     * - Prevents sending a duplicate friend request if one already exists or has been received.
     * - Prevents sending a request if users are already friends.
     * If all checks pass, a new friend request with status {@code REQUESTED} is created and saved.
     * @param userId   the ID of the user who is sending the friend request
     * @param friendId the ID of the user who is receiving the friend request
     * @throws IllegalArgumentException if any of the IDs are null, equal, or users are not found
     * @throws IllegalStateException    if the current user is not the sender
     * @throws FriendRequestException   if any blocking or duplicate request conditions are met
     *
     * @author Dmytro Kravchuk
     */
    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("User ID and friend ID must not be null.");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend.");
        }
        if (!isCurrentUser(userId)) {
            throw new IllegalStateException("You cannot send friend requests on behalf of another user.");
        }

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        final User friend = userRepo.findById(friendId)
            .orElseThrow(() -> new IllegalArgumentException("Friend not found with ID: " + friendId));

        checkIfBlocked(userId, friendId);
        checkForExistingRequest(userId, friendId);
        Friend friendRequest = new Friend();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(FriendStatus.REQUESTED);
        friendRequest.setCity(friend.getCity());
        friendRepo.save(friendRequest);
    }

    /**
     * Checks if there is a block relationship between two users.
     *
     * <p>
     * This method verifies if the user identified by {@code userId} has blocked
     * the user with {@code friendId} or if the reverse block exists.
     * If either block relationship is found, an exception is thrown.
     * </p>
     *
     * @param userId   the ID of the user initiating the action.
     * @param friendId the ID of the target user to check.
     * @throws FriendRequestException if a block exists between the specified users.
     * @author Dmytro Kravchuk
     */
    private void checkIfBlocked(Long userId, Long friendId) {
        Friend blockByUser = friendRepo.findByUserIdAndFriendId(userId, friendId);
        if (blockByUser != null && blockByUser.getStatus() == FriendStatus.BLOCKED) {
            throw new FriendRequestException("You have blocked this user, and you cannot send a friend request.");
        }

        Friend blockByFriend = friendRepo.findByUserIdAndFriendId(friendId, userId);
        if (blockByFriend != null && blockByFriend.getStatus() == FriendStatus.BLOCKED) {
            throw new FriendRequestException("You cannot send a friend request to this user because they have "
                + "blocked you.");
        }
    }

    /**
     * Checks for the existence of a friend request or current friend relationship between two users.
     *
     * <p>
     * This method performs the following validation steps:
     * </p>
     * <ul>
     * <li>Checks if the current user has already sent a friend request to the target user.</li>
     * <li>Checks if the current user has received a friend request from the target user.</li>
     * <li>Checks if the users are already friends.</li>
     * </ul>
     *
     * <p>
     * If any of the above conditions are met, an appropriate {@link FriendRequestException} is thrown.
     * </p>
     *
     * @param userId   the ID of the user initiating the friend request
     * @param friendId the ID of the targeted user
     * @throws FriendRequestException if a friend request already exists in either direction,
     *                                or if the users are already friends
     *
     * @author Dmytro Kravchuk
     */
    private void checkForExistingRequest(Long userId, Long friendId) {
        Friend existingRequest = friendRepo.findByUserIdAndFriendId(userId, friendId);
        if (existingRequest != null && existingRequest.getStatus() == FriendStatus.REQUESTED) {
            throw new FriendRequestException("You have already sent a friend request to this user.");
        }

        Friend reverseRequest = friendRepo.findByUserIdAndFriendId(friendId, userId);

        if (reverseRequest != null && reverseRequest.getStatus() == FriendStatus.REQUESTED) {
            throw new FriendRequestException("You have already received a friend request from this user.");
        }
        if (existingRequest != null && existingRequest.getStatus() == FriendStatus.FRIEND) {
            throw new FriendRequestException("You are already friends.");
        }
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     * This method gets the authenticated user's email from the security context
     * and searches for a user with that email in the user repository.
     * If the user is not found, a UsernameNotFoundException is thrown.
     * @return the ID of the currently authenticated user
     * @throws UsernameNotFoundException if the user is not found in the repository
     *
     * @author Dmytro Kravchuk
     */
    public Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"))
            .getId();
    }

    /**
     * Cancels a previously sent friend request.
     *
     * <p>
     * This method allows the currently authenticated user to cancel a friend
     * request that they have previously sent, provided it has not yet been
     * accepted. If the request exists and has status {@code REQUESTED}, it will be
     * deleted from the database.
     * </p>
     *
     * @param friendId the ID of the user to whom the friend request was sent.
     * @throws IllegalStateException  if the current user is not the sender of the
     *                                request.
     * @throws FriendRequestException if no friend request exists or if the request
     *                                was already accepted.
     *
     * @author Dmytro Kravchuk
     */
    public void cancelFriendRequest(Long friendId) {
        Long currentUserId = getCurrentUserId();

        Friend existingRequest = friendRepo.findByUserIdAndFriendId(currentUserId, friendId);

        if (existingRequest == null || existingRequest.getStatus() != FriendStatus.REQUESTED) {
            throw new FriendRequestException("No pending friend request to cancel.");
        }

        friendRepo.delete(existingRequest);
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
     * @param toBlockId the ID of the user to be blocked.
     * @throws RuntimeException if either the current user or the user to be blocked
     *                          is not found, or if any error occurs during the
     *                          blocking process.
     *
     * @author Dmytro Kravchuk
     */
    @Override
    @Transactional
    public void blockUser(Long toBlockId) {
        Long currentUserId = getCurrentUserId();
        User user = userRepo.findById(currentUserId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        User toBlock = userRepo.findById(toBlockId)
            .orElseThrow(() -> new UserNotFoundException("User to block not found"));

        Friend existing = friendRepo.findByUserIdAndFriendId(currentUserId, toBlockId);
        if (existing != null) {
            friendRepo.delete(existing);
        }

        Friend reverse = friendRepo.findByUserIdAndFriendId(toBlockId, currentUserId);
        if (reverse != null) {
            friendRepo.delete(reverse);
        }

        Friend blocked = new Friend(user, toBlock, FriendStatus.BLOCKED);
        friendRepo.save(blocked);
    }

    /**
     * Verifies if the given user ID matches the ID of the currently authenticated user.
     * This method is designed for testing purposes to check the behavior of the private
     * {@code isCurrentUser} method.
     * @param userId the ID of the user to check against the currently authenticated user.
     * @return {@code true} if the given user ID matches the current user's ID, {@code false} otherwise.
     * @author Dmytro Kravchuk
     */
    public boolean checkIfCurrentUser(Long userId) {
        return isCurrentUser(userId);
    }
}