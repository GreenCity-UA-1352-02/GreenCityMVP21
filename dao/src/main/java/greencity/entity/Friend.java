package greencity.entity;

import greencity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "friend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @Column(name = "city")
    private String city;

    public Friend(User user, User friend, FriendStatus status) {
        this.user = user;
        this.friend = friend;
        this.status = status;
        this.city = friend.getCity();
    }
}
