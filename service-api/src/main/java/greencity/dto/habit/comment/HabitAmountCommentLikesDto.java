package greencity.dto.habit.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class HabitAmountCommentLikesDto {
    @NotEmpty
    private Long id;

    private Integer amountLikes;

    private Long userId;

    private boolean isLiked;
}
