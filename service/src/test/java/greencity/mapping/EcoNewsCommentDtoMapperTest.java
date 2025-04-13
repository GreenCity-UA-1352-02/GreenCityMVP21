package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EcoNewsCommentDtoMapperTest {

    private EcoNewsCommentDtoMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsCommentDtoMapper();
    }

    @Test
    void convert_DeletedComment_ReturnsDtoWithDeletedStatus() {

        EcoNewsComment ecoNewsComment = mock(EcoNewsComment.class);
        when(ecoNewsComment.isDeleted()).thenReturn(true);

        EcoNewsCommentDto dto = mapper.convert(ecoNewsComment);

        assertEquals(CommentStatus.DELETED, dto.getStatus());
        verify(ecoNewsComment, times(1)).isDeleted();
    }

    @Test
    void convert_OriginalComment_ReturnsDtoWithOriginalStatus() {

        EcoNewsComment ecoNewsComment = mock(EcoNewsComment.class);
        when(ecoNewsComment.isDeleted()).thenReturn(false);
        LocalDateTime createdDate = LocalDateTime.now();
        when(ecoNewsComment.getCreatedDate()).thenReturn(createdDate);
        when(ecoNewsComment.getModifiedDate()).thenReturn(createdDate);
        when(ecoNewsComment.getText()).thenReturn("This is a comment");

        User user = mock(User.class);
        when(ecoNewsComment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("John Doe");
        when(user.getProfilePicturePath()).thenReturn("/path/to/profile/pic");

        Set<User> likedUsers = new HashSet<>();
        when(ecoNewsComment.getUsersLiked()).thenReturn(likedUsers);
        when(ecoNewsComment.isCurrentUserLiked()).thenReturn(false);

        EcoNewsCommentDto dto = mapper.convert(ecoNewsComment);

        assertEquals(CommentStatus.ORIGINAL, dto.getStatus());
        assertEquals("This is a comment", dto.getText());
        assertEquals(1L, dto.getAuthor().getId());
        assertEquals("John Doe", dto.getAuthor().getName());
        assertEquals("/path/to/profile/pic", dto.getAuthor().getUserProfilePicturePath());
        assertEquals(0, dto.getLikes());
        assertFalse(dto.isCurrentUserLiked());
    }

    @Test
    void convert_EditedComment_ReturnsDtoWithEditedStatus() {

        EcoNewsComment ecoNewsComment = mock(EcoNewsComment.class);

        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime modifiedDate = LocalDateTime.now();

        when(ecoNewsComment.isDeleted()).thenReturn(false);
        when(ecoNewsComment.getCreatedDate()).thenReturn(createdDate);
        when(ecoNewsComment.getModifiedDate()).thenReturn(modifiedDate);
        when(ecoNewsComment.getText()).thenReturn("This is an edited comment");

        User user = mock(User.class);
        when(ecoNewsComment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("John Doe");
        when(user.getProfilePicturePath()).thenReturn("/path/to/profile/pic");

        Set<User> likedUsers = new HashSet<>();
        when(ecoNewsComment.getUsersLiked()).thenReturn(likedUsers);
        when(ecoNewsComment.isCurrentUserLiked()).thenReturn(false);

        EcoNewsCommentDto dto = mapper.convert(ecoNewsComment);

        assertEquals(CommentStatus.EDITED, dto.getStatus());
        assertEquals("This is an edited comment", dto.getText());
        assertEquals(1L, dto.getAuthor().getId());
        assertEquals("John Doe", dto.getAuthor().getName());
        assertEquals("/path/to/profile/pic", dto.getAuthor().getUserProfilePicturePath());
        assertEquals(0, dto.getLikes());
        assertFalse(dto.isCurrentUserLiked());
    }

    @Test
    void convert_ValidComment_ReturnsDtoWithCorrectData() {

        EcoNewsComment ecoNewsComment = mock(EcoNewsComment.class);
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime modifiedDate = LocalDateTime.now();

        when(ecoNewsComment.isDeleted()).thenReturn(false);
        when(ecoNewsComment.getCreatedDate()).thenReturn(createdDate);
        when(ecoNewsComment.getModifiedDate()).thenReturn(modifiedDate);
        when(ecoNewsComment.getText()).thenReturn("This is a comment");

        User user = mock(User.class);
        when(ecoNewsComment.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("John Doe");
        when(user.getProfilePicturePath()).thenReturn("/path/to/profile/pic");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2L);
        when(user2.getName()).thenReturn("Jane Smith");
        when(user2.getProfilePicturePath()).thenReturn("/path/to/another/pic");

        Set<User> usersLiked = new HashSet<>();
        usersLiked.add(user);
        usersLiked.add(user2);
        when(ecoNewsComment.getUsersLiked()).thenReturn(usersLiked);

        when(ecoNewsComment.isCurrentUserLiked()).thenReturn(true);

        EcoNewsCommentDto dto = mapper.convert(ecoNewsComment);

        System.out.println("Liked users count: " + ecoNewsComment.getUsersLiked().size());

        assertEquals("This is a comment", dto.getText());
        assertEquals(1L, dto.getAuthor().getId());
        assertEquals("John Doe", dto.getAuthor().getName());
        assertEquals("/path/to/profile/pic", dto.getAuthor().getUserProfilePicturePath());
        assertEquals(2, dto.getLikes());
        assertTrue(dto.isCurrentUserLiked());
        assertEquals(CommentStatus.EDITED, dto.getStatus());
    }

    @Test
    void convert_NullComment_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((EcoNewsComment) null));
    }
}
