package greencity.dto.event;

import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"dateLocations", "titleImage", "images", "tags"})
@ToString(exclude = {"dateLocations", "titleImage", "images", "tags"})
public class EventVO {
    private Long id;
    private String title;
    private String description;
    @Builder.Default
    private boolean open = true;
    @Builder.Default
    private List<EventDateLocationDto> dateLocations = new ArrayList<>();
    private EventImageDto titleImage;
    @Builder.Default
    private List<EventImageDto> images = new ArrayList<>();
    private UserVO user;
    @Builder.Default
    private List<TagVO> tags = new ArrayList<>();
}
