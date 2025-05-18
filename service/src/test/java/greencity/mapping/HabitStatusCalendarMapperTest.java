package greencity.mapping;


import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HabitStatusCalendarMapperTest {

    HabitStatusCalendarMapper mapper = new HabitStatusCalendarMapper();

    @Test
    void convert_SuccessfulMapping() {
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(LocalDate.now())
                .habitAssignVO(HabitAssignVO.builder()
                        .id(1L)
                        .build())
                .build();

        HabitStatusCalendar dto = mapper.convert(habitStatusCalendarVO);

        assertThat(dto).isNotNull();
        assertEquals(dto.getId(), habitStatusCalendarVO.getId());
        assertEquals(dto.getEnrollDate(), habitStatusCalendarVO.getEnrollDate());
        assertEquals(dto.getHabitAssign().getId(), habitStatusCalendarVO.getHabitAssignVO().getId());
    }

    @Test
    void convert_NullHabitAssignVO_ThrowNullPointerException() {
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(LocalDate.now())
                .build();

        assertThrows(NullPointerException.class, () -> mapper.convert(habitStatusCalendarVO));
    }
}
