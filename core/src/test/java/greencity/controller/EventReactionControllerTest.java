package greencity.controller;

import greencity.enums.ReactionType;
import greencity.exception.exceptions.EntityNotFoundException;
import greencity.exception.exceptions.OwnLikeError;
import greencity.service.EventReactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventReactionControllerTest {
    @Mock
    private EventReactionService reactionService;

    @InjectMocks
    private EventReactionController reactionController;

    @Test
    void likeEvent_Success_ReturnsOk() {
        long eventId = 1L;

        ResponseEntity<Void> response = reactionController.likeEvent(eventId);

        verify(reactionService).react(eventId, ReactionType.LIKE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void likeEvent_EventNotFound_ThrowsEntityNotFoundException() {
        long eventId = 1L;
        doThrow(new EntityNotFoundException("Event not found"))
            .when(reactionService).react(eventId, ReactionType.LIKE);

        assertThrows(EntityNotFoundException.class, () -> reactionController.likeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.LIKE);
    }

    @Test
    void likeEvent_UserNotFound_ThrowsUsernameNotFoundException() {
        long eventId = 1L;
        doThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"))
            .when(reactionService).react(eventId, ReactionType.LIKE);

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> reactionController.likeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.LIKE);
    }

    @Test
    void likeEvent_OwnLikeError_ThrowsOwnLikeError() {
        long eventId = 1L;
        doThrow(new OwnLikeError("Cannot react to your own event"))
            .when(reactionService).react(eventId, ReactionType.LIKE);

        assertThrows(OwnLikeError.class, () -> reactionController.likeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.LIKE);
    }

    @Test
    void dislikeEvent_Success_ReturnsOk() {
        long eventId = 1L;

        ResponseEntity<Void> response = reactionController.dislikeEvent(eventId);

        verify(reactionService).react(eventId, ReactionType.DISLIKE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void dislikeEvent_EventNotFound_ThrowsEntityNotFoundException() {
        long eventId = 2L;
        doThrow(new EntityNotFoundException("Event not found"))
            .when(reactionService).react(eventId, ReactionType.DISLIKE);

        assertThrows(EntityNotFoundException.class, () -> reactionController.dislikeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.DISLIKE);
    }

    @Test
    void dislikeEvent_UserNotFound_ThrowsUsernameNotFoundException() {
        long eventId = 3L;
        doThrow(new UsernameNotFoundException("User not found"))
            .when(reactionService).react(eventId, ReactionType.DISLIKE);

        assertThrows(UsernameNotFoundException.class, () -> reactionController.dislikeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.DISLIKE);
    }

    @Test
    void dislikeEvent_OwnLikeError_ThrowsOwnLikeError() {
        long eventId = 4L;
        doThrow(new OwnLikeError("Cannot react to your own event"))
            .when(reactionService).react(eventId, ReactionType.DISLIKE);

        assertThrows(OwnLikeError.class, () -> reactionController.dislikeEvent(eventId));
        verify(reactionService).react(eventId, ReactionType.DISLIKE);
    }

    @Test
    void getLikes_ReturnsLikesCount_Success() {

        Long eventId = 1L;
        long expectedLikes = 10L;
        when(reactionService.countLikes(eventId)).thenReturn(expectedLikes);

        ResponseEntity<Long> response = reactionController.getLikes(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedLikes, response.getBody());
        verify(reactionService).countLikes(eventId);
    }

    @Test
    void getLikes_EventNotFound_ThrowsEntityNotFoundException() {
        Long eventId = 2L;
        when(reactionService.countLikes(eventId))
            .thenThrow(new EntityNotFoundException("Event not found"));

        assertThrows(EntityNotFoundException.class, () -> reactionController.getLikes(eventId));
        verify(reactionService).countLikes(eventId);
    }

    @Test
    void getDislikes_ReturnsDislikeCount_Success() {
        Long eventId = 1L;
        long expectedDislikes = 5L;
        when(reactionService.countDislikes(eventId)).thenReturn(expectedDislikes);

        ResponseEntity<Long> response = reactionController.getDislikes(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDislikes, response.getBody());
        verify(reactionService).countDislikes(eventId);
    }

    @Test
    void getDislikes_EventNotFound_ThrowsEntityNotFoundException() {
        Long eventId = 2L;
        when(reactionService.countDislikes(eventId))
            .thenThrow(new EntityNotFoundException("Event not found"));

        assertThrows(EntityNotFoundException.class, () -> reactionController.getDislikes(eventId));
        verify(reactionService).countDislikes(eventId);
    }
}
