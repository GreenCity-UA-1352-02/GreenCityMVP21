package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SearchNewsDtoMapperTest {
    private SearchNewsDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SearchNewsDtoMapper();
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    void convert_ShouldMapEcoNewsToSearchNewsDto_WithCorrectLanguageTags() {

        User user = User.builder()
            .id(1L)
            .name("John Doe")
            .build();

        Language english = new Language();
        english.setCode("en");

        TagTranslation translation = new TagTranslation();
        translation.setLanguage(english);
        translation.setName("Environment");

        Tag tag = new Tag();
        tag.setTagTranslations(List.of(translation));

        EcoNews ecoNews = EcoNews.builder()
            .id(100L)
            .title("Save the Earth")
            .creationDate(ZonedDateTime.now())
            .author(user)
            .tags(List.of(tag))
            .build();

        SearchNewsDto result = mapper.convert(ecoNews);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Save the Earth", result.getTitle());
        assertEquals(ZonedDateTime.from(ecoNews.getCreationDate()), result.getCreationDate());
        assertEquals(new EcoNewsAuthorDto(1L, "John Doe"), result.getAuthor());
        assertEquals(List.of("Environment"), result.getTags());
    }

    @Test
    void convert_ShouldReturnEmptyTags_WhenNoMatchingLanguage() {

        User user = User.builder()
            .id(1L)
            .name("Jane")
            .build();

        Language french = new Language();
        french.setCode("fr");

        TagTranslation translation = new TagTranslation();
        translation.setLanguage(french);
        translation.setName("Environnement");

        Tag tag = new Tag();
        tag.setTagTranslations(List.of(translation));

        EcoNews ecoNews = EcoNews.builder()
            .id(101L)
            .title("Climate Action")
            .creationDate(ZonedDateTime.now())
            .author(user)
            .tags(List.of(tag))
            .build();

        LocaleContextHolder.setLocale(Locale.ENGLISH);

        SearchNewsDto result = mapper.convert(ecoNews);

        assertNotNull(result);
        assertEquals(0, result.getTags().size());
    }
}