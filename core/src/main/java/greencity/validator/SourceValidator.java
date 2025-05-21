package greencity.validator;

import greencity.annotations.ValidSource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class SourceValidator implements ConstraintValidator<ValidSource, String> {
    private static final Set<String> VALID_SOURCES = Set.of("ALL", "GREENCITY", "PICKUP");

    @Override
    public void initialize(ValidSource constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return VALID_SOURCES.contains(s.toUpperCase());
    }
}
