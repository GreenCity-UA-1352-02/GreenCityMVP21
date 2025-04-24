package greencity.mapping;

import greencity.dto.tag.TagDto;
import greencity.entity.Language;
import greencity.entity.Tag;

import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TagDtoMapperTest {

    @InjectMocks
    private TagDtoMapper mapper;

    @Test
    void convert() {

        Long tagId = 10L;
        String tagName = "Екологія";
        Tag tag = Tag.builder().id(tagId).build();
        TagTranslation tagTranslation = TagTranslation.builder()
            .id(1L)
            .name(tagName)
            .tag(tag)
            .language(Language.builder().id(1L).code("uk").build())
            .build();

        TagDto tagDto = mapper.convert(tagTranslation);

        assertNotNull(tagDto);
        assertEquals(tagId, tagDto.getId());
        assertEquals(tagName, tagDto.getName());
    }

    @Test
    void convertWithDifferentTagName() {

        Long tagId = 25L;
        String tagName = "Sustainability";
        Tag tag = Tag.builder().id(tagId).build();
        TagTranslation tagTranslation = TagTranslation.builder()
            .id(2L)
            .name(tagName)
            .tag(tag)
            .language(Language.builder().id(2L).code("en").build())
            .build();

        TagDto tagDto = mapper.convert(tagTranslation);

        assertNotNull(tagDto);
        assertEquals(tagId, tagDto.getId());
        assertEquals(tagName, tagDto.getName());
    }

    @Test
    void convertWithNullTag() {

        String tagName = "Новина";
        TagTranslation tagTranslation = TagTranslation.builder()
            .id(3L)
            .name(tagName)
            .tag(null)
            .language(Language.builder().id(1L).code("uk").build())
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> mapper.convert(tagTranslation));
    }

    @Test
    void convertWithNullTagName() {

        Long tagId = 40L;
        Tag tag = Tag.builder().id(tagId).build();
        TagTranslation tagTranslation = TagTranslation.builder()
            .id(4L)
            .name(null)
            .tag(tag)
            .language(Language.builder().id(2L).code("en").build())
            .build();

        TagDto tagDto = mapper.convert(tagTranslation);

        assertNotNull(tagDto);
        assertEquals(tagId, tagDto.getId());
        assertNull(tagDto.getName());
    }
}
