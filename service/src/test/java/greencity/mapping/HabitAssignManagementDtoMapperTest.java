package greencity.mapping;

import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class HabitAssignManagementDtoMapperTest {

    @InjectMocks
    private HabitAssignManagementDtoMapper mapper;

    @Test
    void convert_FromHabitAssignToHabitAssignManagementDto_FullData() {

        User user = User.builder().id(1L).build();
        Habit habit = Habit.builder().id(2L).build();
        ZonedDateTime createDateTime = ZonedDateTime.of(LocalDateTime.now().minusDays(5), ZoneId.of("Europe/Kyiv"));
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.of(LocalDateTime.now().minusDays(1), ZoneId.of("Europe/Kyiv"));

        HabitAssign habitAssign = HabitAssign.builder()
            .id(5L)
            .status(HabitAssignStatus.INPROGRESS)
            .createDate(createDateTime)
            .user(user)
            .habit(habit)
            .duration(30)
            .workingDays(10)
            .habitStreak(5)
            .lastEnrollmentDate(lastEnrollmentDate)
            .progressNotificationHasDisplayed(false)
            .build();

        HabitAssignManagementDto habitAssignManagementDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignManagementDto);
        assertEquals(habitAssign.getId(), habitAssignManagementDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignManagementDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignManagementDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignManagementDto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), habitAssignManagementDto.getHabitId());
        assertEquals(habitAssign.getDuration(), habitAssignManagementDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignManagementDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignManagementDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignManagementDto.getLastEnrollment());
    }

    @Test
    void convert_FromHabitAssignToHabitAssignManagementDto_MinimalData() {

        User user = User.builder().id(2L).build();
        Habit habit = Habit.builder().id(3L).build();
        ZonedDateTime createDateTime = ZonedDateTime.now();

        HabitAssign habitAssign = HabitAssign.builder()
            .id(6L)
            .status(HabitAssignStatus.ACTIVE)
            .createDate(createDateTime)
            .user(user)
            .habit(habit)
            .duration(21)
            .workingDays(0)
            .habitStreak(0)
            .lastEnrollmentDate(createDateTime)
            .progressNotificationHasDisplayed(false)
            .build();

        HabitAssignManagementDto habitAssignManagementDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignManagementDto);
        assertEquals(habitAssign.getId(), habitAssignManagementDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignManagementDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignManagementDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignManagementDto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), habitAssignManagementDto.getHabitId());
        assertEquals(habitAssign.getDuration(), habitAssignManagementDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignManagementDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignManagementDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignManagementDto.getLastEnrollment());
    }

    @Test
    void convert_FromHabitAssignToHabitAssignManagementDto_WithDifferentStatus() {

        User user = User.builder().id(4L).build();
        Habit habit = Habit.builder().id(5L).build();
        ZonedDateTime createDateTime = ZonedDateTime.now().minusDays(10);
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.now().minusDays(2);

        HabitAssign habitAssign = HabitAssign.builder()
            .id(8L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(createDateTime)
            .user(user)
            .habit(habit)
            .duration(90)
            .workingDays(60)
            .habitStreak(90)
            .lastEnrollmentDate(lastEnrollmentDate)
            .progressNotificationHasDisplayed(true)
            .build();

        HabitAssignManagementDto habitAssignManagementDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignManagementDto);
        assertEquals(habitAssign.getId(), habitAssignManagementDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignManagementDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignManagementDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignManagementDto.getUserId());
        assertEquals(habitAssign.getHabit().getId(), habitAssignManagementDto.getHabitId());
        assertEquals(habitAssign.getDuration(), habitAssignManagementDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignManagementDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignManagementDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignManagementDto.getLastEnrollment());
    }
}
