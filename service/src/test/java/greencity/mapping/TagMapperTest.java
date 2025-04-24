package greencity.mapping;

import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Tag;

import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class TagMapperTest {
    @InjectMocks
    private TagMapper mapper;

    @Test
    void convert() {

        Long tagId = 1L;
        TagType tagType = TagType.ECO_NEWS;
        Long translationId1 = 2L;
        String translationName1 = "Новина";
        String languageCode1 = "uk";
        Long languageId1 = 3L;

        Long translationId2 = 4L;
        String translationName2 = "News";
        String languageCode2 = "en";
        Long languageId2 = 5L;

        TagVO tagVO = TagVO.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(List.of(
                TagTranslationVO.builder()
                    .id(translationId1)
                    .name(translationName1)
                    .languageVO(LanguageVO.builder().code(languageCode1).id(languageId1).build())
                    .build(),
                TagTranslationVO.builder()
                    .id(translationId2)
                    .name(translationName2)
                    .languageVO(LanguageVO.builder().code(languageCode2).id(languageId2).build())
                    .build()
            ))
            .build();

        Tag tag = mapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagId, tag.getId());
        assertEquals(tagType, tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertEquals(2, tag.getTagTranslations().size());

        TagTranslation translation1 = tag.getTagTranslations().getFirst();
        assertEquals(translationId1, translation1.getId());
        assertEquals(translationName1, translation1.getName());
        assertNotNull(translation1.getLanguage());
        assertEquals(languageCode1, translation1.getLanguage().getCode());
        assertEquals(languageId1, translation1.getLanguage().getId());

        TagTranslation translation2 = tag.getTagTranslations().get(1);
        assertEquals(translationId2, translation2.getId());
        assertEquals(translationName2, translation2.getName());
        assertNotNull(translation2.getLanguage());
        assertEquals(languageCode2, translation2.getLanguage().getCode());
        assertEquals(languageId2, translation2.getLanguage().getId());
    }

    @Test
    void convertHabitTagWithMultipleTranslations() {

        Long tagId = 6L;
        TagType tagType = TagType.HABIT;
        TagVO tagVO = TagVO.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(List.of(
                TagTranslationVO.builder()
                    .id(7L)
                    .name("Звичка")
                    .languageVO(LanguageVO.builder().code("uk").id(8L).build())
                    .build(),
                TagTranslationVO.builder()
                    .id(9L)
                    .name("Habit")
                    .languageVO(LanguageVO.builder().code("en").id(10L).build())
                    .build()
            ))
            .build();

        Tag tag = mapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagId, tag.getId());
        assertEquals(tagType, tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertEquals(2, tag.getTagTranslations().size());
    }

    @Test
    void convertEventTagWithNoTranslations() {

        Long tagId = 11L;
        TagType tagType = TagType.EVENT;
        TagVO tagVO = TagVO.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(Collections.emptyList())
            .build();

        Tag tag = mapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagId, tag.getId());
        assertEquals(tagType, tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertEquals(0, tag.getTagTranslations().size());
    }

    @Test
    void convertWithNullIdAndType() {

        TagVO tagVO = TagVO.builder()
            .id(null)
            .type(null)
            .tagTranslations(List.of(
                TagTranslationVO.builder()
                    .id(12L)
                    .name("Важливо")
                    .languageVO(LanguageVO.builder().code("ua").id(13L).build())
                    .build()
            ))
            .build();

        Tag tag = mapper.convert(tagVO);

        assertNotNull(tag);
        assertNull(tag.getId());
        assertNull(tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertEquals(1, tag.getTagTranslations().size());
        assertEquals("Важливо", tag.getTagTranslations().getFirst().getName());
    }

    @Test
    void convertWithNullLanguageVO() {

        Long tagId = 14L;
        TagType tagType = TagType.ECO_NEWS;
        TagVO tagVO = TagVO.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(List.of(
                TagTranslationVO.builder()
                    .id(15L)
                    .name("Important")
                    .languageVO(null)
                    .build()
            ))
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> mapper.convert(tagVO));
    }
}
