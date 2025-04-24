package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitStatisticControllerTest {
    private static final String baseUrl = "/habit/statistic";
    private MockMvc mockMvc;

    @Mock
    private HabitStatisticService habitStatisticService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;



    private Principal principal = getPrincipal();

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    public void findAllByHabitId_habitExists_returns200() throws Exception {
        mockMvc.perform(get(baseUrl + "/{habitId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitStatisticService, times(1)).findAllStatsByHabitId(1L);
    }

    @Test
    public void findAllStatsByHabitAssignId_habitAssignExist_returns200() throws Exception {
        mockMvc.perform(get(baseUrl + "/assign/{habitAssignId}", 1L))
                .andExpect(status().isOk());
        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(1L);
    }

    @Test
    public void saveHabitStatistic_createHabitStatistic_returns201() throws Exception {
        UserVO userVO = new UserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        String content = "{\n" +
                "  \"amountOfItems\": 16,\n" +
                "  \"habitRate\": \"" + HabitRate.DEFAULT + "\",\n" +
                "  \"createDate\": \"" + zonedDateTime + "\"\n" +
                "}";

        mockMvc.perform(post(baseUrl + "/{habitId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        AddHabitStatisticDto addHabitStatisticDto = mapper.readValue(content, AddHabitStatisticDto.class);

        verify(userService).findByEmail(anyString());
        verify(habitStatisticService).saveByHabitIdAndUserId(1L, userVO.getId(), addHabitStatisticDto);
    }

    @Test
    void updateStatistic_updateHabitStatistic_returns200() throws Exception {
        UserVO userVO = new UserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        String content = "{\n" +
                "  \"amountOfItems\": 14,\n" +
                "  \"habitRate\": \"" + HabitRate.GOOD + "\"\n" +
                "}";

        mockMvc.perform(put(baseUrl + "/{id}", 3L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        UpdateHabitStatisticDto habitStatisticForUpdateDto = mapper.readValue(content, UpdateHabitStatisticDto.class);

        verify(userService).findByEmail(anyString());
        verify(habitStatisticService).update(3L, userVO.getId(), habitStatisticForUpdateDto);

    }

    @Test
    void getTodayStatisticsForAllHabitItems_statisticsForUaLocale_returns200() throws Exception {
        Locale locale = new Locale("ua");
        mockMvc.perform(get(baseUrl + "/todayStatisticsForAllHabitItems")
                        .locale(locale))
                .andExpect(status().isOk());
        verify(habitStatisticService, times(1)).getTodayStatisticsForAllHabitItems("ua");
    }

    @Test
    void findAmountOfAcquiredHabits_countAcquiredHabits_returns200() throws Exception {
        mockMvc.perform(get(baseUrl + "/acquired/count?userId=" + 1L))
                .andExpect(status().isOk());
        verify(habitStatisticService, times(1)).getAmountOfAcquiredHabitsByUserId(1L);
    }

    @Test
    void findAmountOfHabitsInProgress_countInProgressHabits_returns200() throws Exception {
        mockMvc.perform(get(baseUrl + "/in-progress/count?userId=" + 1L))
                .andExpect(status().isOk());
        verify(habitStatisticService, times(1)).getAmountOfHabitsInProgressByUserId(1L);
    }
}
