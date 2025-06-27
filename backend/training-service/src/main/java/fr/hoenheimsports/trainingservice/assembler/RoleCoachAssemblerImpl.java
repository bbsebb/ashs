package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.RoleCoachControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.RoleCoachMapper;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
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
 * Implementation of RoleCoachAssembler that converts RoleCoach entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for RoleCoach resources.
 */
@Component
public class RoleCoachAssemblerImpl extends AbstractAssembler<RoleCoach, EntityModel<RoleCoachDTOResponse>> implements RoleCoachAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final RoleCoachMapper roleCoachMapper;
    private final PagedResourcesAssembler<RoleCoach> pagedResourcesAssembler;
    private final UserSecurityService userSecurityService;
    private final TeamAssembler teamAssembler;
    private final CoachAssembler coachAssembler;

    public RoleCoachAssemblerImpl(RoleCoachMapper roleCoachMapper, PagedResourcesAssembler<RoleCoach> pagedResourcesAssembler, UserSecurityService userSecurityService, TeamAssembler teamAssembler, CoachAssembler coachAssembler) {
        super(pagedResourcesAssembler);
        this.roleCoachMapper = roleCoachMapper;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.userSecurityService = userSecurityService;
        this.teamAssembler = teamAssembler;
        this.coachAssembler = coachAssembler;
    }

    @Override
    public PagedModel<EntityModel<RoleCoachDTOResponse>> toPagedModel(@NonNull Page<RoleCoach> page) {
        PagedModel<EntityModel<RoleCoachDTOResponse>> pagedModel = super.toPagedModel(page, RoleCoachDTOResponse.class);
        // Add affordances and links to the paged model

        pagedModel.mapLink(IanaLinkRelations.SELF, (link -> link.andAffordances(this.createAffordance())));

        return pagedModel;
    }

    private List<Affordance> createAffordance() {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            // No create method available in RoleCoachControllerImpl
        }
        return list;
    }

    @Override
    @NonNull
    public EntityModel<RoleCoachDTOResponse> toModel(@NonNull RoleCoach entity) {
        var roleCoachDTOResponse = roleCoachMapper.toDto(entity);
        var teamEntityModel = teamAssembler.toModel(entity.getTeam());
        var coachEntityModel = coachAssembler.toModel(entity.getCoach());
        roleCoachDTOResponse = new RoleCoachDTOResponse(roleCoachDTOResponse.id(), roleCoachDTOResponse.role(), coachEntityModel, teamEntityModel);
        Link selfLink = linkTo(methodOn(RoleCoachControllerImpl.class).getRoleCoachById(roleCoachDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(roleCoachDTOResponse));
        var entityModel = EntityModel.of(roleCoachDTOResponse);
        entityModel.add(selfLink);
        return entityModel;
    }

    private List<Affordance> createAffordance(RoleCoachDTOResponse roleCoachDTOResponse) {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            list.add(afford(methodOn(RoleCoachControllerImpl.class).deleteRoleCoach(roleCoachDTOResponse.id())));
            list.add(afford(methodOn(RoleCoachControllerImpl.class).deleteRoleCoach(roleCoachDTOResponse.id())));
        }
        return list;
    }


    @Override
    @NonNull
    public CollectionModel<EntityModel<RoleCoachDTOResponse>> toCollectionModel(@NonNull Iterable<? extends RoleCoach> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        CollectionModel<EntityModel<RoleCoachDTOResponse>> collectionModel = super.toCollectionModel(entities);

        // Add links to the collection
        Link selfLink = linkTo(methodOn(RoleCoachControllerImpl.class).getRoleCoachById(null))
                .withRel("roleCoaches")
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);

        return collectionModel;
    }
}
