package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AddEventCommentDtoRequest(
    @NotBlank
    @Length(min = 1, max = 8000)
    String text,
    Long parentCommentId
) {
}
