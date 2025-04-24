package greencity.mapping;

import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;

import static greencity.ModelUtils.getUserFilterDtoRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterDtoRequestMapperTest {

    private final FilterDtoRequestMapper filterDtoRequestMapper = new FilterDtoRequestMapper();

    private final UserFilterDtoRequest userFilterDtoRequest = getUserFilterDtoRequest();

    @Test
    void convert_ValidUserFilterDtoRequest_ReturnsFilter() {
        Filter expected = Filter.builder()
            .name("Test_Filter")
            .type("USERS")
            .values("Test;USER;ACTIVATED")
            .build();

        Filter actual = filterDtoRequestMapper.convert(userFilterDtoRequest);

        assertEquals(expected, actual);
    }

    @Test
    void convert_EmptyUserFilterDtoRequest_NullPointerExceptionThrown() {
        UserFilterDtoRequest emptyUserFilterDtoRequest = new UserFilterDtoRequest();

        assertThrows(NullPointerException.class, () -> filterDtoRequestMapper.convert(emptyUserFilterDtoRequest));
    }

    @Test
    void convert_Null_NullPointerExceptionThrown() {
        UserFilterDtoRequest nullUserFilterDtoRequest = null;

        assertThrows(NullPointerException.class, () -> filterDtoRequestMapper.convert(nullUserFilterDtoRequest));
    }

}