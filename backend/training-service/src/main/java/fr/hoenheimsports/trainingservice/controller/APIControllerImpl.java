package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.dto.ApiIndexModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>Implementation of the {@link APIController} interface.</p>
 *
 * <p>This controller provides a single entry point for the API with links to all available resources.</p>
 */
@RestController
@Tag(name = "API", description = "API entry point operations")
public class APIControllerImpl implements APIController {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ApiIndexModel> getApiIndex() {
        ApiIndexModel apiIndex = ApiIndexModel.createApiIndex();

        // Add links to all available resources
        apiIndex.add(
                linkTo(methodOn(TeamControllerImpl.class).getTeams(null)).withRel("teams"),
                linkTo(methodOn(TeamControllerImpl.class).getAllTeams()).withRel("allTeams"),
                linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).withRel("coaches"),
                linkTo(methodOn(CoachControllerImpl.class).getAllCoaches()).withRel("allCoaches"),
                linkTo(methodOn(HallControllerImpl.class).getHalls(null)).withRel("halls"),
                linkTo(methodOn(HallControllerImpl.class).getAllHalls()).withRel("allHalls"),
                linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).withRel("trainingSessions"),
                linkTo(methodOn(TrainingSessionControllerImpl.class).getAllTrainingSessions()).withRel("allTrainingSessions"),
                linkTo(methodOn(APIControllerImpl.class).getApiIndex()).withSelfRel()
        );

        return ResponseEntity.ok(apiIndex);
    }
}
