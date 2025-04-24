package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HabitAssignDtoMapperTest {

    @InjectMocks
    private HabitAssignDtoMapper mapper;

    @Test
    void convert_FromHabitAssignToHabitAssignDto_FullData() {

        User user = User.builder().id(1L).build();
        Habit habit = Habit.builder().id(2L).build();
        ZonedDateTime createDateTime = ZonedDateTime.of(LocalDateTime.now().minusDays(5), ZoneId.of("Europe/Kyiv"));
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.of(LocalDateTime.now().minusDays(1), ZoneId.of("Europe/Kyiv"));

        HabitStatusCalendar calendar1 = HabitStatusCalendar.builder()
            .id(10L)
            .enrollDate(LocalDate.now().minusDays(4))
            .habitAssign(HabitAssign.builder().id(5L).build())
            .build();
        HabitStatusCalendar calendar2 = HabitStatusCalendar.builder()
            .id(11L)
            .enrollDate(LocalDate.now().minusDays(3))
            .habitAssign(HabitAssign.builder().id(5L).build())
            .build();

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
            .habitStatusCalendars(List.of(calendar1, calendar2))
            .build();

        HabitAssignDto habitAssignDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignDto);
        assertEquals(habitAssign.getId(), habitAssignDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignDto.getUserId());
        assertEquals(habitAssign.getDuration(), habitAssignDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignDto.getLastEnrollmentDate());
        assertEquals(habitAssign.getHabitStatusCalendars().size(),
            habitAssignDto.getHabitStatusCalendarDtoList().size());

        List<Long> expectedCalendarIds = habitAssign.getHabitStatusCalendars().stream()
            .map(HabitStatusCalendar::getId)
            .collect(Collectors.toList());
        List<Long> actualCalendarIds = habitAssignDto.getHabitStatusCalendarDtoList().stream()
            .map(HabitStatusCalendarDto::getId)
            .collect(Collectors.toList());
        assertEquals(expectedCalendarIds, actualCalendarIds);

        List<LocalDate> expectedEnrollDates = habitAssign.getHabitStatusCalendars().stream()
            .map(HabitStatusCalendar::getEnrollDate)
            .collect(Collectors.toList());
        List<LocalDate> actualEnrollDates = habitAssignDto.getHabitStatusCalendarDtoList().stream()
            .map(HabitStatusCalendarDto::getEnrollDate)
            .collect(Collectors.toList());
        assertEquals(expectedEnrollDates, actualEnrollDates);
    }

    @Test
    void convert_FromHabitAssignToHabitAssignDto_MinimalData() {

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
            .habitStatusCalendars(Collections.emptyList())
            .build();

        HabitAssignDto habitAssignDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignDto);
        assertEquals(habitAssign.getId(), habitAssignDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignDto.getUserId());
        assertEquals(habitAssign.getDuration(), habitAssignDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignDto.getLastEnrollmentDate());
        assertTrue(habitAssignDto.getHabitStatusCalendarDtoList().isEmpty());
    }

    @Test
    void convert_FromHabitAssignToHabitAssignDto_NoHabitStatusCalendars() {

        User user = User.builder().id(3L).build();
        Habit habit = Habit.builder().id(4L).build();
        ZonedDateTime createDateTime = ZonedDateTime.now().minusHours(2);
        ZonedDateTime lastEnrollmentDate = ZonedDateTime.now().minusDays(7);

        HabitAssign habitAssign = HabitAssign.builder()
            .id(7L)
            .status(HabitAssignStatus.CANCELLED)
            .createDate(createDateTime)
            .user(user)
            .habit(habit)
            .duration(60)
            .workingDays(30)
            .habitStreak(15)
            .lastEnrollmentDate(lastEnrollmentDate)
            .progressNotificationHasDisplayed(true)
            .habitStatusCalendars(Collections.emptyList())
            .build();

        HabitAssignDto habitAssignDto = mapper.convert(habitAssign);

        assertNotNull(habitAssignDto);
        assertEquals(habitAssign.getId(), habitAssignDto.getId());
        assertEquals(habitAssign.getStatus(), habitAssignDto.getStatus());
        assertEquals(habitAssign.getCreateDate(), habitAssignDto.getCreateDateTime());
        assertEquals(habitAssign.getUser().getId(), habitAssignDto.getUserId());
        assertEquals(habitAssign.getDuration(), habitAssignDto.getDuration());
        assertEquals(habitAssign.getHabitStreak(), habitAssignDto.getHabitStreak());
        assertEquals(habitAssign.getWorkingDays(), habitAssignDto.getWorkingDays());
        assertEquals(habitAssign.getLastEnrollmentDate(), habitAssignDto.getLastEnrollmentDate());
        assertNotNull(habitAssignDto.getHabitStatusCalendarDtoList());
        assertTrue(habitAssignDto.getHabitStatusCalendarDtoList().isEmpty());
    }
}
