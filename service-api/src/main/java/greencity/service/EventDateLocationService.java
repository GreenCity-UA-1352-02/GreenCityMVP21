package greencity.service;

import greencity.dto.event.EventDateLocationDto;

public interface EventDateLocationService {
    /**
     * Saves an event date location.
     *
     * @param eventDateLocationDto the event date location to save
     * @param eventId              the id of the event to save the date location for
     * @return the saved event date location
     */
    EventDateLocationDto save(EventDateLocationDto eventDateLocationDto, long eventId);

    /**
     * Updates an event date location.
     *
     * @param eventDateLocationDto the updated event date location data
     * @return the updated event date location
     */
    EventDateLocationDto update(EventDateLocationDto eventDateLocationDto);

    /**
     * Deletes an event date location.
     *
     * @param id the id of the event date location to delete
     */
    void delete(Long id);
}
