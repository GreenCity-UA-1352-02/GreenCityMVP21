package greencity.mapping;

import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Language;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TagVOMapperTest {

    @InjectMocks
    private TagVOMapper mapper;

    @Test
    void convert() {

        Long tagId = 1L;
        TagType tagType = TagType.ECO_NEWS;
        Long translationId1 = 2L;
        String translationName1 = "ЕкоНовина";
        Long languageId1 = 3L;
        String languageCode1 = "uk";

        Long translationId2 = 4L;
        String translationName2 = "EcoNews";
        Long languageId2 = 5L;
        String languageCode2 = "en";

        Tag tag = Tag.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(List.of(
                TagTranslation.builder()
                    .id(translationId1)
                    .name(translationName1)
                    .language(Language.builder().id(languageId1).code(languageCode1).build())
                    .build(),
                TagTranslation.builder()
                    .id(translationId2)
                    .name(translationName2)
                    .language(Language.builder().id(languageId2).code(languageCode2).build())
                    .build()))
            .build();

        TagVO tagVO = mapper.convert(tag);

        assertNotNull(tagVO);
        assertEquals(tagId, tagVO.getId());
        assertEquals(tagType, tagVO.getType());
        assertNotNull(tagVO.getTagTranslations());
        assertEquals(2, tagVO.getTagTranslations().size());

        TagTranslationVO translationVO1 = tagVO.getTagTranslations().getFirst();
        assertEquals(translationId1, translationVO1.getId());
        assertEquals(translationName1, translationVO1.getName());
        assertNotNull(translationVO1.getLanguageVO());
        assertEquals(languageId1, translationVO1.getLanguageVO().getId());
        assertEquals(languageCode1, translationVO1.getLanguageVO().getCode());

        TagTranslationVO translationVO2 = tagVO.getTagTranslations().get(1);
        assertEquals(translationId2, translationVO2.getId());
        assertEquals(translationName2, translationVO2.getName());
        assertNotNull(translationVO2.getLanguageVO());
        assertEquals(languageId2, translationVO2.getLanguageVO().getId());
        assertEquals(languageCode2, translationVO2.getLanguageVO().getCode());
    }

    @Test
    void convertWithNoTranslations() {

        Long tagId = 6L;
        TagType tagType = TagType.HABIT;
        Tag tag = Tag.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(Collections.emptyList())
            .build();

        TagVO tagVO = mapper.convert(tag);

        assertNotNull(tagVO);
        assertEquals(tagId, tagVO.getId());
        assertEquals(tagType, tagVO.getType());
        assertNotNull(tagVO.getTagTranslations());
        assertEquals(0, tagVO.getTagTranslations().size());
    }

    @Test
    void convertWithNullTranslations() {

        Long tagId = 7L;
        TagType tagType = TagType.EVENT;
        Tag tag = Tag.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(null)
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> mapper.convert(tag));
    }

    @Test
    void convertWithNullIdAndType() {

        Tag tag = Tag.builder()
            .id(null)
            .type(null)
            .tagTranslations(List.of(
                TagTranslation.builder()
                    .id(8L)
                    .name("Акція")
                    .language(Language.builder().id(9L).code("ua").build())
                    .build()))
            .build();

        TagVO tagVO = mapper.convert(tag);

        assertNotNull(tagVO);
        assertNull(tagVO.getId());
        assertNull(tagVO.getType());
        assertNotNull(tagVO.getTagTranslations());
        assertEquals(1, tagVO.getTagTranslations().size());
        assertEquals("Акція", tagVO.getTagTranslations().getFirst().getName());
        assertNotNull(tagVO.getTagTranslations().getFirst().getLanguageVO());
        assertEquals(9L, tagVO.getTagTranslations().getFirst().getLanguageVO().getId());
        assertEquals("ua", tagVO.getTagTranslations().getFirst().getLanguageVO().getCode());
    }

    @Test
    void convertWithNullLanguage() {

        Long tagId = 10L;
        TagType tagType = TagType.ECO_NEWS;
        Tag tag = Tag.builder()
            .id(tagId)
            .type(tagType)
            .tagTranslations(List.of(
                TagTranslation.builder()
                    .id(11L)
                    .name("Important")
                    .language(null)
                    .build()))
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> mapper.convert(tag));
    }
}
