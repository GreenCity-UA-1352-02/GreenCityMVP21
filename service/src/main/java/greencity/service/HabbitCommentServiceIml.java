package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.comment.AddHabitCommentDtoRequest;
import greencity.dto.habit.comment.AddHabitCommentDtoResponse;
import greencity.dto.habit.comment.HabitAmountCommentLikesDto;
import greencity.dto.habit.comment.HabitCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitComment;
import greencity.entity.User;
import greencity.enums.NotificationObjectType;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.rating.RatingCalculation;
import greencity.repository.HabitCommentRepo;
import greencity.repository.HabitRepo;
import greencity.repository.NotificationPayloadRepo;
import greencity.repository.NotificationRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static greencity.constant.AppConstant.AUTHORIZATION;

/**
 * Service implementation for managing habit comments, including functionalities
 * for retrieving, updating, deleting, liking, and counting comments or replies.
 * This class facilitates interaction with the database via repositories and handles
 * business logic related to habit comments.
 */
@Service
@AllArgsConstructor
public class HabbitCommentServiceIml implements HabitCommentService {

    private final HabitService habitService;
    private final HabitCommentRepo habitCommentRepo;
    private final ModelMapper modelMapper;
    private final HttpServletRequest httpServletRequest;
    private final RatingCalculation ratingCalculation;
    private final SimpMessagingTemplate messagingTemplate;
    private final HabitRepo habitRepo;
    private final NotificationProducerServiceImpl notificationProducerService;
    private final NotificationPayloadRepo notificationPayloadRepo;
    private final NotificationRepo notificationRepo;


    @Override
    public AddHabitCommentDtoResponse save(Long habitId, String langCode, AddHabitCommentDtoRequest addHabitCommentDtoRequest, UserVO userVO) {
        HabitDto habitDto = habitService.getByIdAndLanguageCode(habitId, langCode);
        HabitComment habitComment = modelMapper.map(addHabitCommentDtoRequest, HabitComment.class);
        habitComment.setUser(modelMapper.map(userVO, User.class));
        habitComment.setHabit(modelMapper.map(habitDto, Habit.class));
        if (addHabitCommentDtoRequest.getParentCommentId() != 0L) {
            HabitComment parentComment = habitCommentRepo.findById(addHabitCommentDtoRequest.getParentCommentId())
                    .orElseThrow(() -> new BadRequestException(ErrorMessage.PARENT_COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                habitComment.setParentComment(parentComment);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));

        HabitComment savedComment = habitCommentRepo.save(habitComment);

        if (addHabitCommentDtoRequest.getParentCommentId() == null || addHabitCommentDtoRequest.getParentCommentId() == 0 && habitDto.getIsCustomHabit()) {
            Long authorId = habitDto.getUsersIdWhoCreatedCustomHabit();
            if (!authorId.equals(userVO.getId())) {
                notificationProducerService.sendCommentNotification(
                        savedComment.getHabit().getId(),
                        habitDto.getHabitTranslation().getName(),
                        habitDto.getUsersIdWhoCreatedCustomHabit(),
                        savedComment.getUser().getId(),
                        savedComment.getUser().getName(),
                        String.valueOf(NotificationObjectType.HABIT_COMMENT)
                );
            }
        } else {
            HabitComment parentComment = savedComment.getParentComment();
            Long parentCommentAuthorId = parentComment.getUser().getId();
            if (!parentCommentAuthorId.equals(userVO.getId())) {
                notificationProducerService.sendCommentReplyNotification(
                        savedComment.getId(),
                        habitDto.getHabitTranslation().getName(),
                        String.valueOf(NotificationObjectType.HABIT_REPLY),
                        parentCommentAuthorId,
                        userVO.getId(),
                        userVO.getName()
                );
            }
        }
        return modelMapper.map(savedComment, AddHabitCommentDtoResponse.class);
    }

    /**
     * Retrieves all comments for a specific habit, ordered by creation date in descending order,
     * and maps them to a pageable DTO containing habit comment details.
     * The method also checks if the current user has liked each comment and counts
     * the number of replies for each comment.
     *
     * @param pageable     the pagination information including the page number, size, and sorting
     * @param userVO       the user information making the request
     * @param habitId      the ID of the habit associated with the comments
     * @param languageCode the language code to retrieve the habit in the specified language
     * @return a pageable DTO containing a list of HabitCommentDto, total elements count,
     * current page number, and total pages
     */
    @Override
    public PageableDto<HabitCommentDto> findAllComments(Pageable pageable, UserVO userVO, Long habitId, String languageCode) {
        habitService.getByIdAndLanguageCode(habitId, languageCode);
        Page<HabitComment> pages = habitCommentRepo.findAllByParentCommentIsNullAndHabitIdOrderByCreatedDateDesc(pageable, habitId);
        List<HabitCommentDto> habitCommentDtos = pages
                .stream()
                .map(comment -> {
                    comment.setCurrentUserLiked(comment.getUsersLiked().stream()
                            .anyMatch(user -> user.getId().equals(userVO.getId())));
                    return comment;
                })
                .map(comment -> modelMapper.map(comment, HabitCommentDto.class))
                .map(comment -> {
                    comment.setReplies(habitCommentRepo.countByParentCommentId(comment.getId()));
                    return comment;
                })
                .toList();
        return new PageableDto<>(
                habitCommentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages()
        );
    }

    /**
     * Retrieves all replies to a specific parent comment and maps them to a pageable DTO
     * containing habit comment details. Replies are ordered by creation date in descending order.
     *
     * @param pageable        the pagination information including the page number, size, and sorting
     * @param parentCommentId the ID of the parent comment for which replies are being retrieved
     * @param user            the user making the request
     * @return a pageable DTO containing a list of HabitCommentDto, total elements count,
     * current page number, and total pages
     */
    @Override
    public PageableDto<HabitCommentDto> findAllReplies(Pageable pageable, Long parentCommentId, UserVO user) {
        Page<HabitComment> pages = habitCommentRepo.findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId);
        List<HabitCommentDto> habitCommentDtos = pages
                .stream()
                .map(habitComment -> modelMapper.map(habitComment, HabitCommentDto.class))
                .map(comment -> {
                    comment.setReplies(habitCommentRepo.countByParentCommentId(comment.getId()));
                    return comment;
                })
                .toList();

        return new PageableDto<>(
                habitCommentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages()
        );
    }

