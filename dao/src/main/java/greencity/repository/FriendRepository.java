package greencity.repository;

import greencity.dto.friend.FriendDto;
import greencity.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT new greencity.dto.friend.FriendDto"
        + "(f.friend.id, f.friend.name, f.friend.email, f.friend.profilePicturePath, f.friend.city)"
        + " " + "FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    List<FriendDto> findAllFriendsByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE f.user.id = :userId AND f.friend.id = :friendId")
    Friend findByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    void delete(Friend friend);

    @Query("SELECT COUNT(f) FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE f.user.id = :userId AND "
        + "f.friend.id = :friendId AND f.status = 'FRIEND'")
    Boolean existsByUserIdAndFriendIdAndStatus(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
