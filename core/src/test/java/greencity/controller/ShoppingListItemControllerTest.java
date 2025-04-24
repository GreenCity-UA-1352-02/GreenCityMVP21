package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.Validator;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShoppingListItemControllerTest {
    private static final String SHOPPING_LIST_ITEM_LINK = "/user/shopping-list-items";
    private MockMvc mockMvc;
    @InjectMocks
    ShoppingListItemController shoppingListItemController;
    @Mock
    ShoppingListItemService shoppingListItemService;
    @Mock
    UserService userService;
    @Mock
    private Validator validator;
    @Mock
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(shoppingListItemController)
            .setCustomArgumentResolvers(
                new UserArgumentResolver(userService, modelMapper))
            .setValidator(validator)
            .build();
    }

    @Test
    void saveUserShoppingListItem_ValidRequest_ReturnsCreatedStatus() throws Exception {
        Long habitId = 1L;
        Locale locale = new Locale("en");
        UserVO userVO = getUserVO();
        List<ShoppingListItemRequestDto> dto = List.of(new ShoppingListItemRequestDto(1L));
        List<UserShoppingListItemResponseDto> responseDto = List.of(getUserShoppingListItemResponseDto());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.saveUserShoppingListItems(userVO.getId(), habitId, dto, locale.getLanguage()))
            .thenReturn(responseDto);

        mockMvc.perform(post(SHOPPING_LIST_ITEM_LINK)
            .principal(principal)
            .param("habitId", habitId.toString())
            .locale(locale)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        verify(shoppingListItemService).saveUserShoppingListItems(userVO.getId(), habitId, dto, locale.getLanguage());
    }

    @Test
    void getShoppingListItemsAssignedToUser_ValidHabitIdAndUser_ReturnsShoppingListItems() throws Exception {
        Long habitId = 1L;
        Locale locale = new Locale("en");
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(SHOPPING_LIST_ITEM_LINK + "/" + "habits/" + habitId + "/shopping-list", habitId)
            .principal(principal)
            .locale(locale))
            .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), habitId, locale.getLanguage());
    }

    @Test
    void deleteUserShoppingListItem_ValidRequest_ReturnStatusOk() throws Exception {
        Long habitId = 1L;
        Long shoppingListItemId = 5L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(SHOPPING_LIST_ITEM_LINK)
            .principal(principal)
            .param("habitId", habitId.toString())
            .param("shoppingListItemId", shoppingListItemId.toString()))
            .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(
            shoppingListItemId, userVO.getId(), habitId);
    }

    @Test
    void deleteUserShoppingListItem_MissingHabitId_ReturnsBadRequest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(SHOPPING_LIST_ITEM_LINK)
            .principal(principal)
            .param("shoppingListItemId", "testValue"))
            .andExpect(status().isBadRequest());

        verify(shoppingListItemService, never()).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(any(), any(),
            any());
    }

    @Test
    void updateUserShoppingListItem_ValidRequest_ReturnsStatusCreated() throws Exception {
        UserVO userVO = getUserVO();
        Locale locale = new Locale("en");

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(SHOPPING_LIST_ITEM_LINK + "/{userShoppingListItemId}", 1)
            .principal(principal)
            .locale(locale))
            .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(userVO.getId(), 1L, locale.getLanguage());
    }

    @Test
    void updateUserShoppingListItemStatus_ValidRequest_ReturnsStatusOk() throws Exception {
        UserVO userVO = getUserVO();
        Locale locale = new Locale("en");
        String status = "Active";

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(SHOPPING_LIST_ITEM_LINK + "/{userShoppingListItemId}/status/{status}", 1, status)
            .principal(principal)
            .locale(locale))
            .andExpect(status().isOk());

        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(), 1L, locale.getLanguage(),
            status);
    }

    @Test
    void bulkDeleteUserShoppingListItems_ValidIds_ReturnsStatusOk() throws Exception {
        String ids = "1,2,3";
        List<Long> deletedIds = List.of(1L, 2L, 3L);
        UserVO userVO = getUserVO();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.deleteUserShoppingListItems(ids)).thenReturn(deletedIds);

        mockMvc.perform(delete(SHOPPING_LIST_ITEM_LINK + "/user-shopping-list-items")
            .principal(principal)
            .param("ids", ids))
            .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItems(ids);
    }

    @Test
    void findInProgressByUserId_ValidRequest_ReturnsStatusOk() throws Exception {
        UserVO userVO = getUserVO();
        Locale locale = new Locale("en");
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(SHOPPING_LIST_ITEM_LINK + "/{userId}/get-all-inprogress", userVO.getId())
            .param("lang", locale.getLanguage()));

        verify(shoppingListItemService).findInProgressByUserIdAndLanguageCode(userVO.getId(), locale.getLanguage());
    }

    @Test
    void findInProgressByUserId_MissingLangParam_ReturnsBadRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get(SHOPPING_LIST_ITEM_LINK + "/{userId}/get-all-inprogress", userId))
            .andExpect(status().isBadRequest());

        verify(shoppingListItemService, never()).findInProgressByUserIdAndLanguageCode(anyLong(), anyString());
    }
}
