package greencity.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {
    private Long id;
    private String name;
    private String email;
    private String profilePicture;
    private String city;
}
