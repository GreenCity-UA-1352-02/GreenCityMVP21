package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryDtoResponseMapperTest {
    private final CategoryDtoResponseMapper categoryDtoResponseMapper = new CategoryDtoResponseMapper();

    @Test
    public void convert_validEntity_returnsCategoryDtoResponse() {
        Category category = Category.builder()
            .id(1L)
            .name("test")
            .build();

        CategoryDtoResponse expected = CategoryDtoResponse.builder()
            .id(1L)
            .name("test")
            .build();

        CategoryDtoResponse actual = categoryDtoResponseMapper.convert(category);

        assertThat(actual).isNotNull();
        assertEquals(expected, actual);
    }

    @Test
    public void convert_dtoWithNullFields_returnsCategoryDtoResponse() {
        Category category = Category.builder()
            .id(null)
            .name(null)
            .build();

        CategoryDtoResponse actual = categoryDtoResponseMapper.convert(category);
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isNull();
    }

    @Test
    public void convert_emptyDto_fieldsNullOrDefault() {
        CategoryDtoResponse expected = new CategoryDtoResponse();
        CategoryDtoResponse actual = categoryDtoResponseMapper.convert(new Category());

        assertEquals(expected, actual);
    }
}
