package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageValidatorTest {
    private ImageValidator imageValidator;
    private ConstraintValidatorContext context;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        imageValidator = new ImageValidator();
        context = mock(ConstraintValidatorContext.class);
        file = mock(MultipartFile.class);
    }

    @Test
    void isValid_WhenFileIsNull_ShouldReturnTrue() {
        assertTrue(imageValidator.isValid(null, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/jpeg", "image/jpg"})
    void isValid_WhenContentTypeValid_ShouldReturnTrue(String imageType) {
        when(file.getContentType()).thenReturn(imageType);
        assertTrue(imageValidator.isValid(file, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/gif", "application/pdf", "text/plain"})
    void isValid_WhenContentTypeInvalid_ShouldReturnFalse(String imageType) {
        when(file.getContentType()).thenReturn(imageType);
        assertFalse(imageValidator.isValid(file, context));
    }

    @Test
    void isValid_WhenContentTypeNull_ShouldReturnFalse() {
        when(file.getContentType()).thenReturn(null);
        assertFalse(imageValidator.isValid(file, context));
    }
}
