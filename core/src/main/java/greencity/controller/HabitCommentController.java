package greencity.controller;

import greencity.annotations.*;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.habit.comment.AddHabitCommentDtoRequest;
import greencity.dto.habit.comment.AddHabitCommentDtoResponse;
import greencity.dto.habit.comment.HabitAmountCommentLikesDto;
import greencity.dto.habit.comment.HabitCommentDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/comments")
public class HabitCommentController {
    private final HabitCommentService habitCommentService;

    /**
     * Saves a new comment for the specified habit.
     *
     * @param habitId the ID of the habit to which the comment is being added
     * @param request the DTO containing details of the comment to be added
     * @param user    the user who is adding the comment
     * @return a {@link ResponseEntity} containing the response DTO with details of the saved comment
     */
    @Operation(summary = "Add comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEcoNewsCommentDtoResponse.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("{habitId}")
    @ApiLocale
    public ResponseEntity<AddHabitCommentDtoResponse> save(@PathVariable Long habitId,
                                                           @ValidLanguage Locale locale,
                                                           @Valid @RequestBody AddHabitCommentDtoRequest request,
                                                           @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitCommentService.save(habitId, locale.getLanguage(), request, user));
    }

    /**
     * Retrieves the total count of comments for a specific habit.
     *
     * @param habitId the ID of the habit for which the count of comments is to be retrieved
     * @return the total number of comments associated with the specified habit
     */
    @Operation(summary = "Get count of comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/count/comments/{habitId}")
    public int getCountOfComments(@PathVariable Long habitId) {
        return habitCommentService.countOfComments(habitId);
    }

    /**
     * Retrieves all replies for a given parent comment.
     *
     * @param pageable        the paging and sorting information.
     * @param parentCommentId the ID of the parent comment for which replies are being retrieved.
     * @param user            the currently authenticated user.
     * @return a ResponseEntity containing a pageable list of habit comment DTOs.
     */
    @Operation(summary = "Find all replies")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("replies/{parentCommentId}")
    @ApiPageable
    public ResponseEntity<PageableDto<HabitCommentDto>> findAllReplies(@Parameter(hidden = true) Pageable pageable,
                                                                       @PathVariable Long parentCommentId,
                                                                       @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(habitCommentService.findAllReplies(pageable, parentCommentId, user));
    }

    /**
     * Retrieves the count of replies for a given parent comment.
     *
     * @param parentCommentID the ID of the parent comment for which the count of replies is to be retrieved
     * @return the total number of replies associated with the specified parent comment
     */
    @Operation(summary = "Get count of comment replies")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("count/replies/{parentCommentID}")
    public int getCountOfReplies(@PathVariable Long parentCommentID) {
        return habitCommentService.countReplies(parentCommentID);
    }

    /**
     * Marks a habit comment as deleted by its ID.
     *
     * @param id   the ID of the habit comment to be deleted
     * @param user the user performing the delete operation
     * @return a ResponseEntity indicating the result of the operation
     */
    @Operation(summary = "Mark comment as deleted by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("")
    public ResponseEntity<Object> delete(Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        habitCommentService.deleteById(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates an existing comment.
     *
     * @param id   the unique identifier of the comment to be updated.
     * @param text the new text of the comment; must not be blank.
     * @param user the current user initiating the update request.
     */
    @Operation(summary = "Update comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("")
    public void update(Long id, @RequestParam @NotBlank String text, @Parameter(hidden = true) @CurrentUser UserVO user) {
        habitCommentService.update(text, id, user);
    }

    /**
     * Handles the request to like a comment. This method allows a user to like a specific comment
     * identified by its ID.
     *
     * @param id   the ID of the comment to be liked
     * @param user the current user performing the operation, obtained from the session or token
     */
    @Operation(summary = "Like comment")
    @PostMapping("like")
    @ApiLocale
    public void like(@RequestParam("id") Long id
            , @Parameter(hidden = true) @CurrentUser UserVO user
            , @Parameter(hidden = true) @ValidLanguage Locale locale) {
        habitCommentService.like(id, user, locale.getLanguage());
    }

    /**
     * Handles a message to process "like" action and count likes for a specific habit comment.
     *
     * @param habitAmountCommentLikesDto the data transfer object containing information about the habit comment
     *                                   and the like action to be processed
     */
    @MessageMapping("likeAndCountHabit")
    public void likeAndCount(@Payload HabitAmountCommentLikesDto habitAmountCommentLikesDto) {
        habitCommentService.countLikes(habitAmountCommentLikesDto);
    }

    /**
     * Retrieves all active replies associated with a specific comment.
     *
     * @param pageable Pageable object for pagination information.
     * @param user     The current authenticated user performing the action.
     * @param id       The ID of the parent comment for which active replies are being retrieved.
     * @return ResponseEntity containing a PageableDto of HabitCommentDto objects representing active replies.
     */
    @Operation(summary = "Get all active replies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("active/{commentId}")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<HabitCommentDto>> getCountActiveReplies(@Parameter(hidden = true) Pageable pageable,
                                                                              @Parameter(hidden = true) @CurrentUser UserVO user,
                                                                              @RequestParam("id") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(habitCommentService.findAllActiveReplies(pageable, id, user));
    }
}
