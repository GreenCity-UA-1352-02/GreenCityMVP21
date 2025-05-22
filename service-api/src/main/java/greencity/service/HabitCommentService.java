package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.comment.AddHabitCommentDtoRequest;
import greencity.dto.habit.comment.AddHabitCommentDtoResponse;
import greencity.dto.habit.comment.HabitAmountCommentLikesDto;
import greencity.dto.habit.comment.HabitCommentDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing comments related to Habits. This interface
 * provides methods for adding, retrieving, updating, liking, and deleting
 * comments and their replies, as well as counting comments and replies.
 */
public interface HabitCommentService {
    /**
     * Saves a new comment for a specified habit in the given language with details provided
     * in the request and associates the action with a specific user.
     *
     * @param habitId the unique identifier of the habit for which the comment is being added
     * @param languageCode the language code used for the comment (e.g., "en", "fr")
     * @param addHabitCommentDtoRequest the request object containing the details of the comment to be added
     * @param userVO the user adding the comment
     * @return an AddHabitCommentDtoResponse object containing details of the saved comment
     */
    AddHabitCommentDtoResponse save(Long habitId, String languageCode, AddHabitCommentDtoRequest addHabitCommentDtoRequest, UserVO userVO);

    /**
     * Retrieves a paginated list of habit comments based on the provided parameters.
     *
     * @param pageable      object that contains pagination information.
     * @param user          the user requesting the comments.
     * @param habitId       the ID of the habit for which comments are to be retrieved.
     * @param languageCode  the language code for localizing the comments.
     * @return a paginated list of {@code HabitCommentDto} objects wrapped in {@code PageableDto}.
     */
    PageableDto<HabitCommentDto> findAllComments(Pageable pageable, UserVO user, Long habitId, String languageCode);

    /**
     * Retrieves a pageable list of replies to a specific parent comment.
     *
     * @param pageable        the pagination and sorting information
     * @param parentCommentId the ID of the parent comment for which replies are to be retrieved
     * @param user            the current user requesting the replies
     * @return a pageable DTO containing a list of habit comment DTOs representing the replies,
     *         along with pagination details
     */
    PageableDto<HabitCommentDto> findAllReplies(Pageable pageable, Long parentCommentId, UserVO user);

    /**
     * Deletes a comment or reply identified by its unique ID, ensuring the action
     * is authorized by the provided user.
     *
     * @param id   the unique identifier of the comment or reply to be deleted
     * @param user the user performing the deletion operation
     */
    void deleteById(Long id, UserVO user);

    /**
     * Updates the text of a habit comment or reply based on the provided ID.
     * This method requires authentication and ensures that the user making
     * the request has the appropriate permissions to update the comment.
     *
     * @param text the new text to update the comment or reply with
     * @param id the ID of the comment or reply to update
     * @param user the authenticated user making the update request
     */
    void update(String text, Long id, UserVO user);

    /**
     * Adds a like to a comment identified by its ID for the specified user.
     *
     * @param id   the ID of the comment to be liked.
     * @param user the user who is liking the comment.
     */
    void like(Long id, UserVO user, String languageCode);

    /**
     * Counts the number of likes for a given habit comment using the provided data.
     *
     * @param amountCommentLikesDto the data transfer object containing information
     *                              about the habit comment, including its ID, the
     *                              number of likes, the user ID, and whether it is
     *                              liked or not.
     */
    void countLikes(HabitAmountCommentLikesDto amountCommentLikesDto);

    /**
     * Counts the number of replies to a comment based on the given comment ID.
     *
     * @param id the identifier of the parent comment whose replies are to be counted.
     * @return the total number of replies to the specified comment.
     */
    int countReplies(Long id);

    /**
     * Counts the total number of comments associated with a given habit.
     *
     * @param habitId the unique identifier of the habit for which comments are to be counted
     * @return the total count of comments for the specified habit
     */
    int countOfComments(Long habitId);

    /**
     * Retrieves a pageable list of all active comments for a specific habit.
     *
     * @param pageable {@link Pageable} object specifying pagination and sorting information.
     * @param user     {@link UserVO} object representing the current authenticated user.
     * @param habitId  {@link Long} representing the ID of the habit for which active comments are being retrieved.
     * @return {@link PageableDto} containing a list of {@link HabitCommentDto} with details about the
     *         active comments, along with pagination information.
     */
    PageableDto<HabitCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long habitId);

    /**
     * Retrieves a pageable list of all active replies to a parent comment by its ID.
     *
     * @param pageable the pagination information including page number and size.
     * @param parentCommentId the ID of the parent comment for which active replies are requested.
     * @param user the user requesting the replies, used for determining user-specific data (e.g., likes).
     * @return a {@code PageableDto<HabitCommentDto>} containing a list of active replies,
     *         total number of elements, current page, and total pages.
     */
    PageableDto<HabitCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO user);
}
