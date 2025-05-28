package fr.hoenheimsports.trainingservice.dto.validator;

import fr.hoenheimsports.trainingservice.dto.request.TimeSlotDTORequest;
import fr.hoenheimsports.trainingservice.dto.validator.annotation.StartTimeBeforeEndTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartTimeBeforeEndTimeValidator implements ConstraintValidator<StartTimeBeforeEndTime, TimeSlotDTORequest> {
    @Override
    public boolean isValid(TimeSlotDTORequest value, ConstraintValidatorContext context) {
        return value.startTime().isBefore(value.endTime());
    }
}
