package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EcoNewsAuthorDtoMapperTest {

    private final EcoNewsAuthorDtoMapper mapper = new EcoNewsAuthorDtoMapper();

    @Test
    void convert_MapUserToEcoNewsAuthorDto_Success() {

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        EcoNewsAuthorDto dto = mapper.convert(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
    }

    @Test
    void convert_UserWithNullProperties_ReturnsValidDtoWithNullProperties() {
        User user = new User();
        EcoNewsAuthorDto dto = mapper.convert(user);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
    }
}
