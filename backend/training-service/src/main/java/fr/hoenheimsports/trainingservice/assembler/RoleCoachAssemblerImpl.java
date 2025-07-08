package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.RoleCoachControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.RoleCoachMapper;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
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
 * Implementation of RoleCoachAssembler that converts RoleCoach entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for RoleCoach resources.
 */
@Component
@Slf4j
public class RoleCoachAssemblerImpl extends AbstractAssembler<RoleCoach, EntityModel<RoleCoachDTOResponse>> implements RoleCoachAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final RoleCoachMapper roleCoachMapper;
    private final UserSecurityService userSecurityService;
    private final TeamAssembler teamAssembler;
    private final CoachAssembler coachAssembler;

    public RoleCoachAssemblerImpl(RoleCoachMapper roleCoachMapper, PagedResourcesAssembler<RoleCoach> pagedResourcesAssembler, UserSecurityService userSecurityService, TeamAssembler teamAssembler, CoachAssembler coachAssembler) {
        super(pagedResourcesAssembler);
        this.roleCoachMapper = roleCoachMapper;
        this.userSecurityService = userSecurityService;
        this.teamAssembler = teamAssembler;
        this.coachAssembler = coachAssembler;
    }

    @Override
    public PagedModel<EntityModel<RoleCoachDTOResponse>> toPagedModel(@NonNull Page<RoleCoach> page) {
        log.debug("Conversion d'une page d'entités RoleCoach en modèle paginé (page: {}, taille: {})",
                page.getNumber(), page.getSize());
        PagedModel<EntityModel<RoleCoachDTOResponse>> pagedModel = super.toPagedModel(page, RoleCoachDTOResponse.class);

        log.debug("Ajout des affordances et liens au modèle paginé");
        // Add affordances and links to the paged model

        pagedModel.mapLink(IanaLinkRelations.SELF, (link -> link.andAffordances(this.createAffordance())));

        log.debug("Modèle paginé créé avec {} éléments", pagedModel.getContent().size());
        return pagedModel;
    }

    private List<Affordance> createAffordance() {
        log.debug("Création des affordances pour les liens");
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, mais aucune affordance disponible (pas de méthode create)");
            // No create method available in RoleCoachControllerImpl
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }

    @Override
    @NonNull
    public EntityModel<RoleCoachDTOResponse> toModel(@NonNull RoleCoach entity) {
        log.debug("Conversion d'une entité RoleCoach (ID: {}) en modèle", entity.getId());
        var roleCoachDTOResponse = roleCoachMapper.toDto(entity);

        log.debug("Conversion des entités associées (Team et Coach) en modèles");
        var teamEntityModel = teamAssembler.toModel(entity.getTeam());
        var coachEntityModel = coachAssembler.toModel(entity.getCoach());

        roleCoachDTOResponse = new RoleCoachDTOResponse(roleCoachDTOResponse.id(), roleCoachDTOResponse.role(), coachEntityModel, teamEntityModel);

        log.debug("Ajout des liens au modèle RoleCoach");
        Link selfLink = linkTo(methodOn(RoleCoachControllerImpl.class).getRoleCoachById(roleCoachDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(roleCoachDTOResponse));
        var entityModel = EntityModel.of(roleCoachDTOResponse);
        entityModel.add(selfLink);

        return entityModel;
    }

    private List<Affordance> createAffordance(RoleCoachDTOResponse roleCoachDTOResponse) {
        log.debug("Création des affordances pour le rôle de coach avec l'ID: {}", roleCoachDTOResponse.id());
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de suppression");
            list.add(afford(methodOn(RoleCoachControllerImpl.class).deleteRoleCoach(roleCoachDTOResponse.id())));
            list.add(afford(methodOn(RoleCoachControllerImpl.class).deleteRoleCoach(roleCoachDTOResponse.id())));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }


    @Override
    @NonNull
    public CollectionModel<EntityModel<RoleCoachDTOResponse>> toCollectionModel(@NonNull Iterable<? extends RoleCoach> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités RoleCoach en modèle de collection");

        CollectionModel<EntityModel<RoleCoachDTOResponse>> collectionModel = super.toCollectionModel(entities, RoleCoachDTOResponse.class);
        log.debug("Ajout des liens à la collection de rôles de coach");

        // Add links to the collection
        Link selfLink = linkTo(methodOn(RoleCoachControllerImpl.class).getRoleCoachById(null))
                .withRel("roleCoaches")
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        log.debug("Liens ajoutés à la collection de rôles de coach");

        return collectionModel;
    }
}
