package greencity.dto.friend;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendCardDto {
    private Long id;
    private String name;
    private String city;
    private String profilePicture;
    private Double rating;
    private Long friendCount; // Должен быть Long, а не int
    private Boolean isFriend; // Должен быть Boolean, а не boolean

    public FriendCardDto(Long id, String name, String city, String profilePicture, Double rating, Long friendCount,
                         Boolean isFriend) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.profilePicture = profilePicture;
        this.rating = rating;
        this.friendCount = friendCount;
        this.isFriend = isFriend;
    }
}
