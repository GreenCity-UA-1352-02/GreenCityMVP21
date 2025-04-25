package greencity.validator;

import greencity.annotations.EventTypeValidation;
import greencity.dto.event.EventDateDetailsDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventTypeValidator implements ConstraintValidator<EventTypeValidation, EventDateDetailsDto> {
    @Override
    public void initialize(EventTypeValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EventDateDetailsDto dto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }
        if (Boolean.TRUE.equals(dto.isOnline()) || Boolean.TRUE.equals(dto.isPlace())) {
            return false;
        }
        return true;
    }
}
