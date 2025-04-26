package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.user.FriendDto;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
     * @return a {@link ResponseEntity} containing a list of {@link FriendDto} objects representing the user's friends.
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
     * Sends a friend request from the user with the specified {@code userId} to the
     * user with the specified {@code friendId}.
     *
     * <p>
     * This method allows a user to send a friend request to another user. If the
     * request already exists or if the users are already friends, an
     * {@link IllegalArgumentException} will be thrown, resulting in a
     * {@link HttpStatus#BAD_REQUEST}. If the user tries to send a request on behalf
     * of another user, an {@link IllegalStateException} will be thrown, resulting
     * in a {@link HttpStatus#FORBIDDEN}.
     * </p>
     *
     * @param userId   the ID of the user sending the friend request.
     * @param friendId the ID of the user to whom the friend request is being sent.
     * @return a {@link ResponseEntity} with status {@link HttpStatus#CREATED} if
     *         the request is successfully sent, {@link HttpStatus#BAD_REQUEST} if
     *         the request already exists or the users are already friends, or
     *         {@link HttpStatus#FORBIDDEN} if the request cannot be sent due to an
     *         invalid state.
     * @throws IllegalArgumentException if the friend request already exists or the
     *                                  users are already friends.
     * @throws IllegalStateException    if the user tries to send a request on
     *                                  behalf of another user.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Send a friend request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend request successfully sent."),
        @ApiResponse(responseCode = "400", description = "Friend request already exists or users are already friends."),
        @ApiResponse(responseCode = "403", description = "User cannot send request on behalf of another user.")
    })
    @PostMapping("/{userId}/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            friendService.addFriend(userId, friendId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The request already exists or you are already friends.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Confirms a friend request sent from the user with the specified
     * {@code requesterId} to the user with the specified {@code userId}.
     *
     * <p>
     * This method allows the recipient of a friend request to confirm it. If the
     * request is not found, has already been confirmed, or cannot be processed for
     * any other reason, a {@link RuntimeException} will be thrown, resulting in a
     * {@link HttpStatus#NOT_FOUND} response.
     * </p>
     *
     * @param userId      the ID of the user who is confirming the friend request.
     * @param requesterId the ID of the user who sent the friend request.
     * @return a {@link ResponseEntity} with status {@link HttpStatus#OK} if the
     *         request is successfully confirmed, or {@link HttpStatus#NOT_FOUND} if
     *         the request cannot be found or has already been confirmed.
     * @throws RuntimeException if the friend request is not found, already
     *                          confirmed, or cannot be processed.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Confirm a friend request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request successfully confirmed."),
        @ApiResponse(responseCode = "404", description = "Friend request not found or already confirmed."),})
    @PutMapping("/{userId}/confirm/{requesterId}")
    public ResponseEntity<?> confirmFriend(@PathVariable Long userId, @PathVariable Long requesterId) {
        try {
            friendService.confirmFriend(userId, requesterId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Method for blocking a user.
     *
     * <p>
     * This method allows a user to block another user. If any error occurs during
     * the blocking process, a {@link RuntimeException} will be thrown, resulting in
     * a {@link HttpStatus#BAD_REQUEST} response.
     * </p>
     *
     * @param userId    {@link Long} - the ID of the user who wants to block another
     *                  user.
     * @param toBlockId {@link Long} - the ID of the user who is to be blocked.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK} status if the user
     *         is successfully blocked, or {@link HttpStatus#BAD_REQUEST} if an
     *         error occurs during the blocking process.
     * @throws RuntimeException if an error occurs during the blocking process, such
     *                          as an invalid user or blocking attempt.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Block a user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully blocked."),
        @ApiResponse(responseCode = "400", description = "Error occurred during user blocking."),
        @ApiResponse(responseCode = "404", description = "User not found."),
    })
    @PostMapping("/{userId}/block/{toBlockId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId, @PathVariable Long toBlockId) {
        try {
            friendService.blockUser(userId, toBlockId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Method for removing a friendship between two users.
     *
     * @param userId   {@link Long} - ID of the user who wants to remove a friend.
     * @param friendId {@link Long} - ID of the friend to be removed.
     * @return {@link ResponseEntity} - response entity with HTTP status code:
     *         <ul>
     *         <li>204 (NO_CONTENT) if the friendship is successfully removed.</li>
     *         <li>404 (NOT_FOUND) if the friendship does not exist or if an error
     *         occurs during the removal process.</li>
     *         </ul>
     * @throws RuntimeException if the friendship does not exist or another error
     *                          occurs during the removal process.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Remove a friendship between two users.")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @DeleteMapping("/{userId}/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Method for searching new friends by name or email.
     *
     * @param searchTerm    {@link String} - the search term (name or email) used to
     *                      filter users.
     * @param currentUserId {@link Long} - the ID of the current user to exclude
     *                      them from the search results and avoid duplicates.
     * @return a {@link ResponseEntity} containing a list of {@link FriendDto}
     *         representing potential new friends matching the search query.
     * @throws RuntimeException if an error occurs while processing the request.
     * @author [Dmytro Kravchuk].
     */
    @Operation(summary = "Search for new friends.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful search for new friends.",
            content = @Content(schema = @Schema(implementation = FriendDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad request."),
        @ApiResponse(responseCode = "404", description = "User not found."),
    })
    @GetMapping("/search")
    public ResponseEntity<List<FriendDto>> searchNewFriends(
        @RequestParam @NotBlank(message = "Search term cannot be empty") String searchTerm,
        @RequestParam @Positive(message = "User ID must be positive") Long currentUserId) {
        List<FriendDto> result = friendService.searchNewFriends(searchTerm, currentUserId);
        return ResponseEntity.ok(result);
    }
}
