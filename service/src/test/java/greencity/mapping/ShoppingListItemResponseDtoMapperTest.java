package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemResponseDtoMapperTest {

    @InjectMocks
    private ShoppingListItemResponseDtoMapper mapper;

    @Test
    void convert() {

        Long itemId = 20L;
        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
            .id(itemId)
            .translations(List.of(
                ShoppingListItemTranslation.builder().id(1L).content("Молоко").build(),
                ShoppingListItemTranslation.builder().id(2L).content("Хлеб").build()))
            .build();

        ShoppingListItemResponseDto responseDto = mapper.convert(shoppingListItem);

        assertNotNull(responseDto);
        assertEquals(itemId, responseDto.getId());
        assertNotNull(responseDto.getTranslations());
        assertEquals(2, responseDto.getTranslations().size());

        ShoppingListItemTranslationDTO translation1 = responseDto.getTranslations().get(0);
        assertEquals(1L, translation1.getId());
        assertEquals("Молоко", translation1.getContent());

        ShoppingListItemTranslationDTO translation2 = responseDto.getTranslations().get(1);
        assertEquals(2L, translation2.getId());
        assertEquals("Хлеб", translation2.getContent());
    }

    @Test
    void convertWithNoTranslations() {

        Long itemId = 25L;
        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
            .id(itemId)
            .translations(Collections.emptyList())
            .build();

        ShoppingListItemResponseDto responseDto = mapper.convert(shoppingListItem);

        assertNotNull(responseDto);
        assertEquals(itemId, responseDto.getId());
        assertNotNull(responseDto.getTranslations());
        assertEquals(0, responseDto.getTranslations().size());
    }

    @Test
    void convertWithNullTranslations() {

        Long itemId = 30L;
        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
            .id(itemId)
            .translations(null)
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> mapper.convert(shoppingListItem));
    }

    @Test
    void convertWithSingleTranslation() {

        Long itemId = 35L;
        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
            .id(itemId)
            .translations(List.of(
                ShoppingListItemTranslation.builder().id(3L).content("Яблоки").build()))
            .build();

        ShoppingListItemResponseDto responseDto = mapper.convert(shoppingListItem);

        assertNotNull(responseDto);
        assertEquals(itemId, responseDto.getId());
        assertNotNull(responseDto.getTranslations());
        assertEquals(1, responseDto.getTranslations().size());

        ShoppingListItemTranslationDTO translation = responseDto.getTranslations().get(0);
        assertEquals(3L, translation.getId());
        assertEquals("Яблоки", translation.getContent());
    }
}
