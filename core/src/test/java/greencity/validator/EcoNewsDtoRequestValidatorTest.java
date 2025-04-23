package greencity.validator;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


class EcoNewsDtoRequestValidatorTest {

    private EcoNewsDtoRequestValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EcoNewsDtoRequestValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"https://eco-lavca.ua/"})
    void isValid_ValidSources_ReturnsTrue(String source) {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource(source);
        assertTrue(validator.isValid(request, context));
    }


    @Test
    void isValid_EmptyTags_ThrowsException() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest("title", "text",
                Collections.emptyList(), null, null, "shortInfo");

        WrongCountOfTagsException exception = assertThrows(
                WrongCountOfTagsException.class,
                () -> validator.isValid(request, context)
        );

        assertEquals(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION, exception.getMessage());
    }

    @Test
    void isValid_TooManyTags_ThrowsException() {
        List<String> tags = List.of("tag1", "tag2", "tag3", "tag4");
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest("title", "text",
                tags, null, null, "shortInfo");

        WrongCountOfTagsException exception = assertThrows(
                WrongCountOfTagsException.class,
                () -> validator.isValid(request, context)
        );

        assertEquals(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION, exception.getMessage());
    }

    @Test
    void isValid_InvalidSourceUrl_ThrowsException() {
        AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();
        request.setSource("invalid-url");

        assertThrows(InvalidURLException.class, () -> validator.isValid(request, null));
    }
}
