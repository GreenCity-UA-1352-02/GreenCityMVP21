package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShoppingListItemDtoMapperTest {

    private ShoppingListItemDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShoppingListItemDtoMapper();
    }

    @Test
    void convert_ShouldMapFieldsCorrectly() {

        ShoppingListItem item = mock(ShoppingListItem.class);
        when(item.getId()).thenReturn(10L);

        ShoppingListItemTranslation translation = mock(ShoppingListItemTranslation.class);
        when(translation.getShoppingListItem()).thenReturn(item);
        when(translation.getContent()).thenReturn("Buy reusable bottle");

        ShoppingListItemDto result = mapper.convert(translation);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Buy reusable bottle", result.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }

    @Test
    void convert_ShouldThrowNullPointerException_WhenShoppingListItemIsNull() {

        ShoppingListItemTranslation translation = mock(ShoppingListItemTranslation.class);
        when(translation.getShoppingListItem()).thenReturn(null);
        when(translation.getContent()).thenReturn("No ID item");

        assertThrows(NullPointerException.class, () -> mapper.convert(translation));
    }

    @Test
    void convert_ShouldHandleNullContent_Successfully() {

        ShoppingListItem item = mock(ShoppingListItem.class);
        when(item.getId()).thenReturn(15L);

        ShoppingListItemTranslation translation = mock(ShoppingListItemTranslation.class);
        when(translation.getShoppingListItem()).thenReturn(item);
        when(translation.getContent()).thenReturn(null);

        ShoppingListItemDto result = mapper.convert(translation);

        assertNotNull(result);
        assertEquals(15L, result.getId());
        assertNull(result.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }
}
