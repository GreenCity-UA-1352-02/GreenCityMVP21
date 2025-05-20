package greencity.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventCommentController.class)
@Import(SecurityConfig.class)
@ContextConfiguration(classes = GreenCityApplication.class)
class EventCommentControllerTest {
    private static final String EVENT_COMMENT_CONTROLLER_LINK = "/events/comments";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventCommentService eventCommentService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;


    @MockBean
    private JwtTool jwtTool;

    @Test
    @WithMockUser(username = "test@gmail.com")
    void saveComment() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        String content = "{ \"text\": \"string\" }";

        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEventCommentDtoRequest request = mapper.readValue(content, AddEventCommentDtoRequest.class);

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).save(1L, request, userVO);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void saveBadRequest() throws Exception {
        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveComment_unauthorized_shouldReturnUnauthorized() throws Exception {
        String content = "{ \"text\": \"comment text\" }";

        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(EVENT_COMMENT_CONTROLLER_LINK + "?id=1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).deleteById(1L, userVO);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteTest_BadRequest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(EVENT_COMMENT_CONTROLLER_LINK)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_unauthorized_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete(EVENT_COMMENT_CONTROLLER_LINK + "?id=1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteComment_AsAdmin_ShouldAllow() throws Exception {
        Long commentId = 1L;
        mockMvc.perform(delete("/events/comments")
                .param("id", commentId.toString()))
            .andExpect(status().isNoContent()); // або .isOk(), залежить від реалізації
    }

    @Test
    @WithMockUser(username = "ownerUser")
    void deleteComment_AsOwner_ShouldAllow() throws Exception {
        Long commentId = 1L;
        when(eventCommentService.isCommentOwner(eq(commentId), any()))
            .thenReturn(true);

        mockMvc.perform(delete("/events/comments")
                .param("id", commentId.toString()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "randomUser")
    void deleteComment_AsNotOwner_ShouldDeny() throws Exception {
        Long commentId = 1L;
        when(eventCommentService.isCommentOwner(eq(commentId), any()))
            .thenReturn(false);

        mockMvc.perform(delete("/events/comments")
                .param("id", commentId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateTest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(EVENT_COMMENT_CONTROLLER_LINK + "?id=1&text=text"))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).update("text", 1L, userVO);
    }

    @Test
    void updateComment_unauthorized_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch(EVENT_COMMENT_CONTROLLER_LINK + "?id=1&text=text")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void update_badRequest() throws Exception {
        UserVO userVO = ModelUtils.getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(EVENT_COMMENT_CONTROLLER_LINK + "?id=1"))
            .andExpect(status().isBadRequest());
    }
}
