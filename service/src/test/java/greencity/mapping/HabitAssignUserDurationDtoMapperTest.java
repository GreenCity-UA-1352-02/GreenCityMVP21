package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class HabitAssignUserDurationDtoMapperTest {

    @InjectMocks
    private HabitAssignUserDurationDtoMapper mapper;

    @Test
    void convert_FromHabitAssignToHabitAssignUserDurationDto_FullData() {

        User user = User.builder().id(1L).build();
        Habit habit = Habit.builder().id(2L).build();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(5L)
                .user(user)
                .habit(habit)
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(10)
                .duration(30)
                .build();

        HabitAssignUserDurationDto dto = mapper.convert(habitAssign);

        assertNotNull(dto);
        assertEquals(habitAssign.getId(), dto.getHabitAssignId());
        assertEquals(habitAssign.getUser().getId(), dto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), dto.getHabitId());
        assertEquals(habitAssign.getStatus(), dto.getStatus());
        assertEquals(habitAssign.getWorkingDays(), dto.getWorkingDays());
        assertEquals(habitAssign.getDuration(), dto.getDuration());
    }

    @Test
    void convert_FromHabitAssignToHabitAssignUserDurationDto_MinimalData() {

        User user = User.builder().id(3L).build();
        Habit habit = Habit.builder().id(4L).build();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(6L)
                .user(user)
                .habit(habit)
                .status(HabitAssignStatus.ACTIVE)
                .workingDays(0)
                .duration(21)
                .build();

        HabitAssignUserDurationDto dto = mapper.convert(habitAssign);

        assertNotNull(dto);
        assertEquals(habitAssign.getId(), dto.getHabitAssignId());
        assertEquals(habitAssign.getUser().getId(), dto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), dto.getHabitId());
        assertEquals(habitAssign.getStatus(), dto.getStatus());
        assertEquals(habitAssign.getWorkingDays(), dto.getWorkingDays());
        assertEquals(habitAssign.getDuration(), dto.getDuration());
    }

    @Test
    void convert_FromHabitAssignToHabitAssignUserDurationDto_DifferentStatusAndValues() {

        User user = User.builder().id(7L).build();
        Habit habit = Habit.builder().id(8L).build();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(9L)
                .user(user)
                .habit(habit)
                .status(HabitAssignStatus.ACQUIRED)
                .workingDays(30)
                .duration(90)
                .build();

        HabitAssignUserDurationDto dto = mapper.convert(habitAssign);

        assertNotNull(dto);
        assertEquals(habitAssign.getId(), dto.getHabitAssignId());
        assertEquals(habitAssign.getUser().getId(), dto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), dto.getHabitId());
        assertEquals(habitAssign.getStatus(), dto.getStatus());
        assertEquals(habitAssign.getWorkingDays(), dto.getWorkingDays());
        assertEquals(habitAssign.getDuration(), dto.getDuration());
    }
}
