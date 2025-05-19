package greencity.service;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import jakarta.validation.constraints.NotBlank;

public interface EventCommentService {
    /**
     * This method adds a comment to the event.
     *
     * @param eventId the id of the event
     * @param comment the comment that is added
     * @param user    the owner of the comment
     * @return {@link AddEventCommentDtoResponse} instance
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user);

    /**
     * Method to change the existing comment.
     *
     * @param text new text of the comment
     * @param id id of the comment that user wants to change
     * @param user current user {@link  UserVO} that want to change comment
     */
    void update(@NotBlank String text, Long id, UserVO user);

    /**
     * Method to mark comment ad deleted.
     *
     * @param id id of the comment
     * @param user current user {@link  UserVO} that want to delete comment
     */
    void deleteById(Long id, UserVO user);
}
