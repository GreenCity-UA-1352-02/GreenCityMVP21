package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class NewTagDtoMapperTest {
    @InjectMocks
    private NewTagDtoMapper newTagDtoMapper;

    @Test
    void convertTest() {
        Tag newTag = ModelUtils.getTag();
        List<TagTranslation> tagTranslations = ModelUtils.getTagTranslations();

        NewTagDto expected = NewTagDto.builder()
                .name(tagTranslations.get(1).getName())
                .nameUa(tagTranslations.get(0).getName())
                .id(newTag.getId())
                .build();

        assertEquals(expected, newTagDtoMapper.convert(newTag));
    }
}
