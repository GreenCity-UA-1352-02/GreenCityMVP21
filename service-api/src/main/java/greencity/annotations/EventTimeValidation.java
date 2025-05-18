package greencity.annotations;

import greencity.validator.EventTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventTimeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventTimeValidation {
    String message() default "Event Time is not valid or with past date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
