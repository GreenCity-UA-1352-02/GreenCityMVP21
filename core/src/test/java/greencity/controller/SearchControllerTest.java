package greencity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;
    @Mock
    private Validator validator;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(validator)
                .build();
    }

    @Test
    void search_ValidSearchQuery_ReturnsSearchResults() throws Exception {
        String searchQuery = "Title";
        Locale locale = Locale.ENGLISH;

        SearchResponseDto expectedResponse = SearchResponseDto.builder()
                .ecoNews(List.of())
                .countOfResults(0L)
                .build();

        when(searchService.search(searchQuery, locale.getLanguage())).thenReturn(expectedResponse);

        mockMvc.perform(get("/search")
                        .param("searchQuery", searchQuery)
                        .param("locale", locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(searchService).search(searchQuery, locale.getLanguage());
    }

    @Test
    void searchEcoNews_ValidSearchQueryWithPagination_ReturnsPaginatedResults() throws Exception {
        String searchQuery = "Eco news title";
        Locale locale = Locale.ENGLISH;
        PageableDto<SearchNewsDto> expectedResponse = new PageableDto<>(List.of(), 0, 0, 1);

        when(searchService.searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage())))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/search/econews")
                        .param("searchQuery", searchQuery)
                        .param("locale", locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(searchService).searchAllNews(any(Pageable.class), eq(searchQuery), eq(locale.getLanguage()));
    }

    @Test
    void search_MissingSearchQuery_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchEcoNews_MissingSearchQuery_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/search/econews")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_WithDefaultLocale_PassesLocaleToService() throws Exception {
        String searchQuery = "Eco news";

        SearchResponseDto expectedResponse = SearchResponseDto.builder()
                .ecoNews(List.of())
                .countOfResults(0L)
                .build();

        when(searchService.search(eq(searchQuery), any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(get("/search")
                        .param("searchQuery", searchQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(searchService).search(eq(searchQuery), any(String.class));
    }
}