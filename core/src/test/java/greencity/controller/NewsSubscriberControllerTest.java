package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.service.NewsSubscriberService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static greencity.ModelUtils.getObjectMapper;
import static greencity.TestConst.EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsSubscriberControllerTest {
    @Mock
    private NewsSubscriberService newsSubscriberService;

    @InjectMocks
    private NewsSubscriberController newsSubscriberController;

    @Mock
    private MockMvc mockMvc;

    ObjectMapper objectMapper = getObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriberController)
            .setValidator(localValidatorFactoryBean)
            .build();
    }

    @Test
    @SneakyThrows
    void post_ValidInputData_Ok() {
        String json = """
            {
              "email": "%s"
            }
            """.formatted(EMAIL);

        NewsSubscriberRequestDto response = objectMapper.readValue(json, NewsSubscriberRequestDto.class);
        when(newsSubscriberService.subscribe(any(NewsSubscriberRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/newsSubscriber")
            .contentType("application/json")
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(EMAIL));

        verify(newsSubscriberService, times(1)).subscribe(any(NewsSubscriberRequestDto.class));
    }

    @Test
    @SneakyThrows
    void post_InvalidEmail_BadRequest() {
        String json = """
            {
              "email": "invalid-email"
            }
            """;

        mockMvc.perform(post("/newsSubscriber")
            .contentType("application/json")
            .content(json))
            .andExpect(status().isBadRequest());

        verify(newsSubscriberService, never()).subscribe(any(NewsSubscriberRequestDto.class));
    }
}