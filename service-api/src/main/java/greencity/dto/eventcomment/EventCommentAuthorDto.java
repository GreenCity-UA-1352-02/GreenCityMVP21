package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EventCommentAuthorDto(
    @NotNull
    Long id,
    @NotEmpty
    String name,
    String userProfilePicturePath
) {
}
