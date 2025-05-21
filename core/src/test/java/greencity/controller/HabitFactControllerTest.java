package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitFactControllerTest {
    private static final String habitFactLink = "/facts";
    private MockMvc mockMvc;

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private HabitFactController habitFactController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator mockValidator;


    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setValidator(mockValidator)
                .build();
    }

    @Test
    void getRandomFactByHabitIdTest_randomFactForEnLocale_returns200() throws Exception {

        mockMvc.perform(get(habitFactLink + "/random/{habitId}", 1)).andExpect(status().isOk());
        verify(habitFactService, times(1)).getRandomHabitFactByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void getHabitFactOfTheDay_habitFactByLangId_returns200() throws Exception {
        mockMvc.perform(get(habitFactLink + "/dayFact/{languageId}", 1)).andExpect(status().isOk());

        verify(habitFactService, times(1)).getHabitFactOfTheDay(1L);
    }

    @Test
    void getAllHabitFacts_getAllFacts_returns200() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(habitFactLink + "?page=1")).andExpect(status().isOk());

        verify(habitFactService).getAllHabitFacts(pageable, "en");
    }

    @Test
    void save_saveFact_returns201() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("test.User@gmail.com");

        String json = "{\n" +
                "  \"translations\": [\n" +
                "    {\n" +
                "      \"language\": {\n" +
                "        \"id\": 1,\n" +
                "        \"code\": \"ua\"\n" +
                "      },\n" +
                "      \"content\": \"test1\"\n" +
                "    }" +
                "  ],\n" +
                "  \"habit\": {\n" +
                "    \"id\": 1\n" +
                "  }\n" +
                "}";

        this.mockMvc.perform(post(habitFactLink)
                        .content(json)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        ObjectMapper mapper = new ObjectMapper();
        HabitFactPostDto fact = mapper.readValue(json, HabitFactPostDto.class);

        verify(habitFactService).save(eq(fact));
    }

    @Test
    void update_updateHabitFact_returns200() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("test.User@gmail.com");

        String json = "{\n" +
                "  \"translations\": [\n" +
                "    {\n" +
                "      \"language\": {\n" +
                "        \"id\": 1,\n" +
                "        \"code\": \"ua\"\n" +
                "      },\n" +
                "      \"content\": \"test1\"\n" +
                "    }\n" +
                "],\n" +
                "  \"habit\": {\n" +
                "    \"id\": 1\n" +
                "  }\n" +
                "}";

        this.mockMvc.perform(put(habitFactLink + "/{habitId}", 1)
                        .content(json)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        HabitFactUpdateDto fact = mapper.readValue(json, HabitFactUpdateDto.class);

        verify(habitFactService).update(fact, 1L);
    }

    @Test
    void delete_habitFactDeleteById_returns200() throws Exception {
        mockMvc.perform(delete(habitFactLink + "/{habitId}", 1))
                .andExpect(status().isOk());

        verify(habitFactService).delete(1L);
    }

    @Test
    void delete_habitFactDeleteWrongId_returns400() throws Exception {
        Mockito.when(habitFactService.delete(1L)).thenThrow(NotDeletedException.class);
        mockMvc.perform(delete(habitFactLink + "/{habitId}", 1))
                .andExpect(status().is(400));

        verify(habitFactService).delete(1L);
    }
}
