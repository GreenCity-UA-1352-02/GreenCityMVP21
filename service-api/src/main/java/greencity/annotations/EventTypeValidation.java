package greencity.annotations;

import greencity.validator.EventTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventTypeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventTypeValidation {
    String message() default "the event type was not selected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
