package greencity.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import greencity.service.LanguageService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {
    private static final String languageLink = "/language";
    private MockMvc mockMvc;

    @InjectMocks
    private LanguageController languageController;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController).build();
    }

    @Test
    void findAllLanguageCodes_LanguagesExist_ReturnsListOfCodes() throws Exception {
        List<String> expectedLanguages = Arrays.asList("en", "ua", "fr");
        when(languageService.findAllLanguageCodes()).thenReturn(expectedLanguages);

        mockMvc.perform(get(languageLink)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("en"))
            .andExpect(jsonPath("$[1]").value("ua"))
            .andExpect(jsonPath("$[2]").value("fr"));

        verify(languageService).findAllLanguageCodes();
    }

    @Test
    void findAllLanguageCodes_EmptyList_ReturnsEmptyJsonArray() throws Exception {
        List<String> expectedLanguages = List.of();
        when(languageService.findAllLanguageCodes()).thenReturn(expectedLanguages);

        mockMvc.perform(get(languageLink)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(languageService).findAllLanguageCodes();
    }
}