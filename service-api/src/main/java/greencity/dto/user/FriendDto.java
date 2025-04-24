package greencity.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FriendDto {
    private Long id;
    private String name;
    private String email;
    private String profilePicture;

    public FriendDto() {
    }

    public FriendDto(Long id, String name, String email, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }
}
