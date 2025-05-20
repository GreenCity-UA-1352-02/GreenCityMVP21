package greencity.validator;

import greencity.annotations.EventTimeValidation;
import greencity.dto.event.EventDateLocationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;

public class EventTimeValidator implements ConstraintValidator<EventTimeValidation, EventDateLocationDto> {
    @Override
    public void initialize(EventTimeValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EventDateLocationDto dto,
        ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }

        ZonedDateTime start = dto.startDate();
        ZonedDateTime end = dto.finishDate();
        ZonedDateTime now = ZonedDateTime.now();

        return !(start != null && start.isBefore(now) || end != null && end.isBefore(start));
    }
}
