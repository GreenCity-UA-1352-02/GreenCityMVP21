package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventResponse {
    private Long id;
    @Size(max = 5)
    private List<String> additionalImages;
    @Size(max = 7)
    private List<@Valid EventDateLocationDto> dates;
    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    private String description;
    private boolean open;
    private EventAuthorDto organizer;
    private List<TagUaEnDto> tags;
    @NotBlank
    @Size(max = 70, message = "Title must be no longer than 70 characters")
    private String title;
    @NotBlank
    private String titleImage;
}
