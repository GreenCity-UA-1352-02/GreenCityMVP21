package greencity.service;

import greencity.dto.friend.FriendCardDto;
import greencity.dto.friend.FriendDto;
import greencity.dto.friend.FriendSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FriendService {
    List<FriendDto> getFriends(Long userId);

    void addFriend(Long userId, Long friendId);

    void confirmFriend(Long requesterId);

    Page<FriendCardDto> searchNewFriends(FriendSearchRequest request, Pageable pageable);

    void declineFriend(Long friendId);

    Long getCurrentUserId();

    void removeFriend(Long friendId);

    void cancelFriendRequest(Long friendId);

    void blockUser(Long toBlockId);
}
