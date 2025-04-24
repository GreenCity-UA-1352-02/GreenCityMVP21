package greencity.repository;

import greencity.dto.user.FriendDto;
import greencity.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT new greencity.dto.user.FriendDto"
        + "(f.friend.id, f.friend.name, f.friend.email, f.friend.profilePicturePath)"
        + " " + "FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    List<FriendDto> findAllFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    Friend findByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    void delete(Friend friend);
}
