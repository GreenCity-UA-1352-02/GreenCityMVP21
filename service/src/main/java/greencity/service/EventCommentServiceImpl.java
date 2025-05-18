package greencity.service;

import static greencity.constant.AppConstant.AUTHORIZATION;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventCommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepository eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user) {
        EventVO eventVO = eventService.findById(eventId);
        EventComment eventComment = modelMapper.map(comment, EventComment.class);
        eventComment.setUser(modelMapper.map(user, User.class));
        eventComment.setEvent(modelMapper.map(eventVO, Event.class));
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
}
