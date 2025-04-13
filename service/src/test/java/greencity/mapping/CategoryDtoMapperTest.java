package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryDtoMapperTest {

    private final CategoryDtoMapper mapper = new CategoryDtoMapper();

    @Test
    void convert_validCategoryDto_returnsValidEntity() {
        Category expected = Category.builder()
                .name("test")
                .build();

        CategoryDto categoryDto = CategoryDto.builder()
                .name(expected.getName())
                .build();

        Category actual = mapper.convert(categoryDto);

        assertEquals(expected, actual);
    }

    @Test
    void convert_emptyDto_fieldsNullOrDefault() {
        Category expected = new Category();
        Category actual = mapper.convert(new CategoryDto());

        assertEquals(expected, actual);
    }
}
