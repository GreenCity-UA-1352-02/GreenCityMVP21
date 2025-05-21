package greencity.dto.habit.comment;

import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.enums.CommentStatus;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty
    private LocalDateTime modifiedDate;

    private EcoNewsCommentAuthorDto author;

    private String text;

    private int replies;

    private int likes;

    private boolean currentUserLiked;

    private CommentStatus status;
}
