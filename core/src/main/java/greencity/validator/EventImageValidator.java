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
        long maxSizeBytes = 10L * 1024 * 1024;
        if (multipartFiles == null || multipartFiles.isEmpty() || multipartFiles.size() > 5) {
            return false;
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile == null || multipartFile.getSize() > maxSizeBytes) {
                return false;
            }
            if (!validType.contains(multipartFile.getContentType())) {
                return false;
            }
        }

        return true;
    }
}
