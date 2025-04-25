package greencity.validator;

import greencity.annotations.EventTimeValidation;
import greencity.dto.event.EventDateDetailsDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;

public class EventTimeValidator implements ConstraintValidator<EventTimeValidation, EventDateDetailsDto> {
    @Override
    public void initialize(EventTimeValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EventDateDetailsDto dto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }

        ZonedDateTime start = dto.getStartTime();
        ZonedDateTime end = dto.getEndTime();
        ZonedDateTime now = ZonedDateTime.now();

        if (start != null && start.isBefore(now)) {
            return false;
        }
        if (dto.isAllDay()) {
            return true;
        }
        if (end != null && end.isBefore(start)) {
            return false;
        }
        return true;
    }
}
