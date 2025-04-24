package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemRequestDtoMapperTest {

    @InjectMocks
    private ShoppingListItemRequestDtoMapper mapper;

    @Test
    void convert() {

        Long itemId = 15L;
        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
            .id(itemId)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithNullId() {
        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
            .id(null)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertNull(userShoppingListItem.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithMinValidId() {
        Long itemId = 1L;
        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
            .id(itemId)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, userShoppingListItem.getStatus());
    }
}
