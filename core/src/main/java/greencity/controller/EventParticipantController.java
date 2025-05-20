package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.event.EventParticipantDto;
import greencity.enums.EventRole;
import greencity.service.EventParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for managing the current user's participation in events.
 * Provides endpoints to retrieve events by role and cancel participation.
 * Author: Dmytro Kravchuk
 */
@RestController
@RequestMapping("/api/events/my")
@RequiredArgsConstructor
public class EventParticipantController {
    private final EventParticipantService eventParticipantService;

    /**
     * Method for retrieving all events where the current user is a participant or an organizer.
     *
     * @return HTTP 200 OK with a list of {@link EventParticipantDto} objects representing the user's event
     *      participations.
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Get all events of the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<EventParticipantDto>> getMyEvents() {
        List<EventParticipantDto> events = eventParticipantService.getUserEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Method for retrieving events by role (ORGANIZER or ATTENDEE) for the current user.
     *
     * @param role - the role to filter events by.
     * @return HTTP 200 OK with a list of {@link EventParticipantDto} filtered by the specified role.
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Get events of the current user by role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/role/{role}")
    public ResponseEntity<List<EventParticipantDto>> getMyEventsByRole(@PathVariable EventRole role) {
        List<EventParticipantDto> events = eventParticipantService.getUserEventsByRole(role);
        return ResponseEntity.ok(events);
    }

    /**
     * Method for cancelling the current user's participation in a specific event.
     *
     * @param eventId - ID of the event from which participation should be cancelled.
     * @return HTTP 204 NO CONTENT if the cancellation was successful (soft delete of participation record).
     *     The participation becomes inactive but is not removed from the database.
     * @author Dmytro Kravchuk
     */
    @Operation(summary = "Cancel participation in an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "NO_CONTENT"),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventId}/cancel")
    public ResponseEntity<Void> cancelParticipation(@PathVariable Long eventId) {
        eventParticipantService.cancelParticipation(eventId);
        return ResponseEntity.noContent().build();
    }
}
