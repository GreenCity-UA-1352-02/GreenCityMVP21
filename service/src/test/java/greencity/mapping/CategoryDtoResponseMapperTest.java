package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryDtoResponseMapperTest {
    private final CategoryDtoResponseMapper categoryDtoResponseMapper = new CategoryDtoResponseMapper();

    @Test
    public void convert_validEntity_returnsCategoryDtoResponse() {
        Category category = Category.builder()
                .id(1L)
                .name("test")
                .build();

        CategoryDtoResponse categoryDtoResponse = CategoryDtoResponse.builder()
                .id(1L)
                .name("test")
                .build();

        CategoryDtoResponse actual = categoryDtoResponseMapper.convert(category);
        assertEquals(categoryDtoResponse, actual);
    }
}
