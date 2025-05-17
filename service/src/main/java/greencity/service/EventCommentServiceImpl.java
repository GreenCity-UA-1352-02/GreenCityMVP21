package greencity.service;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.repository.EventCommentRepository;
import greencity.repository.EventRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepository eventCommentRepo;
    private EventService eventService;
    private EventRepo eventRepo;
    private ModelMapper modelMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user) {
        return null;
    }
}
