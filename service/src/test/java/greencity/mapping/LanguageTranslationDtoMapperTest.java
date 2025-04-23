package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class LanguageTranslationDtoMapperTest {
    @InjectMocks
    private LanguageTranslationDtoMapper languageTranslationDtoMapper;

    @Test
    void convert_habitFactTranslationToLanguageTranslationDTO_habitFactTranslation() {
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();

        LanguageTranslationDTO expected = LanguageTranslationDTO.builder()
                .content(habitFactTranslation.getContent())
                .language(LanguageDTO.builder()
                        .id(habitFactTranslation.getLanguage().getId())
                        .code(habitFactTranslation.getLanguage().getCode())
                        .build())
                .build();

        assertNotNull(languageTranslationDtoMapper.convert(habitFactTranslation));
        assertEquals(expected, languageTranslationDtoMapper.convert(habitFactTranslation));
        assertEquals(habitFactTranslation.getLanguage().getId(), habitFactTranslation.getLanguage().getId());
        assertEquals(habitFactTranslation.getContent(), habitFactTranslation.getContent());
        assertEquals(habitFactTranslation.getLanguage().getCode(), habitFactTranslation.getLanguage().getCode());
    }
}
