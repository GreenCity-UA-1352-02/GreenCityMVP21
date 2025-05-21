package greencity.dto.habit.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddHabitCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private HabitCommentAuthorDto author;

    @NotEmpty
    private String text;

    @NotEmpty
    private LocalDateTime modifiedDate;
}
