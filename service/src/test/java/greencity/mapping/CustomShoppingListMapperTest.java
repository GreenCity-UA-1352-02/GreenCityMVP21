package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static greencity.ModelUtils.getCustomShoppingListItem;
import static greencity.ModelUtils.getCustomShoppingListItemResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomShoppingListMapperTest {
    private final CustomShoppingListMapper mapper = new CustomShoppingListMapper();
    private final CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
        getCustomShoppingListItemResponseDto();
    private final CustomShoppingListItem customShoppingListItem = getCustomShoppingListItem();

    @Test
    void convert_validCustomShoppingListItemResponseDto_returnsCustomShoppingListItem() {

        assertEquals(customShoppingListItem, mapper.convert(customShoppingListItemResponseDto));
    }

    @Test
    void convert_nullCustomShoppingListItemResponseDto_throwsNullPointerException() {
        CustomShoppingListItemResponseDto nullCustomShoppingListItemResponseDto = null;
        assertThrows(NullPointerException.class, () -> mapper.convert(nullCustomShoppingListItemResponseDto));
    }

    @Test
    void convert_emptyCustomShoppingListItemResponseDto_returnsEmptyCustomShoppingListItem() {
        CustomShoppingListItemResponseDto emptyCustomShoppingListItemResponseDto =
            new CustomShoppingListItemResponseDto();
        assertEquals(new CustomShoppingListItem().setStatus(null),
            mapper.convert(emptyCustomShoppingListItemResponseDto));
    }

    @Test
    void mapAllToList_validCustomShoppingListItemResponseDto_returnsCustomShoppingListItem() {
        List<CustomShoppingListItemResponseDto> customShoppingListItemResponseDtos = Arrays.asList(
            new CustomShoppingListItemResponseDto()
                .setId(5L)
                .setStatus(ShoppingListItemStatus.ACTIVE)
                .setText("List item №1"),
            customShoppingListItemResponseDto);

        List<CustomShoppingListItem> expected = Arrays.asList(
            new CustomShoppingListItem()
                .setId(5L)
                .setText("List item №1"),
            customShoppingListItem);
        assertEquals(expected, mapper.mapAllToList(customShoppingListItemResponseDtos));
    }

    @Test
    void mapAllToList_nullCustomShoppingListItemResponseDto_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.mapAllToList(null));
    }
}
