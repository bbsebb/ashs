package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.TrainingSessionControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.TrainingSessionMapper;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import fr.hoenheimsports.trainingservice.service.UserSecurityService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.debug("Conversion d'une entité TrainingSession (ID: {}) en modèle", trainingSession.getId());

        TrainingSessionDTOResponse trainingSessionDTOResponse = trainingSessionMapper.toDto(trainingSession);
        log.debug("Ajout du modèle Hall à la réponse");
        trainingSessionDTOResponse = trainingSessionDTOResponse.withAdditionalHallEntityModel(hallAssembler.toModel(trainingSession.getHall()));

        var entityModel = EntityModel.of(trainingSessionDTOResponse);

        log.debug("Ajout des liens au modèle TrainingSession");
        entityModel.add(
                linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).withRel("trainingSessions"),
                linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessionById(trainingSessionDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(trainingSessionDTOResponse, trainingSession.getTeam().getId()))
        );

        return entityModel;
    }


    private List<Affordance> createAffordance(TrainingSessionDTOResponse trainingSessionDTOResponse, long teamId) {
        log.debug("Création des affordances pour la séance d'entraînement avec l'ID: {}, équipe ID: {}", 
                trainingSessionDTOResponse.id(), teamId);
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de suppression et mise à jour");
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).deleteTrainingSession(trainingSessionDTOResponse.id())));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).deleteTrainingSession(trainingSessionDTOResponse.id())));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).updateTrainingSession(trainingSessionDTOResponse.id(), null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }


    @NonNull
    @Override
    public PagedModel<EntityModel<TrainingSessionDTOResponse>> toPagedModel(@NonNull Page<TrainingSession> pageTrainingSessions) {
        log.debug("Conversion d'une page d'entités TrainingSession en modèle paginé (page: {}, taille: {})", 
                pageTrainingSessions.getNumber(), pageTrainingSessions.getSize());

        PagedModel<EntityModel<TrainingSessionDTOResponse>> pagedModel = super.toPagedModel(pageTrainingSessions, TrainingSessionDTOResponse.class);

        log.debug("Ajout des affordances et liens au modèle paginé");
        // Add affordances and links to the paged model
        if (!pagedModel.hasLink("self")) {
            log.debug("Ajout du lien 'self' au modèle paginé");
            pagedModel.add(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(pageTrainingSessions.getPageable())).withSelfRel());
        }
        pagedModel.mapLink(IanaLinkRelations.SELF, (link) -> link.andAffordances(createAffordance()));

        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(TrainingSessionControllerImpl.class).getAllTrainingSessions()).withRel("allTrainingSessions"));

        log.debug("Modèle paginé créé avec {} éléments", pagedModel.getContent().size());
        return pagedModel;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<TrainingSessionDTOResponse>> toCollectionModel(@NonNull Iterable<? extends TrainingSession> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités TrainingSession en modèle de collection");

        CollectionModel<EntityModel<TrainingSessionDTOResponse>> collectionModel = super.toCollectionModel(entities, TrainingSessionDTOResponse.class);
        log.debug("Ajout des liens à la collection de séances d'entraînement");

        // Add links to the collection
        Link selfLink = linkTo(methodOn(TrainingSessionControllerImpl.class).getAllTrainingSessions())
                .withSelfRel()
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(TrainingSessionControllerImpl.class).getTrainingSessions(null)).toUri().toString()));
        log.debug("Liens ajoutés à la collection de séances d'entraînement");

        return collectionModel;
    }


    private List<Affordance> createAffordance() {
        log.debug("Création des affordances pour les liens");
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de création");
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).createTrainingSession(null)));
            list.add(afford(methodOn(TrainingSessionControllerImpl.class).createTrainingSession(null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }


}
