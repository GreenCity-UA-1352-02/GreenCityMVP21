package greencity.mapping;

import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getFilter;
import static greencity.ModelUtils.getUserFilterDtoResponse;
import static org.junit.jupiter.api.Assertions.*;

class FilterDtoResponseMapperTest {

    private final FilterDtoResponseMapper filterDtoResponseMapper = new FilterDtoResponseMapper();

    private final Filter filter = getFilter();

    @Test
    void convert_ValidFilter_ReturnsUserFilterDtoResponse() {
        UserFilterDtoResponse expected = getUserFilterDtoResponse();

        UserFilterDtoResponse actual = filterDtoResponseMapper.convert(filter);

        assertEquals(expected, actual);
    }

    @Test
    void convert_EmptyFilter_NullPointerExceptionThrown() {
        Filter emptyFilter = new Filter();

        assertThrows(NullPointerException.class, () -> filterDtoResponseMapper.convert(emptyFilter));
    }

    @Test
    void convert_Null_NullPointerExceptionThrown() {
        Filter nullFilter = null;

        assertThrows(NullPointerException.class, () -> filterDtoResponseMapper.convert(nullFilter));
    }

}