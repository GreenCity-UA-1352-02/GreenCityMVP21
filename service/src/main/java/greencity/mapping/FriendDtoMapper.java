package greencity.mapping;

import greencity.dto.friend.FriendDto;
import greencity.entity.User;

public class FriendDtoMapper {
    public static FriendDto toDto(User user) {
        return new FriendDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCity(),
            user.getProfilePicturePath());
    }
}
