package fr.hoenheimsports.trainingservice.dto.validator;

import fr.hoenheimsports.trainingservice.dto.request.TeamDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.validator.annotation.UniqueTeam;
import fr.hoenheimsports.trainingservice.service.TeamService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UniqueTeamValidator implements ConstraintValidator<UniqueTeam, TeamDTOCreateRequest> {
    private final TeamService teamService;

    public UniqueTeamValidator(TeamService teamService) {
        this.teamService = teamService;
    }


    @Override
    public boolean isValid(TeamDTOCreateRequest teamDTORequest, ConstraintValidatorContext context) {
        return !teamService.isNotUniqueTeam(teamDTORequest.gender(), teamDTORequest.category(), teamDTORequest.teamNumber());
    }
}
