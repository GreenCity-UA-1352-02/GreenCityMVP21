package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import greencity.enums.FactOfDayStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class LanguageTranslationDtoMapperTest {
    @InjectMocks
    private LanguageTranslationDtoMapper languageTranslationDtoMapper;

    @Test
    void convertTest() {
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();

        LanguageTranslationDTO expected = LanguageTranslationDTO.builder()
                .content(habitFactTranslation.getContent())
                .language(LanguageDTO.builder()
                        .id(habitFactTranslation.getLanguage().getId())
                        .code(habitFactTranslation.getLanguage().getCode())
                        .build())
                .build();
        ;

        assertEquals(expected, languageTranslationDtoMapper.convert(habitFactTranslation));


    }
}
