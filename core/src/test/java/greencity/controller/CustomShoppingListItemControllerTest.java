package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomShoppingListItemController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getAllAvailableCustomShoppingListItems_Success() throws Exception {
        Long userId = 1L;
        Long habitId = 1L;

        CustomShoppingListItemResponseDto item1 = new CustomShoppingListItemResponseDto(1L, "Item 1", ShoppingListItemStatus.ACTIVE);
        CustomShoppingListItemResponseDto item2 = new CustomShoppingListItemResponseDto(2L, "Item 2", ShoppingListItemStatus.ACTIVE);
        List<CustomShoppingListItemResponseDto> itemList = Arrays.asList(item1, item2);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId)).thenReturn(itemList);

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/{habitId}", userId, habitId).accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Item 2"));

        verify(customShoppingListItemService, times(1)).findAllAvailableCustomShoppingListItems(userId, habitId);
    }

    @Test
    public void getAllAvailableCustomShoppingListItems_BadRequest() throws Exception {
        mockMvc.perform(get("/custom/shopping-list-items/invalidString/1")
                        .accept("application/json"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void saveUserCustomShoppingListItems_Success() throws Exception {
        Long userId = 1L;
        Long habitAssignId = 1L;

        CustomShoppingListItemSaveRequestDto customShoppingListItemSaveRequestDto =
                new CustomShoppingListItemSaveRequestDto("itemName");
        BulkSaveCustomShoppingListItemDto dto = new BulkSaveCustomShoppingListItemDto(
                Collections.singletonList(customShoppingListItemSaveRequestDto));

        String content = new ObjectMapper().writeValueAsString(dto);

        CustomShoppingListItemResponseDto responseDto = new CustomShoppingListItemResponseDto(
                10L, "itemName", ShoppingListItemStatus.ACTIVE);

        when(customShoppingListItemService.save(dto, userId, habitAssignId))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(post("/custom/shopping-list-items/{userId}/{habitAssignId}/custom-shopping-list-items", userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].text").value("itemName"));

        verify(customShoppingListItemService, times(1)).save(dto, userId, habitAssignId);
    }

    @Test
    public void updateItemStatus_Success() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        String status = "INPROGRESS";

        CustomShoppingListItemResponseDto dto = new CustomShoppingListItemResponseDto();
        dto.setId(itemId);
        dto.setText("Item Text");
        dto.setStatus(ShoppingListItemStatus.INPROGRESS);

        when(customShoppingListItemService.updateItemStatus(userId, itemId, status)).thenReturn(dto);

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("itemId", itemId.toString())
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.text").value("Item Text"))
                .andExpect(jsonPath("$.status").value(status));

        verify(customShoppingListItemService, times(1)).updateItemStatus(userId, itemId, status);
    }

    @Test
    public void updateItemStatusToDone_Success() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        doNothing().when(customShoppingListItemService).updateItemStatusToDone(userId, itemId);

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("itemId", itemId.toString()))
                .andExpect(status().isOk());

        verify(customShoppingListItemService, times(1)).updateItemStatusToDone(userId, itemId);
    }

    @Test
    public void bulkDeleteCustomShoppingListItems_Success() throws Exception {
        Long userId = 1L;
        String ids = "1,2,3";
        List<Long> deleteIds = Arrays.asList(1L, 2L, 3L);

        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(deleteIds);

        mockMvc.perform(delete("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2))
                .andExpect(jsonPath("$[2]").value(3));

        verify(customShoppingListItemService, times(1)).bulkDelete(ids);
    }

    @Test
    public void getAllCustomShoppingItemsByStatus_Success() throws Exception {
        Long userId = 1L;
        String status = "ACTIVE";
        CustomShoppingListItemResponseDto itemResponse = new CustomShoppingListItemResponseDto();
        itemResponse.setId(1L);
        itemResponse.setText("Item Text");
        itemResponse.setStatus(ShoppingListItemStatus.ACTIVE);

        List<CustomShoppingListItemResponseDto> responceList = Collections.singletonList(itemResponse);

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status)).thenReturn(responceList);

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Item Text"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(customShoppingListItemService, times(1)).findAllUsersCustomShoppingListItemsByStatus(userId, status);
    }
}

