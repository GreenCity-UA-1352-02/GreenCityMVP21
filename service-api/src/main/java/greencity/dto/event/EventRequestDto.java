package greencity.dto.event;

import greencity.annotations.EventTimeValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@EventTimeValidation
public class EventRequestDto {
    @NotBlank
    @Size(max = 70)
    private String title;

    @NotBlank
    @Size(min = 20, max = 63206)
    private String description;

    @NotBlank
    private String visability;

    @NotEmpty
    private List<@Valid EventDateDetailsDto> event;

    private ZonedDateTime createdAt;

    private Long authorId;

    private List<String> tag;

    @Size(max = 5)
    private List<EventImageDto> images;

    private String mainImage;
}
