package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HabitVOMapper extends AbstractConverter<Habit, HabitVO> {
        private final UserVOMapper userVOMapper;

            public HabitVOMapper(UserVOMapper userVOMapper) {
                this.userVOMapper = userVOMapper;
           }

    @Override
    protected HabitVO convert(Habit habit) {
        return HabitVO.builder()
                .id(habit.getId())
                .image(habit.getImage())
                .complexity(habit.getComplexity())
                .defaultDuration(habit.getDefaultDuration())
                .usersLiked(habit.getUsersLiked().stream()
                        .map(userVOMapper::convert)
                        .collect(Collectors.toSet()))
                .currentUserLiked(habit.isCurrentUserLiked())
                .userId(habit.getUserId())
                .isCustomHabit(habit.getIsCustomHabit())
                .build();
    }

}
