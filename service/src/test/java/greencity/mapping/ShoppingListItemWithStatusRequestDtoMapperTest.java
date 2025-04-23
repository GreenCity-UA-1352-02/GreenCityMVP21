package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemWithStatusRequestDtoMapperTest {
    @InjectMocks
    private ShoppingListItemWithStatusRequestDtoMapper mapper;

    @Test
    void convertWithActiveStatus() {

        Long itemId = 20L;
        ShoppingListItemStatus status = ShoppingListItemStatus.ACTIVE;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(itemId)
            .status(status)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(status, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithDoneStatus() {

        Long itemId = 25L;
        ShoppingListItemStatus status = ShoppingListItemStatus.DONE;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(itemId)
            .status(status)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(status, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithDisabledStatus() {

        Long itemId = 30L;
        ShoppingListItemStatus status = ShoppingListItemStatus.DISABLED;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(itemId)
            .status(status)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(status, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithInProgressStatus() {

        Long itemId = 35L;
        ShoppingListItemStatus status = ShoppingListItemStatus.INPROGRESS;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(itemId)
            .status(status)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertEquals(status, userShoppingListItem.getStatus());
    }

    @Test
    void convertWithNullStatus() {

        Long itemId = 40L;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(itemId)
            .status(null)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertEquals(itemId, userShoppingListItem.getShoppingListItem().getId());
        assertNull(userShoppingListItem.getStatus());
    }

    @Test
    void convertWithNullId() {

        ShoppingListItemStatus status = ShoppingListItemStatus.ACTIVE;
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
            .id(null)
            .status(status)
            .build();

        UserShoppingListItem userShoppingListItem = mapper.convert(requestDto);

        assertNotNull(userShoppingListItem);
        assertNotNull(userShoppingListItem.getShoppingListItem());
        assertNull(userShoppingListItem.getShoppingListItem().getId());
        assertEquals(status, userShoppingListItem.getStatus());
    }
}
