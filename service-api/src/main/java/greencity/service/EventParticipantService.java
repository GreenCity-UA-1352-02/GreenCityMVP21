package greencity.service;

import greencity.dto.event.EventParticipantDto;
import greencity.enums.EventRole;
import java.util.List;

/**
 * Service interface for managing event participation.
 * Provides methods to retrieve events related to the current user and manage participation status.
 *
 * @author Dmytro Kravchuk
 */
public interface EventParticipantService {
    /**
     * Retrieves all events the current user is involved in, either as an organizer or an attendee.
     * Events created by the user will be marked with the ORGANIZER role.
     *
     * @return a list of {@link EventParticipantDto} objects representing user-related events.
     * @author Dmytro Kravchuk
     */
    List<EventParticipantDto> getUserEvents();

    /**
     * Retrieves all active events for the current user that match the given {@link EventRole}.
     *
     * @param role the role of the current user in the event (e.g., ORGANIZER, ATTENDEE).
     * @return a list of {@link EventParticipantDto} objects matching the given role.
     * @author Dmytro Kravchuk
     */
    List<EventParticipantDto> getUserEventsByRole(EventRole role);

    /**
     * Cancels the current user's participation in the event with the given ID.
     * This action deactivates the participation without deleting the record.
     *
     * @param eventId the ID of the event from which to cancel participation.
     * @author Dmytro Kravchuk
     */
    void cancelParticipation(Long eventId);
}
