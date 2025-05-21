package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getHabitAssign;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HabitStatusCalendarVOMapperTest {
    HabitStatusCalendarVOMapper mapper = new HabitStatusCalendarVOMapper();

    @Test
    void convert_SuccessMapping() {
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();
        habitStatusCalendar.setHabitAssign(getHabitAssign());
        HabitStatusCalendarVO convert = mapper.convert(habitStatusCalendar);

        assertThat(convert).isNotNull();
        assertEquals(convert.getId(), habitStatusCalendar.getId());
        assertEquals(convert.getEnrollDate(), habitStatusCalendar.getEnrollDate());
        assertEquals(convert.getHabitAssignVO().getId(), habitStatusCalendar.getHabitAssign().getId());
    }

    @Test
    void convert_NullHabitAssign_ThrowNullPointerException() {
        HabitStatusCalendar habitStatusCalendar = ModelUtils.getHabitStatusCalendar();

        assertThrows(NullPointerException.class, () -> mapper.convert(habitStatusCalendar));
    }
}
