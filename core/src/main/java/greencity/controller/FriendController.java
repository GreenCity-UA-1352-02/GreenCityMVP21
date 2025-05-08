package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.friend.FriendCardDto;
import greencity.dto.friend.FriendDto;
import greencity.dto.friend.FriendSearchRequest;
import greencity.exception.exceptions.FriendRequestException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/friends")
@Validated
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Retrieves a list of friends for the user with the specified {@code userId}.
     *
     * <p>
     * This method fetches the list of friends for the user identified by the
     * provided {@code userId}. It uses the {@link FriendService} to fetch the list
     * and returns it as a {@link ResponseEntity} with an HTTP 200 status,
     * containing a list of {@link FriendDto} objects representing the user's
     * friends.
     * </p>
     *
     * @param userId the ID of the user whose friends are being retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link FriendDto}
     *         objects representing the user's friends.
     * @throws RuntimeException if the user with the given ID does not exist.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Retrieve a list of friends.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of friends successfully retrieved.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendDto.class)))),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFriends(@PathVariable Long userId) {
        try {
            List<FriendDto> friends = friendService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Confirms a friend request sent from the user with the specified
     * {@code friendId} to the currently authenticated user.
     *
     * <p>
     * This method allows the recipient of a friend request (authenticated user)
     * to confirm it. If the request is not found, has already been confirmed,
     * or cannot be processed, a {@link RuntimeException} will be thrown.
     * </p>
     *
     * @param friendId the ID of the user who sent the friend request.
     * @return a {@link ResponseEntity} with status {@link HttpStatus#OK} if the
     *         request is successfully confirmed, or {@link HttpStatus#NOT_FOUND}
     *         if the request cannot be found or has already been confirmed.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Confirm a friend request from another user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request successfully confirmed."),
        @ApiResponse(responseCode = "404", description = "Friend request not found or already confirmed.")
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<?> confirmFriend(@PathVariable Long friendId) {
        try {
            friendService.confirmFriend(friendId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Метод для блокировки пользователя.
     *
     * @param toBlockId ID пользователя, которого нужно заблокировать.
     * @return ResponseEntity со статусом 200 (OK) при успешной блокировке,
     *         или 400 (BAD_REQUEST) в случае ошибки.
     */
    @Operation(summary = "Block a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully blocked."),
        @ApiResponse(responseCode = "400", description = "Error occurred during user blocking."),
        @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @PostMapping("/block/{toBlockId}")
    public ResponseEntity<?> blockUser(@PathVariable Long toBlockId) {
        try {
            friendService.blockUser(toBlockId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Removes a friend from the current user's friend list.
     *
     * <p>This endpoint allows the currently authenticated user to remove a friend
     * from their friend list. The method ensures that a valid friendship exists
     * and that the current user is part of it. If successful, the friendship is removed
     * in both directions.</p>
     *
     * @param friendId {@link Long} - ID of the friend to be removed.
     * @return {@link ResponseEntity} with:
     *     <ul>
     *         <li>204 (NO_CONTENT) if the friendship is successfully removed.</li>
     *         <li>404 (NOT_FOUND) if the friendship does not exist or is not valid.</li>
     *     </ul>
     * @throws RuntimeException if the friendship does not exist or another error occurs during the removal.
     *
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Remove a friend from the current user's friend list.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Friendship successfully removed."),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId) {
        try {
            friendService.removeFriend(friendId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Method for searching potential new friends for a user.
     *
     * @param userId                {@link Long} - ID of the user performing the search.
     * @param searchTerm            {@link String} - optional text query (e.g. name or email) to search for specific
     *                                            users.
     * @param filterByCity          {@link Boolean} - optional flag to filter users by city.
     * @param filterByMutualFriends {@link Boolean} - optional flag to filter users based on mutual friends.
     * @param city                  {@link String} - optional city name for city-based filtering.
     * @param friendId              {@link Long} - optional ID of a friend, used to filter by friends-of-friends.
     * @param pageable              {@link Pageable} - pagination information (page, size, sort).
     * @return a paginated list of users matching the search and filter criteria.
     *
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Search for new potential friends based on filters.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results."),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/search-new-friends")
    public Page<FriendCardDto> searchNewFriends(
        @RequestParam("userId") Long userId,
        @RequestParam(value = "search", required = false) String searchTerm,
        @RequestParam(value = "filterByCity", required = false) Boolean filterByCity,
        @RequestParam(value = "filterByMutualFriends", required = false) Boolean filterByMutualFriends,
        @RequestParam(value = "city", required = false) String city,
        @RequestParam(value = "friendId", required = false) Long friendId,
        Pageable pageable) {
        FriendSearchRequest request = new FriendSearchRequest(userId, searchTerm, filterByCity,
            filterByMutualFriends, city, friendId);
        return friendService.searchNewFriends(request, pageable);
    }

    /**
     * Method for declining a friend request sent by another user.
     *
     * <p>This endpoint is used by the currently authenticated user to reject a pending friend request.
     * The method verifies that the request exists and has not already been accepted or declined.
     * If successful, the request is removed from the system or marked as declined.</p>
     *
     * @param friendId {@link Long} - ID of the user who sent the friend request.
     * @return {@link ResponseEntity} with:
     *     <ul>
     *         <li>200 (OK) if the friend request was successfully declined.</li>
     *         <li>400 (BAD_REQUEST) if the provided friend ID is invalid.</li>
     *         <li>404 (NOT_FOUND) if the friend request does not exist or has already been handled.</li>
     *     </ul>
     * @throws IllegalArgumentException if the {@code friendId} is null or invalid.
     * @throws RuntimeException if the friend request does not exist or another error occurs during the operation.
     *
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Decline a pending friend request from another user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request declined successfully."),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}/declineFriend")
    public ResponseEntity<String> declineFriend(@PathVariable Long friendId) {
        try {
            friendService.declineFriend(friendId);
            return ResponseEntity.ok("Friend request declined successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid friend ID.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend request not found or already handled.");
        }
    }

    /**
     * Method for sending a friend request to another user.
     *
     * <p>This endpoint allows the currently authenticated user to send a friend request to another user.
     * The method verifies that the user is not trying to add themselves, that the user is not already friends,
     * and that no pending request exists. If successful, a friend request is created.</p>
     *
     * @param friendId {@link Long} - ID of the user to whom the friend request is being sent.
     * @return {@link ResponseEntity} with:
     *     <ul>
     *         <li>201 (CREATED) if the friend request was successfully sent.</li>
     *         <li>400 (BAD_REQUEST) if the provided friend ID is invalid or if a friend request already exists.</li>
     *         <li>500 (INTERNAL_SERVER_ERROR) if an unexpected error occurs during the operation.</li>
     *     </ul>
     * @throws IllegalArgumentException if the {@code friendId} is invalid or if the current user
     *                                  tries to add themselves.
     * @throws RuntimeException if any other error occurs during the creation of the friend request.
     *
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Send a friend request to another user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend request sent successfully."),
        @ApiResponse(responseCode = "400", description = "Invalid friend ID or request already exists."),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred.")
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId) {
        try {
            Long currentUserId = friendService.getCurrentUserId();
            friendService.addFriend(currentUserId, friendId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent successfully.");
        } catch (FriendRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to cancel a previously sent friend request.
     *
     * @param friendId the ID of the user to whom the friend request was sent.
     * @return {@link ResponseEntity} with:
     *     <ul>
     *         <li>204 (NO_CONTENT) if the request was successfully canceled.</li>
     *         <li>404 (NOT_FOUND) if no such friend request exists.</li>
     *     </ul>
     */
    @Operation(summary = "Cancel a previously sent friend request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Friend request canceled successfully."),
        @ApiResponse(responseCode = "404", description = "Friend request not found or already processed.")
    })
    @DeleteMapping("/requests/{friendId}")
    public ResponseEntity<?> cancelFriendRequest(@PathVariable Long friendId) {
        try {
            friendService.cancelFriendRequest(friendId);
            return ResponseEntity.noContent().build();
        } catch (FriendRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
