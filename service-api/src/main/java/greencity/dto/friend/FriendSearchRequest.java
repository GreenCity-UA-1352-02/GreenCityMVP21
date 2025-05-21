package greencity.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FriendSearchRequest {
    private Long userId;
    private String searchTerm;
    private Boolean filterByCity;
    private Boolean filterByMutualFriends;
    private String city;
    private Long friendId;
}
