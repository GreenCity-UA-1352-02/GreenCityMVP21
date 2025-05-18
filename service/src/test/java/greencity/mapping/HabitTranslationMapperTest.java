package greencity.mapping;


import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static greencity.ModelUtils.getLanguage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HabitTranslationMapperTest {

    HabitTranslationMapper mapper = new HabitTranslationMapper();

    @Test
    void convert_Success() {
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name("name")
                .description("description")
                .habitItem("HabitItem")
                .languageCode(getLanguage().getCode())
                .build();

        HabitTranslation convert = mapper.convert(habitTranslationDto);

        assertThat(convert).isNotNull();
        assertEquals(habitTranslationDto.getName(), convert.getName());
        assertEquals(habitTranslationDto.getDescription(), convert.getDescription());
        assertEquals(habitTranslationDto.getHabitItem(), convert.getHabitItem());

    }

    @Test
    void convert_mapAllToHabitTranslation() {
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name("name")
                .description("description")
                .habitItem("HabitItem")
                .languageCode(getLanguage().getCode())
                .build();

        HabitTranslationDto habitTranslationDto2 = HabitTranslationDto.builder()
                .name("name2")
                .description("description2")
                .habitItem("HabitItem2")
                .languageCode(getLanguage().getCode())
                .build();

        List<HabitTranslationDto> listHabitTranslationDto = List.of(habitTranslationDto, habitTranslationDto2);

        List<HabitTranslation> convertList = mapper.mapAllToList(listHabitTranslationDto);

        assertThat(convertList).isNotNull();
        assertEquals(listHabitTranslationDto.size(), convertList.size());
        assertEquals(listHabitTranslationDto.get(0).getName(), convertList.get(0).getName());
        assertEquals(listHabitTranslationDto.get(1).getName(), convertList.get(1).getName());
        assertEquals(listHabitTranslationDto.get(0).getDescription(), convertList.get(0).getDescription());
        assertEquals(listHabitTranslationDto.get(1).getDescription(), convertList.get(1).getDescription());
        assertEquals(listHabitTranslationDto.get(0).getHabitItem(), convertList.get(0).getHabitItem());
        assertEquals(listHabitTranslationDto.get(1).getHabitItem(), convertList.get(1).getHabitItem());


    }
}
