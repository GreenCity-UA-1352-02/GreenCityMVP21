package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "habit_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"parentComment", "comments", "usersLiked"})
@EntityListeners(AuditingEntityListener.class)
public class HabitComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    private HabitComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.ALL})
    private List<HabitComment> comments = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private Habit habit;

    @Column
    private boolean deleted;

    @Transient
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
            name = "habit_comment_users_liked",
            joinColumns = @JoinColumn(name = "habit_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "users_liked_id"))
    private Set<User> usersLiked = new HashSet<>();
    ;
}
