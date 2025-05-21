package greencity.controller;

import greencity.dto.event.EventParticipantDto;
import greencity.enums.EventRole;
import greencity.service.EventParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipantControllerTest {
    @Mock
    private EventParticipantService eventParticipantService;

    private EventParticipantController controller;

    @BeforeEach
    void setup() {
        controller = new EventParticipantController(eventParticipantService);
    }

    @Test
    void whenGetMyEvents_thenReturnEventList() {
        EventParticipantDto dto1 = new EventParticipantDto();
        dto1.setId(1L);
        dto1.setRole(EventRole.ORGANIZER);

        EventParticipantDto dto2 = new EventParticipantDto();
        dto2.setId(2L);
        dto2.setRole(EventRole.ATTENDEE);

        List<EventParticipantDto> mockEvents = List.of(dto1, dto2);

        when(eventParticipantService.getUserEvents()).thenReturn(mockEvents);

        var response = controller.getMyEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(mockEvents, response.getBody());

        verify(eventParticipantService, times(1)).getUserEvents();
    }

    @Test
    void whenGetMyEventsByRole_withOrganizerRole_thenReturnFilteredEvents() {
        EventParticipantDto dto1 = new EventParticipantDto();
        dto1.setId(1L);
        dto1.setRole(EventRole.ORGANIZER);

        EventParticipantDto dto2 = new EventParticipantDto();
        dto2.setId(2L);
        dto2.setRole(EventRole.ORGANIZER);

        List<EventParticipantDto> mockEvents = List.of(dto1, dto2);

        when(eventParticipantService.getUserEventsByRole(EventRole.ORGANIZER)).thenReturn(mockEvents);

        ResponseEntity<List<EventParticipantDto>> response = controller.getMyEventsByRole(EventRole.ORGANIZER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvents, response.getBody());

        verify(eventParticipantService, times(1)).getUserEventsByRole(EventRole.ORGANIZER);
    }

    @Test
    void whenGetMyEventsByRole_withAttendeeRole_thenReturnFilteredEvents() {
        EventParticipantDto dto1 = new EventParticipantDto();
        dto1.setId(3L);
        dto1.setRole(EventRole.ATTENDEE);

        List<EventParticipantDto> mockEvents = List.of(dto1);

        when(eventParticipantService.getUserEventsByRole(EventRole.ATTENDEE)).thenReturn(mockEvents);

        ResponseEntity<List<EventParticipantDto>> response = controller.getMyEventsByRole(EventRole.ATTENDEE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvents, response.getBody());

        verify(eventParticipantService, times(1)).getUserEventsByRole(EventRole.ATTENDEE);
    }

    @Test
    void whenCancelParticipation_thenReturnsNoContent() {
        Long eventId = 123L;

        doNothing().when(eventParticipantService).cancelParticipation(eventId);

        ResponseEntity<Void> response = controller.cancelParticipation(eventId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(eventParticipantService, times(1)).cancelParticipation(eventId);
    }

    @Test
    void whenCancelParticipation_withNonExistingEvent_thenThrowsException() {
        Long eventId = 999L;

        doThrow(new RuntimeException("Event not found")).when(eventParticipantService).cancelParticipation(eventId);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> controller.cancelParticipation(eventId));
        assertEquals("Event not found", ex.getMessage());

        verify(eventParticipantService, times(1)).cancelParticipation(eventId);
    }

}
