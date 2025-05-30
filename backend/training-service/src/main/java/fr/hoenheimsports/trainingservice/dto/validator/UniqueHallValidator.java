package fr.hoenheimsports.trainingservice.dto.validator;

import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.validator.annotation.UniqueHall;
import fr.hoenheimsports.trainingservice.service.HallService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueHallValidator implements ConstraintValidator<UniqueHall, HallDTOCreateRequest> {
    private final HallService hallService;

    public UniqueHallValidator(HallService hallService) {
        this.hallService = hallService;
    }


    @Override
    public boolean isValid(HallDTOCreateRequest hallDTOCreateRequest, ConstraintValidatorContext context) {
        return !hallService.isNotUniqueHall(
                hallDTOCreateRequest.name(),
                hallDTOCreateRequest.address().street(),
                hallDTOCreateRequest.address().city(),
                hallDTOCreateRequest.address().postalCode(),
                hallDTOCreateRequest.address().country()
        );
    }
}
