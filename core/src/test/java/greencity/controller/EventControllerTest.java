package greencity.controller;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.dto.event.EventResponse;
import greencity.service.EventService;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Principal principal = ModelUtils.getPrincipal();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }


    @Test
    void create_shouldReturnCreated() throws Exception {
        String requestJson = """
                {
                  "title": "Spring Boot Workshop",
                  "description": "Let's talk about Spring Boot in detail.",
                  "open": true,
                  "datesLocations": [
                    {
                      "id": 1,
                      "startDate": "2026-06-01T10:00:00Z",
                      "finishDate": "2026-07-01T12:00:00Z",
                      "coordinates": {
                        "id": 1,
                        "latitude": 50.4501,
                        "longitude": 30.5234
                      },
                      "onlineLink": "https://example.com/event"
                    }
                  ],
                  "tags": ["Social"]
                }
            """;

        String responseJson = """
                {
                  "id": 1,
                  "additionalImages": [],
                  "dates": [
                    {
                      "id": 1,
                      "startDate": "2026-06-01T10:00:00Z",
                      "finishDate": "2026-07-01T12:00:00Z",
                      "coordinates": {
                        "id": 1,
                        "latitude": 50.4501,
                        "longitude": 30.5234
                      },
                      "onlineLink": "https://example.com/event"
                    }
                  ],
                  "description": "Let's talk about Spring Boot in detail.",
                  "open": true,
                  "organizer": {
                    "id": 1,
                    "name": "John Doe",
                    "organizerRating": 4.5
                  },
                  "tags": [
                    {
                      "id": 1,
                      "nameUa": "Соціальний",
                      "nameEn": "Social"
                    }
                  ],
                  "title": "Spring Boot Workshop",
                  "titleImage": "Link"
                }
            """;

        EventResponse response = objectMapper.readValue(responseJson, EventResponse.class);

        when(eventService.save(any(), anyList(), anyString())).thenReturn(response);

        MockMultipartFile json = new MockMultipartFile(
            "addEventRequest",
            "",
            "application/json",
            requestJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile image = new MockMultipartFile(
            "images",
            "image.jpg",
            "image/jpeg",
            "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/events/create")
                .file(json)
                .file(image)
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Spring Boot Workshop"))
            .andExpect(jsonPath("$.description").value("Let's talk about Spring Boot in detail."))
            .andExpect(jsonPath("$.open").value(true));

        verify(eventService).save(any(), anyList(), anyString());
    }

    @Test
    void create_shouldReturnBadRequest_whenDateInPast() throws Exception {
        String invalidJson = """
            {
              "title": "Invalid Event",
              "description": "Too old event",
              "open": true,
              "datesLocations": [
                {
                  "id": 0,
                  "startDate": "2000-01-01T10:00:00Z",
                  "finishDate": "2000-01-01T12:00:00Z",
                  "coordinates": {
                    "id": 1,
                    "latitude": 50.4501,
                    "longitude": 30.5234
                  },
                  "onlineLink": null
                }
              ],
              "tags": ["tag1", "tag2"]
            }
            """;

        MockMultipartFile json = new MockMultipartFile(
            "addEventRequest", "", "application/json", invalidJson.getBytes()
        );

        MockMultipartFile image = new MockMultipartFile(
            "images", "image.jpg", "image/jpeg", "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/events/create")
                .file(json)
                .file(image)
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnBadRequest_whenInvalidImageFormat() throws Exception {
        String validJson = """
            {
              "title": "Image Format Test",
              "description": "Valid description",
              "open": true,
              "datesLocations": [
                {
                  "id": 0,
                  "startDate": "2099-01-01T10:00:00Z",
                  "finishDate": "2099-01-01T12:00:00Z",
                  "coordinates": {
                    "id": 1,
                    "latitude": 50.4501,
                    "longitude": 30.5234
                  },
                  "onlineLink": null
                }
              ],
              "tags": ["tag1"]
            }
            """;

        MockMultipartFile json = new MockMultipartFile(
            "addEventRequest", "", "application/json", validJson.getBytes()
        );

        MockMultipartFile invalidImage = new MockMultipartFile(
            "images", "malicious.exe", "application/octet-stream", "bad-content".getBytes()
        );

        mockMvc.perform(multipart("/events/create")
                .file(json)
                .file(invalidImage)
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnOk_whenValidData() throws Exception {
        String updateJson = """
            {
              "id": 1,
              "title": "Updated Event",
              "description": "Updated description for event",
              "open": true,
              "datesLocations": [
                {
                  "id": 1,
                  "startDate": "2026-06-01T10:00:00Z",
                  "finishDate": "2026-07-01T12:00:00Z",
                  "coordinates": {
                    "id": 1,
                    "latitude": 50.4501,
                    "longitude": 30.5234
                  },
                  "onlineLink": "https://example.com/event"
                }
              ],
              "tags": ["Social"]
            }
            """;

        String responseJson = """
            {
              "id": 1,
              "title": "Updated Event",
              "description": "Updated description for event",
              "open": true,
              "dates": [
                {
                  "id": 1,
                  "startDate": "2026-06-01T10:00:00Z",
                  "finishDate": "2026-07-01T12:00:00Z",
                  "coordinates": {
                    "id": 1,
                    "latitude": 50.4501,
                    "longitude": 30.5234
                  },
                  "onlineLink": "https://example.com/event"
                }
              ],
              "tags": [
                {
                  "id": 1,
                  "nameUa": "Cоціальний",
                  "nameEn": "Social"
                }
              ],
              "titleImage": "UpdatedImageLink"
            }
            """;

        EventResponse response = objectMapper.readValue(responseJson, EventResponse.class);

        when(eventService.update(any(), any())).thenReturn(response);

        MockMultipartFile json = new MockMultipartFile(
            "updateEventRequest", "", "application/json", updateJson.getBytes()
        );


        mockMvc.perform(multipart("/events/update")
                .file(json)
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Event"))
            .andExpect(jsonPath("$.description").value("Updated description for event"));
        verify(eventService).update(any(), any());
    }

    @Test
    void update_shouldReturnBadRequest_whenInvalidData() throws Exception {
        String invalidJson = """
            {
              "id": 1,
              "title": "",
              "description": "",
              "open": true,
              "datesLocations": [],
              "tags": []
            }
            """;

        MockMultipartFile json = new MockMultipartFile(
            "updateEventRequest", "", "application/json", invalidJson.getBytes()
        );

        mockMvc.perform(multipart("/events/update")
                .file(json)
                .principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturnOk_whenEventExists() throws Exception {
        long eventId = 1L;

        doNothing().when(eventService).delete(eventId);

        mockMvc.perform(delete("/events/delete/{eventId}", eventId)
                .principal(principal))
            .andExpect(status().isOk());

        verify(eventService).delete(eventId);
    }
}
