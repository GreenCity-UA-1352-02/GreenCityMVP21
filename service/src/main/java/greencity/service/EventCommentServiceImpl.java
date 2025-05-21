package greencity.service;

import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.event.EventComment;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentRepository;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
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
    private UserRepo userRepo;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user) {
        EventComment eventComment = modelMapper.map(comment, EventComment.class);
        eventComment.setUser(userRepo.getReferenceById(user.getId()));
        eventComment.setEvent(eventRepo.getReferenceById(eventId));
        if (comment.parentCommentId() != null) {
            boolean parentIsTopLevel = eventCommentRepo.existsByIdAndParentCommentIsNull(comment.parentCommentId());
            if (!parentIsTopLevel) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
            EventComment parent = eventCommentRepo.getReferenceById(comment.parentCommentId());
            if (!parent.getEvent().getId().equals(eventId)) {
                throw new BadRequestException(ErrorMessage.PARENT_COMMENT_NOT_FROM_EVENT);
            }
            eventComment.setParentComment(parent);
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
    @Transactional
    public void deleteById(Long id, UserVO user) {
        EventComment comment = eventCommentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION + id));
        if (comment.getComments() != null) {
            comment.getComments().forEach(c -> c.setDeleted(true));
        }
        comment.setDeleted(true);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation
                .ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, accessToken));
        eventCommentRepo.save(comment);
    }

    @Override
    public boolean isCommentOwner(Long commentId, String username) {
        Optional<EventComment> commentOpt = eventCommentRepo.findById(commentId);
        if (commentOpt.isEmpty()) {
            return false;
        }
        EventComment comment = commentOpt.get();
        String currentUserEmail = username;
        return comment.getUser().getEmail().equals(currentUserEmail);
    }
}
