package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.AppConstant;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.UpdateUserShoppingListDto;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {

    private final String baseUrl = "/habit/assign";
    private final long habitId = 1L;

    private MockMvc mockMvc;

    @Mock
    private HabitAssignService habitAssignService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HabitAssignController habitAssignController;

    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
            .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .build();
    }

    @Test
    @SneakyThrows
    void assignDefault_ValidHabitId_Created() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post("%s/{habitId}".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(habitAssignService, times(1))
                .assignDefaultHabitForUser(habitId, userVO);
    }

    @Test
    @SneakyThrows
    void assignCustom_ValidHabitIdAndRequestBody_Created() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String json = """
                {
                  "habitAssignPropertiesDto":\
                 {
                    "duration": %d,
                    "defaultShoppingListItems": []
                  },
                  "friendsIdsList": []
                }""".formatted(AppConstant.MIN_DAYS_DURATION);

        mockMvc.perform(post("%s/{habitId}/custom".formatted(baseUrl), habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .principal(principal))
                .andExpect(status().isCreated());

        HabitAssignCustomPropertiesDto dto = objectMapper.readValue(json, HabitAssignCustomPropertiesDto.class);

        verify(habitAssignService, times(1))
                .assignCustomHabitForUser(habitId, userVO, dto);
    }

    @Test
    @SneakyThrows
    void updateHabitAssignDuration_ValidHabitIdAndDuration_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        int minDaysDuration = AppConstant.MIN_DAYS_DURATION;

        mockMvc.perform(put("%s/{habitAssignId}/update-habit-duration".formatted(baseUrl), habitId)
                        .principal(principal)
                        .param("duration", String.valueOf(minDaysDuration)))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitId, userVO.getId(), minDaysDuration);
    }

    @Test
    @SneakyThrows
    void getHabitAssignTest() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/{habitAssignId}".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getByHabitAssignIdAndUserId(habitId, userVO.getId(), "en");
    }

    @Test
    @SneakyThrows
    void getCurrentUserHabitAssignsByIdAndAcquired_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/allForCurrentUser".formatted(baseUrl))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), "en");
    }

    @Test
    @SneakyThrows
    void getUserShoppingAndCustomShoppingListsTest() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/{habitAssignId}/allUserAndCustomList".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getUserShoppingAndCustomShoppingLists(userVO.getId(), habitId, "en");
    }

    @Test
    @SneakyThrows
    void updateUserAndCustomShoppingLists_ValidHabitIdAndRequestBody_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        @Language("JSON") String json = """
                {
                  "userShoppingListItemDto": [],
                  "customShoppingListItemDto": []
                }""";

        mockMvc.perform(put("%s/{habitAssignId}/allUserAndCustomList".formatted(baseUrl), habitId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        UserShoppingAndCustomShoppingListsDto dto = objectMapper.readValue(json, UserShoppingAndCustomShoppingListsDto.class);

        verify(habitAssignService, times(1)).fullUpdateUserAndCustomShoppingLists(userVO.getId(), habitId, dto, "en");
    }

    @Test
    @SneakyThrows
    void getListOfUserAndCustomShoppingListsInprogress_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/allUserAndCustomShoppingListsInprogress".formatted(baseUrl))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), "en");
    }

    @Test
    @SneakyThrows
    void getAllHabitAssignsByHabitIdAndAcquiredTest() {
        mockMvc.perform(get("%s/{habitId}/all".formatted(baseUrl), habitId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, "en");
    }

    @Test
    @SneakyThrows
    void getHabitAssignByHabitId_ValidHabitId_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/{habitId}/active".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, "en");
    }

    @Test
    @SneakyThrows
    void getUsersHabitByHabitAssignIdTest() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get("%s/{habitAssignId}/more".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitByUserIdAndHabitAssignId(userVO.getId(), habitId, "en");
    }

    @Test
    @SneakyThrows
    void updateAssignByHabitId_ValidHabitIdAndRequestBody_Ok() {
        @Language("JSON")
        String json = """
            {
              "status": "INPROGRESS"
            }
            """;

        mockMvc.perform(patch("%s/{habitAssignId}".formatted(baseUrl), habitId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        HabitAssignStatDto dto = objectMapper.readValue(json, HabitAssignStatDto.class);

        verify(habitAssignService, times(1)).updateStatusByHabitAssignId(habitId, dto);
    }

    @Test
    @SneakyThrows
    void enrollHabit_ValidHabitIdAndDate_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        LocalDate now = LocalDate.now();

        mockMvc.perform(post("%s/{habitAssignId}/enroll/{date}".formatted(baseUrl), habitId, now.format(DateTimeFormatter.ISO_DATE))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).enrollHabit(habitId, userVO.getId(), now, "en");
    }

    @Test
    @SneakyThrows
    void unenrollHabit_ValidHabitIdAndDate_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        LocalDate now = LocalDate.now();

        mockMvc.perform(post("%s/{habitAssignId}/unenroll/{date}".formatted(baseUrl), habitId, now.format(DateTimeFormatter.ISO_DATE))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).unenrollHabit(habitId, userVO.getId(), now);
    }

    @Test
    @SneakyThrows
    void getInprogressHabitAssignOnDateTest() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        LocalDate now = LocalDate.now();

        mockMvc.perform(get("%s/active/{date}".formatted(baseUrl), now.format(DateTimeFormatter.ISO_DATE))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findInprogressHabitAssignsOnDate(userVO.getId(), now, "en");
    }

    @Test
    @SneakyThrows
    void getHabitAssignBetweenDates_ValidFromAndToDates_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();

        mockMvc.perform(get("%s/activity/{from}/to/{to}".formatted(baseUrl), from.format(DateTimeFormatter.ISO_DATE), to.format(DateTimeFormatter.ISO_DATE))
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitAssignsBetweenDates(userVO.getId(), from, to, "en");
    }

    @Test
    @SneakyThrows
    void cancelHabitAssign_ValidHabitId_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch("%s/cancel/{habitId}".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).cancelHabitAssign(habitId, userVO.getId());
    }

    @Test
    @SneakyThrows
    void deleteHabitAssign_ValidHabitId_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete("%s/delete/{habitAssignId}".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).deleteHabitAssign(habitId, userVO.getId());
    }

    @Test
    @SneakyThrows
    void updateShoppingListStatus_ValidHabitIdAndRequestBody_Ok() {
        @Language("JSON")
        String json = """
            {
              "habitAssignId": 1,
              "userShoppingListItemId": 1,
              "userShoppingListAdvanceDto": []
            }
            """;

        mockMvc.perform(put("%s/saveShoppingListForHabitAssign".formatted(baseUrl))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .principal(principal))
            .andExpect(status().isOk());

        UpdateUserShoppingListDto dto = objectMapper.readValue(json, UpdateUserShoppingListDto.class);

        verify(habitAssignService, times(1)).updateUserShoppingListItem(dto);
    }

    @Test
    @SneakyThrows
    void updateProgressNotificationHasDisplayed_ValidHabitId_Ok() {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put("%s/{habitAssignId}/updateProgressNotificationHasDisplayed".formatted(baseUrl), habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateProgressNotificationHasDisplayed(habitId, userVO.getId());
    }

}