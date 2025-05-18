package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static greencity.ModelUtils.getLanguage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class HabitTranslationDtoMapperTest {

    HabitTranslationDtoMapper mapper = new HabitTranslationDtoMapper();

    @Test
    void convert_SuccessMapping() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .habitItem("HabitItem")
                .language(getLanguage())
                .build();
        HabitTranslationDto dto = mapper.convert(habitTranslation);

        assertThat(dto).isNotNull();
        assertEquals(dto.getDescription(), habitTranslation.getDescription());
        assertEquals(dto.getHabitItem(), habitTranslation.getHabitItem());
        assertEquals(dto.getName(), habitTranslation.getName());
        assertEquals(dto.getLanguageCode(), habitTranslation.getLanguage().getCode());
    }


    @Test
    void convert_MapAllToDto() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .habitItem("HabitItem")
                .language(getLanguage())
                .build();
        HabitTranslation habitTranslation2 = HabitTranslation.builder()
                .id(2L)
                .name("Name2")
                .description("Description2")
                .habitItem("HabitItem2")
                .language(getLanguage())
                .build();
        List<HabitTranslation> translationList = List.of(habitTranslation, habitTranslation2);

        List<HabitTranslationDto> dtoList = mapper.mapAllToList(translationList);

        assertThat(dtoList).isNotNull();
        assertEquals(dtoList.get(0).getDescription(), habitTranslation.getDescription());
        assertEquals(dtoList.get(1).getDescription(), habitTranslation2.getDescription());
        assertEquals(dtoList.get(0).getHabitItem(), habitTranslation.getHabitItem());
        assertEquals(dtoList.get(1).getHabitItem(), habitTranslation2.getHabitItem());
        assertEquals(dtoList.get(0).getName(), habitTranslation.getName());
        assertEquals(dtoList.get(1).getName(), habitTranslation2.getName());
        assertEquals(dtoList.get(0).getLanguageCode(), habitTranslation.getLanguage().getCode());
        assertEquals(dtoList.get(1).getLanguageCode(), habitTranslation2.getLanguage().getCode());
    }

}
