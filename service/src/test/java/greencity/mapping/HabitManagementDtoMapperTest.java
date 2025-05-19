package greencity.mapping;

import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HabitManagementDtoMapperTest {

    private final HabitManagementDtoMapper mapper = new HabitManagementDtoMapper();

    @Test
    void convert_HabitToHabitManagementDto_Success() {
        Language language = Language.builder()
            .id(1L)
            .code("en")
            .build();

        HabitTranslation translation = HabitTranslation.builder()
            .id(100L)
            .description("Test description")
            .habitItem("Test item")
            .name("Test name")
            .language(language)
            .build();

        Habit habit = Habit.builder()
            .id(1L)
            .image("test.jpg")
            .complexity(2)
            .defaultDuration(30)
            .habitTranslations(List.of(translation))
            .build();

        HabitManagementDto dto = mapper.convert(habit);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(habit.getId());
        assertThat(dto.getImage()).isEqualTo(habit.getImage());
        assertThat(dto.getComplexity()).isEqualTo(habit.getComplexity());
        assertThat(dto.getDefaultDuration()).isEqualTo(habit.getDefaultDuration());

        HabitTranslationManagementDto dtoTranslation = dto.getHabitTranslations().get(0);
        assertThat(dtoTranslation.getId()).isEqualTo(translation.getId());
        assertThat(dtoTranslation.getDescription()).isEqualTo(translation.getDescription());
        assertThat(dtoTranslation.getHabitItem()).isEqualTo(translation.getHabitItem());
        assertThat(dtoTranslation.getName()).isEqualTo(translation.getName());
        assertThat(dtoTranslation.getLanguageCode()).isEqualTo(language.getCode());
    }

    @Test
    void convert_EmptyHabitTranslations() {
        Habit habit = Habit.builder()
            .id(2L)
            .image("empty.jpg")
            .complexity(1)
            .defaultDuration(15)
            .habitTranslations(List.of())
            .build();

        HabitManagementDto dto = mapper.convert(habit);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(habit.getId());
        assertThat(dto.getImage()).isEqualTo(habit.getImage());
        assertThat(dto.getComplexity()).isEqualTo(habit.getComplexity());
        assertThat(dto.getDefaultDuration()).isEqualTo(habit.getDefaultDuration());
        assertThat(dto.getHabitTranslations().isEmpty());
    }
}
