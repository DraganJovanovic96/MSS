package com.mss.validation;

import com.mss.dto.VacationRequestUpdateDto;
import com.mss.enumeration.VacationRequestStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for RejectionReasonRequired annotation.
 * Ensures that rejection reason is provided when status is REJECTED.
 */
public class RejectionReasonRequiredValidator implements ConstraintValidator<RejectionReasonRequired, VacationRequestUpdateDto> {

    @Override
    public boolean isValid(VacationRequestUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (dto.getStatus() == VacationRequestStatus.REJECTED) {
            return dto.getRejectionReason() != null && !dto.getRejectionReason().trim().isEmpty();
        }

        return true;
    }
}
