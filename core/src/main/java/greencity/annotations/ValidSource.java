package greencity.annotations;

import static greencity.constant.ErrorMessage.INVALID_SOURCE;
import greencity.validator.SourceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SourceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidSource {
    String message() default INVALID_SOURCE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
