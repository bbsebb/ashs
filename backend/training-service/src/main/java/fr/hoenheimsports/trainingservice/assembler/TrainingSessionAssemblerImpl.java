package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.TrainingSessionControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.UserSecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Implementation of TrainingSessionAssembler that converts TrainingSession entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for TrainingSession resources.
 */
@Component
public class TrainingSessionAssemblerImpl extends AbstractAssembler<TrainingSession, EntityModel<TrainingSessionDTOResponse>> implements TrainingSessionAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final TrainingSessionMapper trainingSessionMapper;
    private final UserSecurityService userSecurityService;
    private final HallAssembler hallAssembler;

    public TrainingSessionAssemblerImpl(PagedResourcesAssembler<TrainingSession> pagedResourcesAssembler, TrainingSessionMapper trainingSessionMapper, UserSecurityService userSecurityService, HallAssembler hallAssembler) {
        super(pagedResourcesAssembler);
        this.trainingSessionMapper = trainingSessionMapper;
        this.userSecurityService = userSecurityService;
        this.hallAssembler = hallAssembler;
    }


    @NonNull
    @Override
    public EntityModel<TrainingSessionDTOResponse> toModel(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null!");
        TrainingSessionDTOResponse trainingSessionDTOResponse = trainingSessionMapper.toDto(trainingSession);
        trainingSessionDTOResponse = trainingSessionDTOResponse.withAdditionalHallEntityModel(hallAssembler.toModel(trainingSession.getHall()));
        var entityModel = EntityModel.of(trainingSessionDTOResponse);

        entityModel.add(
                linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).withRel("trainingSessions"),
                linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessionById(trainingSessionDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(trainingSessionDTOResponse, trainingSession.getTeam().getId()))
        );

        return entityModel;
    }


    private List<Affordance> createAffordance(TrainingSessionDTOResponse trainingSessionDTOResponse, long teamId) {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).deleteTrainingSession(trainingSessionDTOResponse.id())));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).deleteTrainingSession(trainingSessionDTOResponse.id())));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).updateTrainingSession(trainingSessionDTOResponse.id(), null)));
        }
        return list;
    }


    @NonNull
    @Override
    public PagedModel<EntityModel<TrainingSessionDTOResponse>> toPagedModel(@NonNull Page<TrainingSession> pageTrainingSessions) {
        PagedModel<EntityModel<TrainingSessionDTOResponse>> pagedModel = super.toPagedModel(pageTrainingSessions, TrainingSessionDTOResponse.class);
        // Add affordances and links to the paged model
        Link selfLink = linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null))
                .withSelfRel()
                .andAffordances(createAffordance());
        pagedModel.add(selfLink);
        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(TrainingSessionControllerImpl.class).getAllTrainingSessions()).withRel("allTrainingSessions"));

        return pagedModel;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<TrainingSessionDTOResponse>> toCollectionModel(@NonNull Iterable<? extends TrainingSession> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        CollectionModel<EntityModel<TrainingSessionDTOResponse>> collectionModel = super.toCollectionModel(entities, TrainingSessionDTOResponse.class);

        // Add links to the collection
        Link selfLink = linkTo(methodOn(TrainingSessionControllerImpl.class).getAllTrainingSessions())
                .withSelfRel()
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).toUri().toString()));

        return collectionModel;
    }


    private List<Affordance> createAffordance() {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).createTrainingSession(null)));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).createTrainingSession(null)));
        }
        return list;
    }


}
