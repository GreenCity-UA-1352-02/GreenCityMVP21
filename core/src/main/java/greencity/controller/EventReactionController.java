package greencity.controller;

import greencity.enums.ReactionType;
import greencity.service.EventReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events/reactions")
public class EventReactionController {
    private final EventReactionService reactionService;

    public EventReactionController(EventReactionService reactionService) {
        this.reactionService = reactionService;
    }

    /**
     * Endpoint for adding or updating a like reaction to the event.
     *
     * <p>This method allows an authenticated user to react to a specific event with a LIKE.
     * <br>
     * If the user already liked the event, the like will be removed (toggle).
     * <br>
     * If the user previously disliked the event, the reaction will be changed to a like.
     * <br>
     * If the user tries to react to their own event, an error is returned.
     * </p>
     *
     * @param eventId ID of the event to like
     * @return HTTP 200 if successful, appropriate error otherwise
     *
     * @throws greencity.exception.exceptions.EntityNotFoundException if the event does not exist
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the user is not found
     * @throws greencity.exception.exceptions.OwnLikeError if the user tries to like their own event
     *
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "React with LIKE to an event",
        description = "Allows an authenticated user to like an event by its ID. "
            + "If already liked, it removes the like. If previously disliked, updates to like.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction added or updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - user is not authenticated"),
        @ApiResponse(responseCode = "404", description = "Event or user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "User cannot like their own event",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{eventId}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable Long eventId) {
        reactionService.react(eventId, ReactionType.LIKE);
        return ResponseEntity.ok().build();
    }

    /**
     * Adds, updates, or removes a dislike reaction to an event by the authenticated user.
     * @param eventId the ID of the event to dislike.
     * @return HTTP 200 OK if the dislike reaction was successfully added, updated, or removed.
     *     If the user already dislikes the event, this method removes the dislike.
     *     If the user previously liked the event, it updates the reaction to dislike.
     *     The method prevents users from reacting to their own events — in this case, it returns a 400 Bad Request.
     *     If the method cannot find the event, it returns a 404 Not Found.
     *     If the user is not authenticated, it returns a 401 Unauthorized.
     *
     * @response 200 OK — The dislike reaction was successfully added, updated, or removed.
     * @response 400 Bad Request — The user tried to react to their own event.
     * @response 401 Unauthorized — The user is not authenticated.
     * @response 404 Not Found — The event with the specified ID does not exist.
     *
     * @author Dmitry Kravchuk
     */
    @PutMapping("/{eventId}/dislike")
    public ResponseEntity<Void> dislikeEvent(@PathVariable Long eventId) {
        reactionService.react(eventId, ReactionType.DISLIKE);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the total number of likes for a specific event.
     *
     * @param eventId the ID of the event for which to count likes.
     * @return HTTP 200 OK with the number of likes for the specified event.
     *     If the event with the given ID does not exist, the method returns a 404 Not Found.
     *
     * @response 200 OK — Returns the number of likes.
     * @response 404 Not Found — The event with the specified ID does not exist.
     *
     * @author Dmitry Kravchuk
     */
    @GetMapping("/{eventId}/likes")
    public ResponseEntity<Long> getLikes(@PathVariable Long eventId) {
        return ResponseEntity.ok(reactionService.countLikes(eventId));
    }

    /**
     * Returns the total number of dislike reactions for the specified event.
     *
     * @param eventId the ID of the event.
     * @return HTTP 200 OK with the number of dislikes for the event.
     *     If the event with the given ID does not exist, the method throws
     *     {@link greencity.exception.exceptions.EntityNotFoundException} which results in a 404 Not Found.
     *
     * @response 200 OK — the number of dislike reactions successfully returned.
     * @response 404 Not Found — event with the specified ID does not exist.
     *
     * @author Dmitry Kravchuk
     */
    @GetMapping("/{eventId}/dislikes")
    public ResponseEntity<Long> getDislikes(@PathVariable Long eventId) {
        return ResponseEntity.ok(reactionService.countDislikes(eventId));
    }
}
