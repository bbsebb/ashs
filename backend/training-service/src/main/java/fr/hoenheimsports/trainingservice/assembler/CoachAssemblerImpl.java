package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.CoachControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.CoachMapper;
import fr.hoenheimsports.trainingservice.model.Coach;
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
 * Implementation of CoachAssembler that converts Coach entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for Coach resources.
 */
@Component
@Slf4j
public class CoachAssemblerImpl extends AbstractAssembler<Coach, EntityModel<CoachDTOResponse>> implements CoachAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final CoachMapper coachMapper;
    private final UserSecurityService userSecurityService;

    public CoachAssemblerImpl(PagedResourcesAssembler<Coach> pagedResourcesAssembler, CoachMapper coachMapper, UserSecurityService userSecurityService) {
        super(pagedResourcesAssembler);
        this.coachMapper = coachMapper;
        this.userSecurityService = userSecurityService;
    }

    @NonNull
    @Override
    public EntityModel<CoachDTOResponse> toModel(@NonNull Coach coach) {
        log.debug("Conversion d'une entité Coach (ID: {}) en modèle", coach.getId());
        CoachDTOResponse coachDTOResponse = coachMapper.toDto(coach);
        var entityModel = EntityModel.of(coachDTOResponse);

        log.debug("Ajout des liens au modèle Coach");
        entityModel.add(
                linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).withRel("coaches"),
                linkTo(methodOn(CoachControllerImpl.class).getCoachById(coachDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(coachDTOResponse))
        );

        return entityModel;
    }

    private List<Affordance> createAffordance(CoachDTOResponse coachDTOResponse) {
        log.debug("Création des affordances pour le coach avec l'ID: {}", coachDTOResponse.id());
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de suppression et mise à jour");
            list.add(afford(methodOn(CoachControllerImpl.class).deleteCoach(coachDTOResponse.id())));
            list.add(afford(methodOn(CoachControllerImpl.class).deleteCoach(coachDTOResponse.id())));
            list.add(afford(methodOn(CoachControllerImpl.class).updateCoach(coachDTOResponse.id(), null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }

    @NonNull
    @Override
    public PagedModel<EntityModel<CoachDTOResponse>> toPagedModel(@NonNull Page<Coach> pageCoaches) {
        log.debug("Conversion d'une page d'entités Coach en modèle paginé (page: {}, taille: {})",
                pageCoaches.getNumber(), pageCoaches.getSize());
        PagedModel<EntityModel<CoachDTOResponse>> pagedModel = super.toPagedModel(pageCoaches, CoachDTOResponse.class);

        log.debug("Ajout des affordances et liens au modèle paginé");
        // Add affordances and links to the paged model

        if (!pagedModel.hasLink("self")) {
            log.debug("Ajout du lien 'self' au modèle paginé");
            pagedModel.add(linkTo(methodOn(CoachControllerImpl.class).getCoaches(pageCoaches.getPageable())).withSelfRel());
        }
        pagedModel.mapLink(IanaLinkRelations.SELF, (link) -> link.andAffordances(createAffordance()));

        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(CoachControllerImpl.class).getAllCoaches()).withRel("allCoaches"));

        log.debug("Modèle paginé créé avec {} éléments", pagedModel.getContent().size());
        return pagedModel;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<CoachDTOResponse>> toCollectionModel(@NonNull Iterable<? extends Coach> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités Coach en modèle de collection");

        CollectionModel<EntityModel<CoachDTOResponse>> collectionModel = super.toCollectionModel(entities, CoachDTOResponse.class);
        log.debug("Ajout des liens à la collection de coachs");

        // Add links to the collection
        Link selfLink = linkTo(methodOn(CoachControllerImpl.class).getAllCoaches())
                .withSelfRel()
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(CoachControllerImpl.class).getCoaches(null)).toUri().toString()));
        log.debug("Liens ajoutés à la collection de coachs");

        return collectionModel;
    }

    private List<Affordance> createAffordance() {
        log.debug("Création des affordances pour les liens");
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de création");
            list.add(afford(methodOn(CoachControllerImpl.class).createCoach(null)));
            list.add(afford(methodOn(CoachControllerImpl.class).createCoach(null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }
}