    /**
     * Deletes a habit comment by its ID, ensuring that only authorized users (admins
     * or the comment owner) can perform this action. Marks the specified comment and
     * its replies as deleted, and asynchronously triggers rating recalculation.
     *
     * @param id   the ID of the habit comment to delete
     * @param user the user requesting the deletion
     * @throws NotFoundException                    if the habit comment is not found
     * @throws UserHasNoPermissionToAccessException if the user does not have sufficient permissions
     */
    @Override
    public void deleteById(Long id, UserVO user) {
        HabitComment habitComment = habitCommentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(habitComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (habitComment.getComments() != null) {
            habitComment.getComments().forEach(com -> com.setDeleted(true));
        }
        habitComment.setDeleted(true);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
                () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.DELETE_COMMENT, user, accessToken));
        habitCommentRepo.save(habitComment);
    }

    /**
     * Updates the text of an existing habit comment.
     * Ensures that only the author of the comment can update it.
     *
     * @param text the new text to update the comment with
     * @param id   the ID of the comment to be updated
     * @param user the user attempting to update the comment
     * @throws NotFoundException   if the comment with the specified ID is not found
     * @throws BadRequestException if the user attempting the update is not the author of the comment
     */
    @Override
    public void update(String text, Long id, UserVO user) {
        HabitComment comment = habitCommentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new BadRequestException(ErrorMessage.NOT_A_CURRENT_USER);
        }
        comment.setText(text);
        habitCommentRepo.save(comment);
    }

    /**
     * Handles the "like" action on a habit comment. If the user has already liked the comment,
     * this method will call the "unlike" action. Otherwise, it adds the user's like to the comment
     * and triggers necessary notifications or rating calculations.
     *
     * @param id   the unique identifier of the comment to like
     * @param user the user who performs the like action
     */
    @Override
    public void like(Long id, UserVO user, String langCode) {
        HabitComment comment = habitCommentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        Long commentAuthorId = comment.getUser().getId();
        if (comment.getUsersLiked().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            unlike(user, comment);
            if (!commentAuthorId.equals(user.getId())) {
                Long notificationId = notificationPayloadRepo.findByArticleIdAndObjectType(comment.getHabit().getId(), String.valueOf(NotificationObjectType.HABIT_COMMENT_LIKE)).get().getId();
                notificationRepo.deleteById(notificationId);
            }
        } else {
            comment.getUsersLiked().add(modelMapper.map(user, User.class));
            habitCommentRepo.save(comment);
            String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
            CompletableFuture
                    .runAsync(() -> ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT, user, accessToken));
            if (!commentAuthorId.equals(user.getId())) {
                Long articleId = comment.getHabit().getId();

                String habitName = comment.getHabit().getHabitTranslations().stream()
                        .filter(x -> x.getLanguage().getCode().equals(langCode))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.INVALID_LANGUAGE_CODE))
                        .getName();

                notificationProducerService.sendCommentLikeNotification(
                        articleId,
                        habitName,
                        String.valueOf(NotificationObjectType.HABIT_COMMENT_LIKE),
                        commentAuthorId,
                        user.getId(),
                        user.getName());
            }
        }
    }

    /**
     * Removes a user's like from a specific habit comment and asynchronously updates the rating.
     *
     * @param user    the user who is unliking the comment
     * @param comment the habit comment from which the like is being removed
     */
    private void unlike(UserVO user, HabitComment comment) {
        comment.getUsersLiked().removeIf(u -> u.getId().equals(user.getId()));
        habitCommentRepo.save(comment);
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture
                .runAsync(() -> ratingCalculation.ratingCalculation(RatingCalculationEnum.LIKE_COMMENT, user, accessToken));
    }

    /**
     * Updates the like status and like count for a given comment and sends update notifications via WebSocket.
     *
     * @param amountCommentLikesDto An object containing the comment ID and the user ID to check if the user has liked the comment.
     *                              It also stores the updated like status and like count after processing.
     */
    @Override
    public void countLikes(HabitAmountCommentLikesDto amountCommentLikesDto) {
        HabitComment comment = habitCommentRepo.findById(amountCommentLikesDto.getId())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        boolean isLiked = comment.getUsersLiked().stream().map(User::getId)
                .anyMatch(u -> u.equals(amountCommentLikesDto.getUserId()));
        amountCommentLikesDto.setLiked(isLiked);
        int size = comment.getUsersLiked().size();
        amountCommentLikesDto.setAmountLikes(size);
        messagingTemplate
                .convertAndSend("/topic/" + amountCommentLikesDto.getId() + "/comment/" + amountCommentLikesDto.getAmountLikes());
    }

    /**
     * Counts the number of replies for a given comment identified by its ID.
     *
     * @param id the unique identifier of the parent comment for which replies are to be counted
     * @return the number of replies associated with the given parent comment ID
     * @throws BadRequestException if the comment with the given ID is not found
     */
    @Override
    public int countReplies(Long id) {
        if (habitRepo.findById(id).isEmpty()) {
            throw new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION);
        }
        return habitCommentRepo.countByParentCommentId(id);
    }

    /**
     * Counts the number of comments associated with a specific habit.
     *
     * @param habitId the unique identifier of the habit whose comments are to be counted
     * @return the total count of comments for the specified habit
     * @throws NotFoundException if the habit with the given ID is not found
     */
    @Override
    public int countOfComments(Long habitId) {
        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID));
        return habitCommentRepo.countHabitCommentByHabit(habit.getId());
    }

    /**
     * Retrieves all active comments for a specific habit that are not deleted
     * and don't have a parent comment, ordered by their creation date in descending order.
     *
     * @param pageable the pagination information for the request
     * @param user     the user making the request, used to determine if the user liked a comment
     * @param habitId  the ID of the habit for which comments are to be fetched
     * @return a pageable DTO containing a list of active habit comment DTOs, total elements,
     * current page number, and total pages
     */
    @Override
    public PageableDto<HabitCommentDto> getAllActiveComments(Pageable pageable, UserVO user, Long habitId) {
        Page<HabitComment> pages =
                habitCommentRepo.findAllByParentCommentIsNullAndDeletedFalseAndHabitIdOrderByCreatedDateDesc(pageable, habitId);
        List<HabitCommentDto> habitCommentDtos = pages
                .stream()
                .map(comment -> {
                    comment.setCurrentUserLiked(comment.getUsersLiked().stream().anyMatch(u -> u.getId().equals(user.getId())));
                    return comment;
                })
                .map(comment -> modelMapper.map(comment, HabitCommentDto.class))
                .map(comment -> comment.setReplies(habitCommentRepo.countByParentCommentId(comment.getId())))
                .toList();
        return new PageableDto<>(
                habitCommentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages()
        );
    }

    /**
     * Retrieves all active replies for a given parent comment, excluding deleted ones,
     * ordered by their creation date in descending order. If a user is provided, it also
     * sets whether the current user has liked each reply.
     *
     * @param pageable        the pagination information
     * @param parentCommentId the identifier of the parent comment whose replies are to be retrieved
     * @param user            the user currently making the request, used to determine if the user liked the comments
     * @return a {@code PageableDto} containing a list of {@code HabitCommentDto} along with pagination details
     */
    @Override
    public PageableDto<HabitCommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO user) {
        Page<HabitComment> pages =
                habitCommentRepo.findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateDesc(pageable, parentCommentId);
        UserVO userVO = user == null ? UserVO.builder().build() : user;
        List<HabitCommentDto> habitCommentDtos = pages
                .stream()
                .map(comment -> {
                    comment.setCurrentUserLiked(comment.getUsersLiked().stream().anyMatch(u -> u.getId().equals(user.getId())));
                    return comment;
                })
                .map(comment -> modelMapper.map(comment, HabitCommentDto.class))
                .map(comment -> comment.setReplies(habitCommentRepo.countByParentCommentId(comment.getId())))
                .toList();
        return new PageableDto<>(
                habitCommentDtos,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages()
        );
    }
}
