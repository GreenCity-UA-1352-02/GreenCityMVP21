package greencity.service;

import greencity.dto.event.EventParticipantDto;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import greencity.entity.event.EventParticipant;
import greencity.enums.EventRole;
import greencity.exception.exceptions.EntityNotFoundException;
import greencity.repository.EventParticipantRepository;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipantServiceImplTest {
    @Mock
    private EventParticipantRepository participantRepository;

    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private EventParticipantServiceImpl service;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String email) {
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(email, null));
    }

    @Test
    void givenAuthenticatedUser_whenGetCurrentUserId_thenReturnUserId_200() {
        String email = "test@example.com";
        User user = new User();
        user.setId(123L);
        user.setEmail(email);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Long result = ReflectionTestUtils.invokeMethod(service, "getCurrentUserId");

        assertEquals(123L, result);
    }

    @Test
    void givenUnknownEmail_whenGetCurrentUserId_thenThrowEntityNotFoundException_404() {
        String email = "notfound@example.com";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
            ReflectionTestUtils.invokeMethod(service, "getCurrentUserId")
        );

        assertEquals("User with email notfound@example.com not found", ex.getMessage());
    }

    @Test
    void givenUserIsAuthorAndAttendee_whenGetUserEvents_thenReturnAllEventsWithCorrectRoles_200() {
        String email = "test@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Test User");

        Event authorEvent = new Event();
        authorEvent.setId(10L);
        authorEvent.setTitle("Author Event");
        authorEvent.setDescription("Created by user");
        authorEvent.setOpen(true);
        authorEvent.setAuthor(user);
        authorEvent.setTags(List.of());

        EventImage authorImage = EventImage.builder()
            .link("img1")
            .event(authorEvent)
            .build();
        authorEvent.setMainImage(authorImage);

        Event otherEvent = new Event();
        otherEvent.setId(20L);
        otherEvent.setTitle("Joined Event");
        otherEvent.setDescription("User joined");
        otherEvent.setOpen(true);
        otherEvent.setAuthor(new User());
        otherEvent.setTags(List.of());

        EventImage otherImage = EventImage.builder()
            .link("img2")
            .event(otherEvent)
            .build();
        otherEvent.setMainImage(otherImage);

        EventParticipant participant = EventParticipant.builder()
            .event(otherEvent)
            .user(user)
            .role(EventRole.ATTENDEE)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(1L)).thenReturn(List.of(authorEvent));
        when(participantRepository.findAllByUserIdAndActiveTrue(1L)).thenReturn(List.of(participant));

        List<EventParticipantDto> result = service.getUserEvents();

        assertEquals(2, result.size());

        EventParticipantDto organizerDto = result.stream()
            .filter(dto -> dto.getId().equals(10L))
            .findFirst()
            .orElseThrow();
        assertEquals(EventRole.ORGANIZER, organizerDto.getRole());

        EventParticipantDto attendeeDto = result.stream()
            .filter(dto -> dto.getId().equals(20L))
            .findFirst()
            .orElseThrow();
        assertEquals(EventRole.ATTENDEE, attendeeDto.getRole());
    }

    @Test
    void givenUserNotFound_whenGetUserEvents_thenThrowEntityNotFoundException_404() {
        String email = "missing@example.com";
        mockSecurityContext(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User() {{
            setId(2L);
            setEmail(email);
        }}));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getUserEvents());
    }

    @Test
    void givenCreatedEventNotInParticipations_whenGetUserEvents_thenAddAsOrganizer_201() {
        String email = "creator@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(3L);
        user.setEmail(email);
        user.setName("Creator");

        Event createdEvent = new Event();
        createdEvent.setId(100L);
        createdEvent.setTitle("Created Event");
        createdEvent.setDescription("Only author");
        createdEvent.setOpen(true);
        createdEvent.setAuthor(user);
        createdEvent.setTags(List.of());

        EventImage createdEventImage = EventImage.builder()
            .link("img3")
            .event(createdEvent)
            .build();
        createdEvent.setMainImage(createdEventImage);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(3L)).thenReturn(List.of(createdEvent));
        when(participantRepository.findAllByUserIdAndActiveTrue(3L)).thenReturn(List.of());

        List<EventParticipantDto> result = service.getUserEvents();

        assertEquals(1, result.size());
        assertEquals(EventRole.ORGANIZER, result.getFirst().getRole());
        assertEquals(100L, result.getFirst().getId());
    }

    @Test
    void givenAttendeeEventNotCreatedByUser_whenGetUserEvents_thenRoleSetToAttendee_200() {
        String email = "attendee@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(4L);
        user.setEmail(email);
        user.setName("Attendee");

        Event attendedEvent = new Event();
        attendedEvent.setId(200L);
        attendedEvent.setTitle("Attended Event");
        attendedEvent.setOpen(true);
        attendedEvent.setAuthor(new User());
        attendedEvent.setTags(List.of());

        EventImage attendedEventImage = EventImage.builder()
            .link("img4")
            .event(attendedEvent)
            .build();
        attendedEvent.setMainImage(attendedEventImage);

        EventParticipant ep = EventParticipant.builder()
            .user(user)
            .event(attendedEvent)
            .role(EventRole.ORGANIZER)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(4L)).thenReturn(List.of());
        when(participantRepository.findAllByUserIdAndActiveTrue(4L)).thenReturn(List.of(ep));

        List<EventParticipantDto> result = service.getUserEvents();

        assertEquals(1, result.size());
        assertEquals(EventRole.ATTENDEE, result.getFirst().getRole());
        assertEquals(200L, result.getFirst().getId());
    }

    @Test
    void givenEventsWithRole_whenGetUserEventsByRole_thenReturnDtosWithCorrectRole_200() {
        String email = "user@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(5L);
        user.setEmail(email);
        user.setName("User");

        Event event1 = new Event();
        event1.setId(301L);
        event1.setTitle("Event 1");
        event1.setOpen(true);
        event1.setAuthor(user);
        event1.setTags(List.of());
        event1.setMainImage(EventImage.builder().link("img301").event(event1).build());

        Event event2 = new Event();
        event2.setId(302L);
        event2.setTitle("Event 2");
        event2.setOpen(true);
        event2.setAuthor(user);
        event2.setTags(List.of());
        event2.setMainImage(EventImage.builder().link("img302").event(event2).build());

        EventParticipant ep1 = EventParticipant.builder()
            .user(user)
            .event(event1)
            .role(EventRole.ORGANIZER)
            .active(true)
            .build();

        EventParticipant ep2 = EventParticipant.builder()
            .user(user)
            .event(event2)
            .role(EventRole.ORGANIZER)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(participantRepository.findAllByUserIdAndRoleAndActiveTrue(5L, EventRole.ORGANIZER))
            .thenReturn(List.of(ep1, ep2));

        List<EventParticipantDto> result = service.getUserEventsByRole(EventRole.ORGANIZER);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.getRole() == EventRole.ORGANIZER));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(301L)));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(302L)));
    }

    @Test
    void givenNoEventsWithRole_whenGetUserEventsByRole_thenReturnEmptyList_200() {
        String email = "user@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(6L);
        user.setEmail(email);
        user.setName("User No Events");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(participantRepository.findAllByUserIdAndRoleAndActiveTrue(6L, EventRole.ATTENDEE))
            .thenReturn(List.of());

        List<EventParticipantDto> result = service.getUserEventsByRole(EventRole.ATTENDEE);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenActiveParticipation_whenCancelParticipation_thenSetActiveFalseAndSave_200() {
        String email = "user@example.com";
        mockSecurityContext(email);

        Long userId = 7L;
        Long eventId = 101L;

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        Event event = new Event();
        event.setId(eventId);

        EventParticipant participant = EventParticipant.builder()
            .user(user)
            .event(event)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(participantRepository.findByUserIdAndEventIdAndActiveTrue(userId, eventId))
            .thenReturn(Optional.of(participant));
        when(participantRepository.save(any(EventParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.cancelParticipation(eventId);

        assertFalse(participant.isActive());
        verify(participantRepository).save(participant);
    }

    @Test
    void givenNoActiveParticipation_whenCancelParticipation_thenThrowEntityNotFoundException_404() {
        String email = "user@example.com";
        mockSecurityContext(email);

        Long userId = 8L;
        Long eventId = 202L;

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(participantRepository.findByUserIdAndEventIdAndActiveTrue(userId, eventId))
            .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> service.cancelParticipation(eventId));
        assertEquals("Participation not found", exception.getMessage());
    }

    @Test
    void givenUserParticipatesInNonCreatedEventWithWrongRole_whenGetUserEvents_thenRoleCorrectedToAttendee() {
        String email = "user2@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(2L);
        user.setEmail(email);

        Event nonCreatedEvent = new Event();
        nonCreatedEvent.setId(20L);
        nonCreatedEvent.setAuthor(new User());
        nonCreatedEvent.setTags(List.of());

        EventImage mainImage = new EventImage();
        mainImage.setLink("img_default.png");
        nonCreatedEvent.setMainImage(mainImage);

        EventParticipant participant = EventParticipant.builder()
            .user(user)
            .event(nonCreatedEvent)
            .role(EventRole.ORGANIZER)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(2L)).thenReturn(List.of());
        when(participantRepository.findAllByUserIdAndActiveTrue(2L)).thenReturn(List.of(participant));
        when(participantRepository.save(any(EventParticipant.class))).thenAnswer(invocation ->
            invocation.getArgument(0));

        List<EventParticipantDto> result = service.getUserEvents();

        verify(participantRepository).save(participant);
        assertEquals(EventRole.ATTENDEE, participant.getRole());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(20L) && dto.getRole()
            == EventRole.ATTENDEE));
    }

    @Test
    void givenUserIsAuthorWithWrongRole_whenGetUserEvents_thenRoleCorrectedToOrganizer() {
        String email = "user@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        Event createdEvent = new Event();
        createdEvent.setId(10L);
        createdEvent.setAuthor(user);

        EventImage mainImage = new EventImage();
        mainImage.setLink("img1");
        createdEvent.setMainImage(mainImage);

        createdEvent.setTags(List.of());

        EventParticipant participant = EventParticipant.builder()
            .user(user)
            .event(createdEvent)
            .role(EventRole.ATTENDEE)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(1L)).thenReturn(List.of(createdEvent));
        when(participantRepository.findAllByUserIdAndActiveTrue(1L)).thenReturn(List.of(participant));
        when(participantRepository.save(any(EventParticipant.class))).thenAnswer(invocation ->
            invocation.getArgument(0));

        List<EventParticipantDto> result = service.getUserEvents();

        verify(participantRepository).save(participant);
        assertEquals(EventRole.ORGANIZER, participant.getRole());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(10L) && dto.getRole() ==
            EventRole.ORGANIZER));
    }

    @Test
    void givenUserIsAuthorWithCorrectRole_whenGetUserEvents_thenNoRoleChange() {
        String email = "user@example.com";
        mockSecurityContext(email);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        Event createdEvent = new Event();
        createdEvent.setId(10L);
        createdEvent.setAuthor(user);

        EventImage mainImage = new EventImage();
        mainImage.setLink("img1");
        createdEvent.setMainImage(mainImage);

        createdEvent.setTags(List.of());

        EventParticipant participant = EventParticipant.builder()
            .user(user)
            .event(createdEvent)
            .role(EventRole.ORGANIZER)
            .active(true)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(participantRepository.findAllEventsByAuthorId(1L)).thenReturn(List.of(createdEvent));
        when(participantRepository.findAllByUserIdAndActiveTrue(1L)).thenReturn(List.of(participant));

        List<EventParticipantDto> result = service.getUserEvents();

        verify(participantRepository, never()).save(any(EventParticipant.class));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(10L) && dto.getRole() ==
            EventRole.ORGANIZER));
    }
}
