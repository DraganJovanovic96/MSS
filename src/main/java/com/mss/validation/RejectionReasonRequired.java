package com.mss.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure rejection reason is provided when status is REJECTED.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RejectionReasonRequiredValidator.class)
@Documented
public @interface RejectionReasonRequired {
    String message() default "Rejection reason is required when rejecting a vacation request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
