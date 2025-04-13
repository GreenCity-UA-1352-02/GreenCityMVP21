package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageVO;
import greencity.enums.FactOfDayStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class HabitFactDtoResponseMapperTest {

    @InjectMocks
    private HabitFactDtoResponseMapper mapper;

    @Test
    void convert_FromHabitFactVOToHabitFactDtoResponse_FullData() {

        LanguageVO ukrainian = LanguageVO.builder().id(1L).code("uk").build();
        LanguageVO english = LanguageVO.builder().id(2L).code("en").build();
        HabitVO habitVO = HabitVO.builder().id(10L).image("running.jpg").complexity(2).build();

        HabitFactTranslationVO translationUkVO = HabitFactTranslationVO.builder()
                .id(100L)
                .content("Біг корисний для здоров'я.")
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .language(ukrainian)
                .build();

        HabitFactTranslationVO translationEnVO = HabitFactTranslationVO.builder()
                .id(101L)
                .content("Running is good for health.")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(english)
                .build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(50L)
                .habit(habitVO)
                .translations(List.of(translationUkVO, translationEnVO))
                .build();

        HabitFactDtoResponse responseDto = mapper.convert(habitFactVO);

        assertNotNull(responseDto);
        assertEquals(habitFactVO.getId(), responseDto.getId());
        assertEquals(habitFactVO.getHabit(), responseDto.getHabit());
        assertNotNull(responseDto.getTranslations());
        assertEquals(habitFactVO.getTranslations().size(), responseDto.getTranslations().size());

        List<HabitFactTranslationDto> translationsDto = responseDto.getTranslations();

        HabitFactTranslationDto dtoUk = translationsDto.stream()
                .filter(dto -> dto.getLanguage().getCode().equals("uk"))
                .findFirst().orElse(null);
        assertNotNull(dtoUk);
        assertEquals(translationUkVO.getId(), dtoUk.getId());
        assertEquals(translationUkVO.getContent(), dtoUk.getContent());
        assertEquals(FactOfDayStatus.CURRENT, dtoUk.getFactOfDayStatus());
        assertEquals(translationUkVO.getLanguage().getId(), dtoUk.getLanguage().getId());
        assertEquals(translationUkVO.getLanguage().getCode(), dtoUk.getLanguage().getCode());
        assertEquals(habitVO, responseDto.getHabit()); // Verify HabitVO in response

        HabitFactTranslationDto dtoEn = translationsDto.stream()
                .filter(dto -> dto.getLanguage().getCode().equals("en"))
                .findFirst().orElse(null);
        assertNotNull(dtoEn);
        assertEquals(translationEnVO.getId(), dtoEn.getId());
        assertEquals(translationEnVO.getContent(), dtoEn.getContent());
        assertEquals(FactOfDayStatus.POTENTIAL, dtoEn.getFactOfDayStatus());
        assertEquals(translationEnVO.getLanguage().getId(), dtoEn.getLanguage().getId());
        assertEquals(translationEnVO.getLanguage().getCode(), dtoEn.getLanguage().getCode());
    }

    @Test
    void convert_FromHabitFactVOToHabitFactDtoResponse_NoTranslations() {

        HabitVO habitVO = HabitVO.builder().id(12L).image("walking.jpg").complexity(3).build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(52L)
                .habit(habitVO)
                .translations(List.of())
                .build();

        HabitFactDtoResponse responseDto = mapper.convert(habitFactVO);

        assertNotNull(responseDto);
        assertEquals(habitFactVO.getId(), responseDto.getId());
        assertEquals(habitFactVO.getHabit(), responseDto.getHabit());
        assertNotNull(responseDto.getTranslations());
        assertEquals(0, responseDto.getTranslations().size());
        assertEquals(habitVO, responseDto.getHabit());
    }

    @Test
    void convert_FromHabitFactVOToHabitFactDtoResponse_DifferentFactOfDayStatus() {

        LanguageVO english = LanguageVO.builder().id(2L).code("en").build();
        HabitVO habitVO = HabitVO.builder().id(13L).image("sleeping.jpg").complexity(2).build();

        HabitFactTranslationVO translationEnVO = HabitFactTranslationVO.builder()
                .id(103L)
                .content("Sufficient sleep is crucial for well-being.")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(english)
                .build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(53L)
                .habit(habitVO)
                .translations(List.of(translationEnVO))
                .build();

        HabitFactDtoResponse responseDto = mapper.convert(habitFactVO);

        assertNotNull(responseDto);
        assertEquals(habitFactVO.getId(), responseDto.getId());
        assertEquals(habitFactVO.getHabit(), responseDto.getHabit());
        assertNotNull(responseDto.getTranslations());
        assertEquals(1, responseDto.getTranslations().size());

        HabitFactTranslationDto dtoEn = responseDto.getTranslations().get(0);
        assertEquals(translationEnVO.getId(), dtoEn.getId());
        assertEquals(translationEnVO.getContent(), dtoEn.getContent());
        assertEquals(FactOfDayStatus.POTENTIAL, dtoEn.getFactOfDayStatus());
        assertEquals(translationEnVO.getLanguage().getId(), dtoEn.getLanguage().getId());
        assertEquals(translationEnVO.getLanguage().getCode(), dtoEn.getLanguage().getCode());
        assertEquals(habitVO, responseDto.getHabit());
    }

    @Test
    void convert_FromHabitFactVOToHabitFactDtoResponse_VariousFactOfDayStatuses() {

        LanguageVO ukrainian = LanguageVO.builder().id(1L).code("uk").build();
        LanguageVO english = LanguageVO.builder().id(2L).code("en").build();
        HabitVO habitVO = HabitVO.builder().id(14L).image("hydration.jpg").complexity(1).build();

        HabitFactTranslationVO translationUkCurrentVO = HabitFactTranslationVO.builder()
                .id(104L)
                .content("Пийте достатньо води сьогодні.")
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .language(ukrainian)
                .build();

        HabitFactTranslationVO translationEnPotentialVO = HabitFactTranslationVO.builder()
                .id(105L)
                .content("Consider drinking more water.")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(english)
                .build();

        HabitFactTranslationVO translationUkUsedVO = HabitFactTranslationVO.builder()
                .id(106L)
                .content("Ви вже випили свою норму води.")
                .factOfDayStatus(FactOfDayStatus.USED)
                .language(ukrainian)
                .build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(54L)
                .habit(habitVO)
                .translations(List.of(translationUkCurrentVO, translationEnPotentialVO, translationUkUsedVO))
                .build();

        HabitFactDtoResponse responseDto = mapper.convert(habitFactVO);

        assertNotNull(responseDto);
        assertEquals(habitFactVO.getId(), responseDto.getId());
        assertEquals(habitFactVO.getHabit(), responseDto.getHabit());
        assertNotNull(responseDto.getTranslations());
        assertEquals(3, responseDto.getTranslations().size());
        assertEquals(habitVO, responseDto.getHabit());

        responseDto.getTranslations().forEach(dto -> {
            if (dto.getLanguage().getCode().equals("uk")) {
                if (dto.getContent().contains("сьогодні")) {
                    assertEquals(FactOfDayStatus.CURRENT, dto.getFactOfDayStatus());
                } else if (dto.getContent().contains("випили")) {
                    assertEquals(FactOfDayStatus.USED, dto.getFactOfDayStatus());
                }
            } else if (dto.getLanguage().getCode().equals("en")) {
                assertEquals(FactOfDayStatus.POTENTIAL, dto.getFactOfDayStatus());
            }
        });
    }
}
