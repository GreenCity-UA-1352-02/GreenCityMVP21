package greencity.dto.event;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

@Builder
public record AddressDto(
    Long id,
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") Double latitude,
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") Double longitude) {
}
