package greencity.dto.habit.comment;

import greencity.enums.CommentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class HabitCommentDto {
    @NotNull
    @Min(1)
    private Long id;

    @NotNull
    private LocalDateTime modifiedDate;

    private HabitCommentAuthorDto author;

    private String text;

    private int replies;

    private int likes;

    private boolean currentUserLiked;

    private CommentStatus status;
}
