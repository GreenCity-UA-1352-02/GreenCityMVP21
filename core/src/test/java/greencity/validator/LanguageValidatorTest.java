package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageValidator languageValidator;

    @BeforeEach
    void setUp() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en"));
        languageValidator.initialize(null);
    }

    @Test
    void isValid_SupportedLocale_True() {
        Locale locale = Locale.ENGLISH;
        assertTrue(languageValidator.isValid(locale, null));
    }

    @Test
    void isValid_UnsupportedLocale_False() {
        Locale locale = Locale.GERMANY;
        assertFalse(languageValidator.isValid(locale, null));
    }

    @Test
    void isValid_NullLocale_ExceptionThrown() {
        assertThrows(NullPointerException.class, () -> languageValidator.isValid(null, null));
    }

    @Test
    void isValid_EmptyLocale_False() {
        Locale locale = Locale.of("");
        assertFalse(languageValidator.isValid(locale, null));
    }

}