package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EcoNewsDtoMapperTest {

    @InjectMocks
    private EcoNewsDtoMapper mapper;

    @Test
    void convert_FromEcoNewsToEcoNewsDto_DefaultLanguage() {

        User author = User.builder().id(1L).name("John Doe").build();
        Language enLanguage = Language.builder().code(AppConstant.DEFAULT_LANGUAGE_CODE).build();
        Language uaLanguage = Language.builder().code("ua").build();

        List<Tag> tags = new ArrayList<>();
        Tag tag1 = Tag.builder().id(10L).build();
        tag1.setTagTranslations(List.of(
                TagTranslation.builder().name("news").language(enLanguage).build(),
                TagTranslation.builder().name("новини").language(uaLanguage).build()
        ));
        Tag tag2 = Tag.builder().id(11L).build();
        tag2.setTagTranslations(List.of(
                TagTranslation.builder().name("environment").language(enLanguage).build(),
                TagTranslation.builder().name("довкілля").language(uaLanguage).build()
        ));
        tags.add(tag1);
        tags.add(tag2);

        Set<User> likedUsers = new HashSet<>();
        likedUsers.add(User.builder().id(2L).build());
        likedUsers.add(User.builder().id(3L).build());

        Set<User> dislikedUsers = new HashSet<>();
        dislikedUsers.add(User.builder().id(4L).build());

        List<EcoNewsComment> comments = new ArrayList<>();
        comments.add(EcoNewsComment.builder().id(15L).deleted(false).build());
        comments.add(EcoNewsComment.builder().id(16L).deleted(true).build());
        comments.add(EcoNewsComment.builder().id(17L).deleted(false).build());

        EcoNews ecoNews = EcoNews.builder()
                .id(5L)
                .author(author)
                .text("Test content")
                .creationDate(ZonedDateTime.now().minusDays(3))
                .imagePath("/images/test.jpg")
                .usersLikedNews(likedUsers)
                .shortInfo("Short test info")
                .tags(tags)
                .usersDislikedNews(dislikedUsers)
                .title("Test Title")
                .ecoNewsComments(comments)
                .build();

        EcoNewsDto ecoNewsDto = mapper.convert(ecoNews);

        assertEquals(ecoNews.getId(), ecoNewsDto.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsDto.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsDto.getAuthor().getName());
        assertEquals(ecoNews.getText(), ecoNewsDto.getContent());
        assertEquals(ecoNews.getCreationDate(), ecoNewsDto.getCreationDate());
        assertEquals(ecoNews.getImagePath(), ecoNewsDto.getImagePath());
        assertEquals(ecoNews.getUsersLikedNews().size(), ecoNewsDto.getLikes());
        assertEquals(ecoNews.getShortInfo(), ecoNewsDto.getShortInfo());
        assertEquals(List.of("news", "environment"), ecoNewsDto.getTags());
        assertEquals(List.of("новини", "довкілля"), ecoNewsDto.getTagsUa());
        assertEquals(ecoNews.getUsersDislikedNews().size(), ecoNewsDto.getDislikes());
        assertEquals(ecoNews.getTitle(), ecoNewsDto.getTitle());
        assertEquals(2, ecoNewsDto.getCountComments());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsDto_NoTags() {

        User author = User.builder().id(6L).name("Jane Smith").build();
        EcoNews ecoNews = EcoNews.builder()
                .id(6L)
                .author(author)
                .text("Another content")
                .creationDate(ZonedDateTime.of(LocalDateTime.now().minusHours(10), ZoneId.of("Europe/Kyiv")))
                .imagePath(null)
                .usersLikedNews(Collections.emptySet())
                .shortInfo("Another short info")
                .tags(Collections.emptyList())
                .usersDislikedNews(Collections.emptySet())
                .title("Another Title")
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsDto ecoNewsDto = mapper.convert(ecoNews);

        assertEquals(ecoNews.getId(), ecoNewsDto.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsDto.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsDto.getAuthor().getName());
        assertEquals(ecoNews.getText(), ecoNewsDto.getContent());
        assertEquals(ecoNews.getCreationDate(), ecoNewsDto.getCreationDate());
        assertEquals(ecoNews.getImagePath(), ecoNewsDto.getImagePath());
        assertEquals(0, ecoNewsDto.getLikes());
        assertEquals(ecoNews.getShortInfo(), ecoNewsDto.getShortInfo());
        assertEquals(Collections.emptyList(), ecoNewsDto.getTags());
        assertEquals(Collections.emptyList(), ecoNewsDto.getTagsUa());
        assertEquals(0, ecoNewsDto.getDislikes());
        assertEquals(ecoNews.getTitle(), ecoNewsDto.getTitle());
        assertEquals(0, ecoNewsDto.getCountComments());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsDto_NoComments() {

        User author = User.builder().id(7L).name("Peter Pan").build();
        EcoNews ecoNews = EcoNews.builder()
                .id(8L)
                .author(author)
                .text("Content without comments")
                .creationDate(ZonedDateTime.of(LocalDateTime.now().minusDays(1), ZoneId.of("Europe/Kyiv")))
                .imagePath("/image.png")
                .usersLikedNews(Set.of(User.builder().id(9L).build()))
                .shortInfo("Info without comments")
                .tags(Collections.emptyList())
                .usersDislikedNews(Set.of(User.builder().id(10L).build()))
                .title("Title without comments")
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsDto ecoNewsDto = mapper.convert(ecoNews);

        assertEquals(ecoNews.getId(), ecoNewsDto.getId());
        assertEquals(ecoNews.getAuthor().getId(), ecoNewsDto.getAuthor().getId());
        assertEquals(ecoNews.getAuthor().getName(), ecoNewsDto.getAuthor().getName());
        assertEquals(ecoNews.getText(), ecoNewsDto.getContent());
        assertEquals(ecoNews.getCreationDate(), ecoNewsDto.getCreationDate());
        assertEquals(ecoNews.getImagePath(), ecoNewsDto.getImagePath());
        assertEquals(1, ecoNewsDto.getLikes());
        assertEquals(ecoNews.getShortInfo(), ecoNewsDto.getShortInfo());
        assertEquals(Collections.emptyList(), ecoNewsDto.getTags());
        assertEquals(Collections.emptyList(), ecoNewsDto.getTagsUa());
        assertEquals(1, ecoNewsDto.getDislikes());
        assertEquals(ecoNews.getTitle(), ecoNewsDto.getTitle());
        assertEquals(0, ecoNewsDto.getCountComments());
    }

    @Test
    void convert_FromEcoNewsToEcoNewsDto_OnlyDefaultLanguageTags() {

        User author = User.builder().id(11L).name("Alice").build();
        Language enLanguage = Language.builder().code(AppConstant.DEFAULT_LANGUAGE_CODE).build();
        List<Tag> tags = new ArrayList<>();
        Tag tag = Tag.builder().id(20L).build();
        tag.setTagTranslations(List.of(TagTranslation.builder().name("global").language(enLanguage).build()));
        tags.add(tag);

        EcoNews ecoNews = EcoNews.builder()
                .id(12L)
                .author(author)
                .text("Default lang tags")
                .creationDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Kyiv")))
                .imagePath("/image2.png")
                .usersLikedNews(Collections.singleton(User.builder().id(13L).build()))
                .shortInfo("Default tags info")
                .tags(tags)
                .usersDislikedNews(Collections.emptySet())
                .title("Default Tags Title")
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsDto ecoNewsDto = mapper.convert(ecoNews);

        assertEquals(List.of("global"), ecoNewsDto.getTags());
        assertEquals(Collections.emptyList(), ecoNewsDto.getTagsUa());
    }


    @Test
    void convert_FromEcoNewsToEcoNewsDto_OnlyUaLanguageTags() {

        User author = User.builder().id(14L).name("Bob").build();
        Language uaLanguage = Language.builder().code("ua").build();
        List<Tag> tags = new ArrayList<>();
        Tag tag = Tag.builder().id(21L).build();
        tag.setTagTranslations(List.of(TagTranslation.builder().name("світ").language(uaLanguage).build()));
        tags.add(tag);

        EcoNews ecoNews = EcoNews.builder()
                .id(15L)
                .author(author)
                .text("Ua lang tags")
                .creationDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Kyiv")))
                .imagePath("/image3.png")
                .usersLikedNews(Collections.emptySet())
                .shortInfo("Ua tags info")
                .tags(tags)
                .usersDislikedNews(Collections.singleton(User.builder().id(16L).build()))
                .title("Ua Tags Title")
                .ecoNewsComments(Collections.emptyList())
                .build();

        EcoNewsDto ecoNewsDto = mapper.convert(ecoNews);

        assertEquals(Collections.emptyList(), ecoNewsDto.getTags());
        assertEquals(List.of("світ"), ecoNewsDto.getTagsUa());
    }
}
