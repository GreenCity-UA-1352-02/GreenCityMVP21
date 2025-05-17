package greencity.service;

import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventReaction;
import greencity.enums.ReactionType;
import greencity.exception.exceptions.EntityNotFoundException;
import greencity.exception.exceptions.OwnLikeError;
import greencity.repository.EventReactionRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventReactionServiceImplTest {
    @InjectMocks
    private EventReactionServiceImpl eventReactionService;

    @Mock
    private EventReactionRepository reactionRepository;

    @Mock
    private UserRepo userRepository;

    @Mock
    private EventRepo eventRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void react_ShouldThrowEntityNotFoundException_WhenEventNotFound() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            eventReactionService.react(eventId, ReactionType.LIKE);
        });
        assertEquals("Event not found", ex.getMessage());
    }

    @Test
    void react_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            eventReactionService.react(eventId, ReactionType.LIKE);
        });
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void react_ShouldThrowOwnLikeError_WhenUserIsEventAuthor() {
        long eventId = 1L;
        User user = new User();
        user.setId(10L);
        Event event = new Event();
        event.setId(eventId);
        event.setAuthor(user);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        OwnLikeError ex = assertThrows(OwnLikeError.class, () -> {
            eventReactionService.react(eventId, ReactionType.LIKE);
        });
        assertEquals("Cannot react to your own event", ex.getMessage());
    }

    @Test
    void react_ShouldDeleteReaction_WhenSameReactionExists() {
        long eventId = 1L;
        User user = new User();
        user.setId(10L);
        Event event = new Event();
        event.setId(eventId);
        User author = new User();
        author.setId(20L);
        event.setAuthor(author);

        EventReaction reaction = EventReaction.builder()
            .user(user)
            .event(event)
            .reactionType(ReactionType.LIKE)
            .createdDate(LocalDateTime.now())
            .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(reaction));

        eventReactionService.react(eventId, ReactionType.LIKE);

        verify(reactionRepository, times(1)).delete(reaction);
        verify(reactionRepository, never()).save(any());
    }

    @Test
    void react_ShouldUpdateReaction_WhenDifferentReactionExists() {
        long eventId = 1L;
        User user = new User();
        user.setId(10L);
        Event event = new Event();
        event.setId(eventId);
        User author = new User();
        author.setId(20L);
        event.setAuthor(author);

        EventReaction reaction = EventReaction.builder()
            .user(user)
            .event(event)
            .reactionType(ReactionType.LIKE)
            .createdDate(LocalDateTime.now().minusDays(1))
            .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(reaction));

        eventReactionService.react(eventId, ReactionType.DISLIKE);

        assertEquals(ReactionType.DISLIKE, reaction.getReactionType());
        verify(reactionRepository, times(1)).save(reaction);
    }

    @Test
    void react_ShouldSaveNewReaction_WhenNoReactionExists() {
        long eventId = 1L;
        User user = new User();
        user.setId(10L);
        Event event = new Event();
        event.setId(eventId);
        User author = new User();
        author.setId(20L);
        event.setAuthor(author);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());

        eventReactionService.react(eventId, ReactionType.LIKE);

        ArgumentCaptor<EventReaction> captor = ArgumentCaptor.forClass(EventReaction.class);
        verify(reactionRepository, times(1)).save(captor.capture());

        EventReaction savedReaction = captor.getValue();
        assertEquals(user, savedReaction.getUser());
        assertEquals(event, savedReaction.getEvent());
        assertEquals(ReactionType.LIKE, savedReaction.getReactionType());
        assertNotNull(savedReaction.getCreatedDate());
    }

    @Test
    void countLikes_WhenEventExists_ReturnsLikeCount() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reactionRepository.countByEventAndReactionType(event, ReactionType.LIKE)).thenReturn(5L);

        long result = eventReactionService.countLikes(eventId);

        assertEquals(5L, result);

        verify(eventRepository).findById(eventId);
        verify(reactionRepository).countByEventAndReactionType(event, ReactionType.LIKE);
    }

    @Test
    void countLikes_WhenEventDoesNotExist_ThrowsEntityNotFoundException() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventReactionService.countLikes(eventId));

        verify(eventRepository).findById(eventId);
        verifyNoMoreInteractions(reactionRepository);
    }

    @Test
    void countDislikes_WhenEventExists_ReturnsDislikeCount() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reactionRepository.countByEventAndReactionType(event, ReactionType.DISLIKE)).thenReturn(3L);

        long result = eventReactionService.countDislikes(eventId);

        assertEquals(3L, result);

        verify(eventRepository).findById(eventId);
        verify(reactionRepository).countByEventAndReactionType(event, ReactionType.DISLIKE);
    }

    @Test
    void countDislikes_WhenEventDoesNotExist_ThrowsEntityNotFoundException() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventReactionService.countDislikes(eventId));

        verify(eventRepository).findById(eventId);
        verifyNoMoreInteractions(reactionRepository);
    }
}
