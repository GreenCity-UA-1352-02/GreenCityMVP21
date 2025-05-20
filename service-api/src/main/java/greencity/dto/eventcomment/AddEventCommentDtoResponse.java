package greencity.dto.eventcomment;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEventCommentDtoResponse {
    private Long id;
    private String text;
    private EventCommentAuthorDto author;
    private LocalDateTime modifiedDate;
}
