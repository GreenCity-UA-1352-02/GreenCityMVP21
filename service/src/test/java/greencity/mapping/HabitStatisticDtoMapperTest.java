package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitStatistic;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getHabitAssign;
import static greencity.ModelUtils.getHabitStatistic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HabitStatisticDtoMapperTest {

    HabitStatisticDtoMapper mapper = new HabitStatisticDtoMapper();

    @Test
    void convert_SuccessfulMapping() {
        HabitStatistic habitStatistic = getHabitStatistic();
        habitStatistic.setHabitAssign(getHabitAssign());

        HabitStatisticDto dto = mapper.convert(habitStatistic);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(habitStatistic.getId());
        assertEquals(dto.getAmountOfItems(), habitStatistic.getAmountOfItems());
        assertEquals(dto.getHabitRate(), habitStatistic.getHabitRate());
        assertEquals(dto.getCreateDate(), habitStatistic.getCreateDate());
        assertEquals(dto.getHabitAssignId(), habitStatistic.getHabitAssign().getId());
    }

    @Test
    void convert_nullHabitAssign_throwsNullPointerException() {
        HabitStatistic habitStatistic = getHabitStatistic();

        assertThrows(NullPointerException.class, () -> mapper.convert(habitStatistic));
    }
}
