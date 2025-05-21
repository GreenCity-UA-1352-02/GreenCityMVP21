package greencity.dto.habit;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.user.UserVO;
import lombok.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitVO {
    private Long id;
    private String image;
    private String name;
    @Min(value = 1, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    @Max(value = 3, message = ServiceValidationConstants.HABIT_COMPLEXITY)
    private Integer complexity;
    private Integer defaultDuration;
    private boolean currentUserLiked = false;
    private Long userId;
    private boolean isCustomHabit;
    private Set<UserVO> usersLiked = new HashSet<>();
}
