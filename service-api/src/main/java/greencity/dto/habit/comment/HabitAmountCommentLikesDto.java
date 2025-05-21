package greencity.dto.habit.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class HabitAmountCommentLikesDto {
    @NotNull
    private Long id;

    private Integer amountLikes;

    private Long userId;

    private boolean isLiked;
}
