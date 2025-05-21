package greencity.validator;

import greencity.annotations.EventImageValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class EventImageValidator implements ConstraintValidator<EventImageValidation, List<MultipartFile>> {
    private final List<String> validType = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFiles == null || multipartFiles.isEmpty() || multipartFiles.size() > 5) {
            return false;
        }

        return multipartFiles.stream()
            .allMatch(file -> file != null && validType.contains(file.getContentType()));
    }
}
