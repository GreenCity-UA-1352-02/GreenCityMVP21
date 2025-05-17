package greencity.dto.eventcomment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AddEventCommentDtoResponse(
    @NotNull
    @Min(1)
    Long id,
    @NotEmpty
    String text,
    @NotEmpty
    EventCommentAuthorDto author,
    @NotEmpty
    LocalDateTime modifiedDate
){
}
