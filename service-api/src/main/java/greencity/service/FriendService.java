package greencity.service;

import greencity.dto.user.FriendDto;
import java.util.List;

public interface FriendService {
    List<FriendDto> getFriends(Long userId);

    // Найти нового друга по имени или имени пользователя
    List<FriendDto> searchNewFriends(String searchTerm, Long currentUserId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    void confirmFriend(Long userId, Long requesterId);

    void blockUser(Long userId, Long toBlockId);
}
