package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventImageDto {
    @NotBlank
    private String imageUrl;
}
