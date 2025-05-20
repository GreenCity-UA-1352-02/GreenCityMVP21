package greencity.service;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class EventCommentServiceImplTest {

    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Mock
    private EventCommentRepository eventCommentRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RatingCalculation ratingCalculation;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void save_shouldSaveCommentWithoutParent() {
        // given
        Long eventId = 1L;
        UserVO user = ModelUtils.getUserVO();
        User userEntity = ModelUtils.getUser();
        Event event = new Event();

        AddEventCommentDtoRequest request = AddEventCommentDtoRequest.builder()
            .text("Test comment")
            .parentCommentId(null)
            .build();

        EventComment eventComment = new EventComment();
        EventComment savedComment = new EventComment();
        AddEventCommentDtoResponse responseDto = new AddEventCommentDtoResponse();

        when(modelMapper.map(request, EventComment.class)).thenReturn(eventComment);
        when(userRepo.getReferenceById(user.getId())).thenReturn(userEntity);
        when(eventRepo.getReferenceById(eventId)).thenReturn(event);
        when(eventCommentRepo.save(eventComment)).thenReturn(savedComment);
        when(modelMapper.map(savedComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        // when
        AddEventCommentDtoResponse result = eventCommentService.save(eventId, request, user);

        // then
        assertEquals(responseDto, result);
        verify(eventCommentRepo).save(eventComment);
        verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.ADD_COMMENT, user, "Bearer token");
    }

    @Test
    void save_shouldSaveReplyToTopLevelComment() {
        // given
        Long eventId = 1L;
        Long parentCommentId = 10L;
        UserVO user = ModelUtils.getUserVO();
        User userEntity = ModelUtils.getUser();
        Event event = new Event();

        AddEventCommentDtoRequest request = AddEventCommentDtoRequest.builder()
            .text("Reply to top-level comment")
            .parentCommentId(parentCommentId)
            .build();

        EventComment eventComment = new EventComment();
        EventComment parentComment = new EventComment();
        EventComment savedComment = new EventComment();
        AddEventCommentDtoResponse responseDto = new AddEventCommentDtoResponse();

        when(modelMapper.map(request, EventComment.class)).thenReturn(eventComment);
        when(userRepo.getReferenceById(user.getId())).thenReturn(userEntity);
        when(eventRepo.getReferenceById(eventId)).thenReturn(event);
        when(eventCommentRepo.existsByIdAndParentCommentIsNull(parentCommentId)).thenReturn(true);
        when(eventCommentRepo.getReferenceById(parentCommentId)).thenReturn(parentComment);
        when(eventCommentRepo.save(eventComment)).thenReturn(savedComment);
        when(modelMapper.map(savedComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        // when
        AddEventCommentDtoResponse result = eventCommentService.save(eventId, request, user);

        // then
        assertEquals(responseDto, result);
        verify(eventCommentRepo).save(eventComment);
        verify(eventCommentRepo).getReferenceById(parentCommentId);
        await().atMost(1, SECONDS).untilAsserted(() ->
            verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.ADD_COMMENT, user, "Bearer token")
        );
    }

    @Test
    void save_shouldThrowExceptionWhenReplyToNestedComment() {
        // given
        Long eventId = 1L;
        Long parentCommentId = 20L;
        UserVO user = ModelUtils.getUserVO();

        AddEventCommentDtoRequest request = AddEventCommentDtoRequest.builder()
            .text("Invalid reply")
            .parentCommentId(parentCommentId)
            .build();

        EventComment eventComment = new EventComment();

        when(modelMapper.map(request, EventComment.class)).thenReturn(eventComment);
        when(userRepo.getReferenceById(user.getId())).thenReturn(new User());
        when(eventRepo.getReferenceById(eventId)).thenReturn(new Event());
        when(eventCommentRepo.existsByIdAndParentCommentIsNull(parentCommentId)).thenReturn(false); // <-- головне

        // when + then
        BadRequestException ex = assertThrows(BadRequestException.class,
            () -> eventCommentService.save(eventId, request, user));

        assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, ex.getMessage());
        verify(eventCommentRepo, never()).save(any());
        verify(ratingCalculation, never()).ratingCalculation(any(), any(), any());
    }

    @Test
    void update_shouldUpdateComment() {
        Long commentId = 1L;
        String newText = "Updated Comment";

        UserVO user = ModelUtils.getUserVO();
        User userEntity = ModelUtils.getUser();
        EventComment comment = EventComment.builder()
            .id(commentId)
            .text("Old Comment")
            .user(userEntity)
            .build();

        when(eventCommentRepo.findById(1L)).thenReturn(Optional.ofNullable(comment));

        eventCommentService.update(newText, commentId, user);

        assertEquals(newText, comment.getText());
        verify(eventCommentRepo).save(comment);
    }

    @Test
    void update_shouldThrowExceptionWhenOwnerIsInvalid() {
        Long commentId = 5L;
        String newText = "Updated Comment";

        UserVO user = ModelUtils.getUserVO();
        User userEntity = new User();
        userEntity.setId(99L);
        EventComment comment = EventComment.builder()
            .id(commentId)
            .text("Old Comment")
            .user(userEntity)
            .build();

        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        BadRequestException ex = assertThrows(BadRequestException.class,
            () -> eventCommentService.update(newText, commentId, user));

        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, ex.getMessage());
        verify(eventCommentRepo, never()).save(any());
    }

    @Test
    void deleteById_shouldDeleteCommentAsOwner() {
        Long commentId = 1L;
        UserVO user = ModelUtils.getUserVO();

        User userEntity = ModelUtils.getUser();

        EventComment reply = new EventComment();
        reply.setDeleted(false);

        EventComment comment = EventComment.builder()
            .id(commentId)
            .user(userEntity)
            .deleted(false)
            .comments(List.of(reply))
            .build();

        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        eventCommentService.deleteById(commentId, user);

        await().atMost(2, SECONDS).untilAsserted(() -> assertTrue(comment.isDeleted()));
        await().atMost(2, SECONDS).untilAsserted(() -> assertTrue(reply.isDeleted()));
        await().atMost(2, SECONDS).untilAsserted(() ->
            verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token")
        );

        assertTrue(comment.isDeleted());
        assertTrue(reply.isDeleted());
        verify(eventCommentRepo).save(comment);
        verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token");
    }

    @Test
    void deleteById_shouldDeleteCommentAsAdmin() {
        Long commentId = 1L;
        UserVO user = ModelUtils.getUserVO();
        user.setRole(Role.ROLE_ADMIN);
        user.setId(99L);

        User userEntity = ModelUtils.getUser();

        EventComment reply = new EventComment();
        reply.setDeleted(false);

        EventComment comment = EventComment.builder()
            .id(commentId)
            .user(userEntity)
            .deleted(false)
            .comments(List.of(reply))
            .build();

        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        eventCommentService.deleteById(commentId, user);

        await().atMost(2, SECONDS).untilAsserted(() -> assertTrue(comment.isDeleted()));
        await().atMost(2, SECONDS).untilAsserted(() -> assertTrue(reply.isDeleted()));
        await().atMost(2, SECONDS).untilAsserted(() ->
            verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token")
        );

        assertTrue(comment.isDeleted());
        assertTrue(reply.isDeleted());
        verify(eventCommentRepo).save(comment);
        verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token");
    }

    @Test
    void deleteById_shouldThrowBadRequestIfUserIsNotAdminOrOwner() {
        Long commentId = 1L;
        UserVO user = ModelUtils.getUserVO();
        user.setId(99L);

        User userEntity = ModelUtils.getUser();

        EventComment reply = new EventComment();
        reply.setDeleted(false);

        EventComment comment = EventComment.builder()
            .id(commentId)
            .user(userEntity)
            .deleted(false)
            .comments(List.of(reply))
            .build();

        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        BadRequestException ex = assertThrows(BadRequestException.class,
            () -> eventCommentService.deleteById(commentId, user));

        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, ex.getMessage());
        verify(eventCommentRepo, never()).save(any());
    }

    @Test
    void deleteById_shouldDeleteCommentWithoutReplies() {
        Long commentId = 1L;
        UserVO user = ModelUtils.getUserVO();

        User userEntity = ModelUtils.getUser();

        EventComment comment = EventComment.builder()
            .id(commentId)
            .user(userEntity)
            .deleted(false)
            .build();

        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        eventCommentService.deleteById(commentId, user);

        await().atMost(2, SECONDS).untilAsserted(() -> assertTrue(comment.isDeleted()));
        await().atMost(2, SECONDS).untilAsserted(() ->
            verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token")
        );

        assertTrue(comment.isDeleted());
        verify(eventCommentRepo).save(comment);
        verify(ratingCalculation).ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, "Bearer token");
    }
}
