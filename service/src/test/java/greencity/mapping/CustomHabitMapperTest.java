package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomHabitMapperTest {
    private final CustomHabitMapper customHabitMapper = new CustomHabitMapper();

    @Test
    void convert_validHabitDtoRequest_returnsHabit() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = AddCustomHabitDtoRequest.builder()
                .image("image.png")
                .complexity(10)
                .defaultDuration(25)
                .build();

        Habit expected = Habit.builder()
                .image(addCustomHabitDtoRequest.getImage())
                .complexity(addCustomHabitDtoRequest.getComplexity())
                .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
                .isCustomHabit(true)
                .build();

        Habit actual = customHabitMapper.convert(addCustomHabitDtoRequest);

        assertEquals(expected, actual);
    }

    @Test
    void convert_emptyAddCustomHabitDtoRequest_returnsEmptyHabit() {
        AddCustomHabitDtoRequest emptyAddCustomHabitDtoRequest = new AddCustomHabitDtoRequest();

        assertEquals(new Habit().setIsCustomHabit(true), customHabitMapper.convert(emptyAddCustomHabitDtoRequest));
    }

    @Test
    void convert_nullAddCustomHabitDtoRequest_throwsNullPointerException() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = null;

        assertThrows(NullPointerException.class, () -> customHabitMapper.convert(addCustomHabitDtoRequest));
    }

    @Test
    void convert_addCustomHabitDtoRequestWithOnlyImageField_returnsHabit() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = AddCustomHabitDtoRequest.builder()
                .image("image.png")
                .complexity(null)
                .defaultDuration(null)
                .build();

        Habit expected = Habit.builder()
                .image("image.png")
                .isCustomHabit(true)
                .build();

        assertEquals(expected, customHabitMapper.convert(addCustomHabitDtoRequest));
        assertThat(addCustomHabitDtoRequest.getComplexity()).isNull();
        assertThat(addCustomHabitDtoRequest.getDefaultDuration()).isNull();
    }
}
