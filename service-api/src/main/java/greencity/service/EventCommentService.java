package greencity.service;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

public interface EventCommentService {
    /**
     * This method adds a comment to the event.
     *
     * @param eventId the id of the event
     * @param comment the comment that is added
     * @param user    the owner of the comment
     * @return {@link AddEventCommentDtoResponse} instance
     * @author Pohranychnyi Olexandr
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user);
}
