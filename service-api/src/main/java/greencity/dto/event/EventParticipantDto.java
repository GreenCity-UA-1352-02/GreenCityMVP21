package greencity.dto.event;

import greencity.enums.EventRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventParticipantDto {
    private Long id;
    private String title;
    private String description;
    private boolean isOpen;
    private String authorName;
    private List<String> tagNames;
    private String mainImageUrl;
    private EventRole role;
}
