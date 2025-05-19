package greencity.service;

import static greencity.constant.AppConstant.AUTHORIZATION;
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
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentRepository;
import greencity.repository.EventRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepository eventCommentRepo;
    private EventRepo eventRepo;
    private ModelMapper modelMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user) {
        Event event = eventRepo
            .findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId));
        EventComment eventComment = modelMapper.map(comment, EventComment.class);
        eventComment.setUser(modelMapper.map(user, User.class));
        eventComment.setEvent(event);
        if (comment.parentCommentId() != 0) {
            EventComment parentComment =
                eventCommentRepo.findById(comment.parentCommentId()).orElseThrow(
                    () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                eventComment.setParentComment(parentComment);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation
                .ratingCalculation(RatingCalculationEnum.ADD_COMMENT, user, accessToken));
        return modelMapper.map(eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);
    }

    @Override
    @Transactional
    public void update(String text, Long id, UserVO user) {
        EventComment comment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION + id));
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setText(text);
        eventCommentRepo.save(comment);
    }

    @Override
    public void deleteById(Long id, UserVO user) {
        EventComment comment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION + id));
        if (user.getRole() != Role.ROLE_ADMIN || !user.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        if (comment.getComments() != null) {
            comment.getComments().forEach(c -> c.setDeleted(true));
        }
        comment.setDeleted(true);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation
                .ratingCalculation(RatingCalculationEnum.ADD_COMMENT, user, accessToken));
        eventCommentRepo.save(comment);
    }
}
