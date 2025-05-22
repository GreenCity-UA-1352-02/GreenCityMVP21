package greencity.dto.habit.comment;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserVO;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentComment", "comments", "usersLiked"})
@ToString(exclude = {"parentComment", "comments", "usersLiked"})
public class HabitCommentVO {
    private Long id;

    @Size(min = 1, max = 8000)
    private String text;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private HabitCommentVO parentComment;

    private List<HabitCommentVO> comments = new ArrayList<>();

    private UserVO user;

    private HabitVO habitVO;

    private boolean deleted;

    private boolean currentUserLiked = false;

    private Set<UserVO> usersLiked = new HashSet<>();
}
