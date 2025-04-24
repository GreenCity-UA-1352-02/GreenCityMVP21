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

public class CustomShoppingListResponseDtoMapperTest {
    private final CustomShoppingListResponseDtoMapper mapper = new CustomShoppingListResponseDtoMapper();
    private final CustomShoppingListItem customShoppingListItem = getCustomShoppingListItem();
    private final CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
        getCustomShoppingListItemResponseDto();

    @Test
    void convert_validCustomShoppingListItem_returnsCustomShoppingListItemResponseDto() {
        assertEquals(customShoppingListItemResponseDto, mapper.convert(customShoppingListItem));
    }

    @Test
    void convert_nullCustomShoppingListItem_throwsNullPointerException() {
        CustomShoppingListItem nullCustomShoppingListItem = null;
        assertThrows(NullPointerException.class, () -> mapper.convert(nullCustomShoppingListItem));
    }

    @Test
    void convert_emptyCustomShoppingListItem_returnsEmptyCustomShoppingListItemResponseDto() {
        CustomShoppingListItem emptyCustomShoppingListItem = new CustomShoppingListItem();
        assertEquals(new CustomShoppingListItemResponseDto().setStatus(ShoppingListItemStatus.ACTIVE),
            mapper.convert(emptyCustomShoppingListItem));
    }

    @Test
    void mapAllToList_validCustomShoppingListItem_returnsListOfCustomShoppingListItemResponseDto() {
        List<CustomShoppingListItem> customShoppingListItemList = Arrays.asList(
            new CustomShoppingListItem()
                .setId(5L)
                .setText("List item №1"),
            customShoppingListItem);
        List<CustomShoppingListItemResponseDto> expected = Arrays.asList(
            new CustomShoppingListItemResponseDto()
                .setId(5L)
                .setStatus(ShoppingListItemStatus.ACTIVE)
                .setText("List item №1"),
            customShoppingListItemResponseDto);
        assertEquals(expected, mapper.mapAllToList(customShoppingListItemList));
    }

    @Test
    void mapAllToList_nullCustomShoppingListItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.mapAllToList(null));
    }
}
