package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class EcoNewsVOMapperTest {

    @InjectMocks
    private EcoNewsVOMapper mapper;

    @Test
    void convert_FromEcoNewsToEcoNewsVO_FullData() {

        User author = User.builder().id(1L).name("John Doe").userStatus(UserStatus.ACTIVATED).role(Role.ROLE_USER).build();
        Language enLanguage = Language.builder().id(1L).code("en").build();
        Language uaLanguage = Language.builder().id(2L).code("ua").build();

        List<Tag> tags = new ArrayList<>();
        Tag tag1 = Tag.builder().id(10L).build();
        tag1.setTagTranslations(List.of(
                TagTranslation.builder().id(100L).name("news").language(enLanguage).tag(tag1).build(),
                TagTranslation.builder().id(101L).name("новини").language(uaLanguage).tag(tag1).build()
        ));
        Tag tag2 = Tag.builder().id(11L).build();
        tag2.setTagTranslations(List.of(
                TagTranslation.builder().id(102L).name("environment").language(enLanguage).tag(tag2).build(),
                TagTranslation.builder().id(103L).name("довкілля").language(uaLanguage).tag(tag2).build()
        ));
        tags.add(tag1);
        tags.add(tag2);

        Set<User> likedUsers = Set.of(User.builder().id(2L).build(), User.builder().id(3L).build());
        Set<User> dislikedUsers = Set.of(User.builder().id(4L).build());

        List<EcoNewsComment> comments = new ArrayList<>();
        EcoNewsComment comment1 = EcoNewsComment.builder().id(15L).createdDate(LocalDateTime.now().minusDays(2)).currentUserLiked(true).deleted(false).text("Comment 1").modifiedDate(LocalDateTime.now().minusDays(1)).user(User.builder().id(5L).name("Commenter 1").userStatus(UserStatus.CREATED).role(Role.ROLE_MODERATOR).build()).build();
        EcoNewsComment comment2 = EcoNewsComment.builder().id(16L).createdDate(LocalDateTime.now().minusDays(5)).currentUserLiked(false).deleted(true).text("Comment 2").modifiedDate(LocalDateTime.now().minusDays(4)).user(User.builder().id(6L).name("Commenter 2").userStatus(UserStatus.BLOCKED).role(Role.ROLE_ADMIN).build()).build();
        comments.add(comment1);
        comments.add(comment2);

        EcoNews ecoNews = EcoNews.builder()
                .id(5L)
                .author(author)
                .creationDate(ZonedDateTime.now().minusDays(3))
                .imagePath("/images/test.jpg")
                .source("Test Source")
                .text("Test content")
                .title("Test Title")
                .tags(tags)
                .usersLikedNews(likedUsers)
                .usersDislikedNews(dislikedUsers)
                .ecoNewsComments(comments)
                .build();

        EcoNewsVO ecoNewsVO = mapper.convert(ecoNews);

        assertNotNull(ecoNewsVO);
        assertEquals(ecoNews.getId(), ecoNewsVO.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsVO.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsVO.getAuthor().getName());
        assertEquals(ecoNews.getAuthor().getUserStatus(), ecoNewsVO.getAuthor().getUserStatus());
        assertEquals(ecoNews.getAuthor().getRole(), ecoNewsVO.getAuthor().getRole());
        assertEquals(ecoNews.getCreationDate(), ecoNewsVO.getCreationDate());
        assertEquals(ecoNews.getImagePath(), ecoNewsVO.getImagePath());
        assertEquals(ecoNews.getSource(), ecoNewsVO.getSource());
        assertEquals(ecoNews.getText(), ecoNewsVO.getText());
        assertEquals(ecoNews.getTitle(), ecoNewsVO.getTitle());
        assertEquals(ecoNews.getTags().size(), ecoNewsVO.getTags().size());
        assertEquals(ecoNews.getUsersLikedNews().size(), ecoNewsVO.getUsersLikedNews().size());
        assertEquals(ecoNews.getUsersDislikedNews().size(), ecoNewsVO.getUsersDislikedNews().size());
        assertEquals(ecoNews.getEcoNewsComments().size(), ecoNewsVO.getEcoNewsComments().size());

        assertEquals(ecoNews.getTags().getFirst().getId(), ecoNewsVO.getTags().getFirst().getId());
        assertEquals(ecoNews.getTags().getFirst().getTagTranslations().size(), ecoNewsVO.getTags().getFirst().getTagTranslations().size());
        assertEquals(ecoNews.getTags().getFirst().getTagTranslations().getFirst().getName(), ecoNewsVO.getTags().getFirst().getTagTranslations().getFirst().getName());
        assertEquals(ecoNews.getTags().getFirst().getTagTranslations().getFirst().getLanguage().getCode(), ecoNewsVO.getTags().getFirst().getTagTranslations().getFirst().getLanguageVO().getCode());

        assertEquals(ecoNews.getUsersLikedNews().stream().map(User::getId).collect(Collectors.toSet()),
                ecoNewsVO.getUsersLikedNews().stream().map(UserVO::getId).collect(Collectors.toSet()));

        assertEquals(ecoNews.getUsersDislikedNews().stream().map(User::getId).collect(Collectors.toSet()),
                ecoNewsVO.getUsersDislikedNews().stream().map(UserVO::getId).collect(Collectors.toSet()));

        assertEquals(ecoNews.getEcoNewsComments().getFirst().getId(), ecoNewsVO.getEcoNewsComments().getFirst().getId());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().getCreatedDate(), ecoNewsVO.getEcoNewsComments().getFirst().getCreatedDate());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().isCurrentUserLiked(), ecoNewsVO.getEcoNewsComments().getFirst().isCurrentUserLiked());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().isDeleted(), ecoNewsVO.getEcoNewsComments().getFirst().isDeleted());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().getText(), ecoNewsVO.getEcoNewsComments().getFirst().getText());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().getModifiedDate(), ecoNewsVO.getEcoNewsComments().getFirst().getModifiedDate());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().getUser().getId(), ecoNewsVO.getEcoNewsComments().getFirst().getUser().getId());
        assertEquals(ecoNews.getEcoNewsComments().getFirst().getUser().getName(), ecoNewsVO.getEcoNewsComments().getFirst().getUser().getName());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsVO_MinimalData() {

        User author = User.builder().id(1L).name("John Doe").build();
        EcoNews ecoNews = EcoNews.builder()
                .id(5L)
                .author(author)
                .creationDate(ZonedDateTime.now().minusDays(3))
                .title("Test Title")
                .text("Test content")
                .tags(Collections.emptyList())
                .usersLikedNews(Collections.emptySet())
                .usersDislikedNews(Collections.emptySet())
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsVO ecoNewsVO = mapper.convert(ecoNews);

        assertNotNull(ecoNewsVO);
        assertEquals(ecoNews.getId(), ecoNewsVO.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsVO.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsVO.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), ecoNewsVO.getCreationDate());
        assertEquals(ecoNews.getTitle(), ecoNewsVO.getTitle());
        assertEquals(ecoNews.getText(), ecoNewsVO.getText());
        assertEquals(Collections.emptyList(), ecoNewsVO.getTags());
        assertEquals(Collections.emptySet(), ecoNewsVO.getUsersLikedNews());
        assertEquals(Collections.emptySet(), ecoNewsVO.getUsersDislikedNews());
        assertEquals(Collections.emptyList(), ecoNewsVO.getEcoNewsComments());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsVO_WithNullOptionalFields() {

        User author = User.builder().id(1L).name("John Doe").build();
        EcoNews ecoNews = EcoNews.builder()
                .id(5L)
                .author(author)
                .creationDate(ZonedDateTime.now().minusDays(3))
                .title("Test Title")
                .text("Test content")
                .tags(Collections.emptyList())
                .imagePath(null)
                .source(null)
                .usersLikedNews(Collections.emptySet())
                .usersDislikedNews(Collections.emptySet())
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsVO ecoNewsVO = mapper.convert(ecoNews);

        assertNotNull(ecoNewsVO);
        assertEquals(ecoNews.getId(), ecoNewsVO.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsVO.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsVO.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), ecoNewsVO.getCreationDate());
        assertEquals(ecoNews.getTitle(), ecoNewsVO.getTitle());
        assertEquals(ecoNews.getText(), ecoNewsVO.getText());
        assertEquals(Collections.emptyList(), ecoNewsVO.getTags());
        assertEquals(Collections.emptySet(), ecoNewsVO.getUsersLikedNews());
        assertEquals(Collections.emptySet(), ecoNewsVO.getUsersDislikedNews());
        assertEquals(Collections.emptyList(), ecoNewsVO.getEcoNewsComments());
        assertEquals(ecoNews.getImagePath(), ecoNewsVO.getImagePath());
        assertEquals(ecoNews.getSource(), ecoNewsVO.getSource());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsVO_WithoutTagsAndComments() {

        User author = User.builder().id(1L).name("John Doe").build();
        EcoNews ecoNews = EcoNews.builder()
                .id(5L)
                .author(author)
                .creationDate(ZonedDateTime.now().minusDays(3))
                .title("Test Title")
                .text("Test content")
                .usersLikedNews(Set.of(User.builder().id(2L).build()))
                .usersDislikedNews(Set.of(User.builder().id(3L).build()))
                .tags(Collections.emptyList())
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsVO ecoNewsVO = mapper.convert(ecoNews);

        assertNotNull(ecoNewsVO);
        assertEquals(ecoNews.getId(), ecoNewsVO.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsVO.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsVO.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), ecoNewsVO.getCreationDate());
        assertEquals(ecoNews.getTitle(), ecoNewsVO.getTitle());
        assertEquals(ecoNews.getText(), ecoNewsVO.getText());
        assertEquals(Collections.emptyList(), ecoNewsVO.getTags());
        assertEquals(ecoNews.getUsersLikedNews().size(), ecoNewsVO.getUsersLikedNews().size());
        assertEquals(ecoNews.getUsersDislikedNews().size(), ecoNewsVO.getUsersDislikedNews().size());
        assertEquals(Collections.emptyList(), ecoNewsVO.getEcoNewsComments());
    }
}
