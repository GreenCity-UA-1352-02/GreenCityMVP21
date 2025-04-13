package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class EcoNewsCommentVOMapperTest {

    @InjectMocks
    private EcoNewsCommentVOMapper mapper;

    @Test
    void convert_FromEcoNewsCommentToEcoNewsCommentVO_WithoutParentComment() {

        User user = User.builder()
                .id(1L)
                .role(Role.ROLE_USER)
                .name("Test User")
                .build();

        EcoNews ecoNews = EcoNews.builder()
                .id(10L)
                .build();

        Set<User> likedUsers = new HashSet<>();
        likedUsers.add(User.builder().id(2L).build());
        likedUsers.add(User.builder().id(3L).build());

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
                .id(5L)
                .user(user)
                .modifiedDate(LocalDateTime.now().minusDays(2))
                .parentComment(null)
                .text("Test comment")
                .deleted(false)
                .currentUserLiked(true)
                .createdDate(LocalDateTime.now().minusDays(5))
                .usersLiked(likedUsers)
                .ecoNews(ecoNews)
                .build();

        EcoNewsCommentVO commentVO = mapper.convert(ecoNewsComment);

        assertEquals(ecoNewsComment.getId(), commentVO.getId());
        assertEquals(ecoNewsComment.getUser().getId(), commentVO.getUser().getId());
        assertEquals(ecoNewsComment.getUser().getRole(), commentVO.getUser().getRole());
        assertEquals(ecoNewsComment.getUser().getName(), commentVO.getUser().getName());
        assertEquals(ecoNewsComment.getModifiedDate(), commentVO.getModifiedDate());
        assertNull(commentVO.getParentComment());
        assertEquals(ecoNewsComment.getText(), commentVO.getText());
        assertEquals(ecoNewsComment.isDeleted(), commentVO.isDeleted());
        assertEquals(ecoNewsComment.isCurrentUserLiked(), commentVO.isCurrentUserLiked());
        assertEquals(ecoNewsComment.getCreatedDate(), commentVO.getCreatedDate());
        assertEquals(ecoNewsComment.getUsersLiked().size(), commentVO.getUsersLiked().size());
        assertEquals(ecoNewsComment.getUsersLiked().stream().map(User::getId).collect(Collectors.toSet()),
                commentVO.getUsersLiked().stream().map(UserVO::getId).collect(Collectors.toSet()));
        assertEquals(ecoNewsComment.getEcoNews().getId(), commentVO.getEcoNews().getId());
    }

    @Test
    void convert_FromEcoNewsCommentToEcoNewsCommentVO_WithParentComment() {

        User user = User.builder()
                .id(1L)
                .role(Role.ROLE_USER)
                .name("Test User")
                .build();

        EcoNews ecoNews = EcoNews.builder()
                .id(10L)
                .build();

        User parentUser = User.builder()
                .id(4L)
                .role(Role.ROLE_MODERATOR)
                .name("Parent User")
                .build();

        EcoNewsComment parentComment = EcoNewsComment.builder()
                .id(6L)
                .user(parentUser)
                .modifiedDate(LocalDateTime.now().minusDays(3))
                .parentComment(null)
                .text("Parent comment")
                .deleted(false)
                .currentUserLiked(false)
                .createdDate(LocalDateTime.now().minusDays(6))
                .usersLiked(new HashSet<>())
                .ecoNews(ecoNews)
                .build();

        EcoNewsComment ecoNewsComment = EcoNewsComment.builder()
                .id(5L)
                .user(user)
                .modifiedDate(LocalDateTime.now().minusDays(2))
                .parentComment(parentComment)
                .text("Test comment with parent")
                .deleted(false)
                .currentUserLiked(true)
                .createdDate(LocalDateTime.now().minusDays(5))
                .usersLiked(new HashSet<>())
                .ecoNews(ecoNews)
                .build();

        EcoNewsCommentVO commentVO = mapper.convert(ecoNewsComment);

        assertEquals(ecoNewsComment.getId(), commentVO.getId());
        assertEquals(ecoNewsComment.getUser().getId(), commentVO.getUser().getId());
        assertEquals(ecoNewsComment.getUser().getRole(), commentVO.getUser().getRole());
        assertEquals(ecoNewsComment.getUser().getName(), commentVO.getUser().getName());
        assertEquals(ecoNewsComment.getModifiedDate(), commentVO.getModifiedDate());
        assertEquals(parentComment.getId(), commentVO.getParentComment().getId());
        assertEquals(parentComment.getUser().getId(), commentVO.getParentComment().getUser().getId());
        assertEquals(parentComment.getUser().getRole(), commentVO.getParentComment().getUser().getRole());
        assertEquals(parentComment.getUser().getName(), commentVO.getParentComment().getUser().getName());
        assertNull(commentVO.getParentComment().getParentComment());
        assertEquals(ecoNewsComment.getText(), commentVO.getText());
        assertEquals(ecoNewsComment.isDeleted(), commentVO.isDeleted());
        assertEquals(ecoNewsComment.isCurrentUserLiked(), commentVO.isCurrentUserLiked());
        assertEquals(ecoNewsComment.getCreatedDate(), commentVO.getCreatedDate());
        assertEquals(ecoNewsComment.getEcoNews().getId(), commentVO.getEcoNews().getId());
    }
}
