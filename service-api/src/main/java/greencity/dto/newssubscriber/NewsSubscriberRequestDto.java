package greencity.dto.newssubscriber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewsSubscriberRequestDto(
    @Email @NotBlank String email) {
}
