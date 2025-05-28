package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.HallControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Hall;
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
 * Implementation of HallAssembler that converts Hall entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for Hall resources.
 */
@Component
public class HallAssemblerImpl extends AbstractAssembler<Hall, EntityModel<HallDTOResponse>> implements HallAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final PagedResourcesAssembler<Hall> pagedResourcesAssembler;
    private final HallMapper hallMapper;
    private final UserSecurityService userSecurityService;

    public HallAssemblerImpl(PagedResourcesAssembler<Hall> pagedResourcesAssembler, HallMapper hallMapper, UserSecurityService userSecurityService) {
        super(pagedResourcesAssembler);
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.hallMapper = hallMapper;
        this.userSecurityService = userSecurityService;
    }

    @NonNull
    @Override
    public EntityModel<HallDTOResponse> toModel(@NonNull Hall hall) {
        HallDTOResponse hallDTOResponse = hallMapper.toDto(hall);
        var entityModel = EntityModel.of(hallDTOResponse);

        entityModel.add(
                linkTo(methodOn(HallControllerImpl.class).getHalls(null)).withRel("halls"),
                linkTo(methodOn(HallControllerImpl.class).getHallById(hallDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(hallDTOResponse))
        );
        return entityModel;
    }

    private List<Affordance> createAffordance(HallDTOResponse hallDTOResponse) {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            list.add(afford(methodOn(HallControllerImpl.class).deleteHall(hallDTOResponse.id())));
            list.add(afford(methodOn(HallControllerImpl.class).deleteHall(hallDTOResponse.id())));
            list.add(afford(methodOn(HallControllerImpl.class).updateHall(hallDTOResponse.id(), null)));
        }
        return list;
    }

    @NonNull
    @Override
    public PagedModel<EntityModel<HallDTOResponse>> toPagedModel(@NonNull Page<Hall> pageHalls) {
        PagedModel<EntityModel<HallDTOResponse>> pagedModel = super.toPagedModel(pageHalls, HallDTOResponse.class);
        // Add affordances and links to the paged model
        Link selfLink = linkTo(methodOn(HallControllerImpl.class).getHalls(null))
                .withSelfRel()
                .andAffordances(createAffordance());
        pagedModel.add(selfLink);
        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(HallControllerImpl.class).getAllHalls()).withRel("allHalls"));

        return pagedModel;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<HallDTOResponse>> toCollectionModel(@NonNull Iterable<? extends Hall> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        CollectionModel<EntityModel<HallDTOResponse>> collectionModel = super.toCollectionModel(entities);

        // Add links to the collection
        Link selfLink = linkTo(methodOn(HallControllerImpl.class).getAllHalls())
                .withSelfRel()
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).toUri().toString()));

        return collectionModel;
    }

    private List<Affordance> createAffordance() {
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            list.add(afford(methodOn(HallControllerImpl.class).createHall(null)));
            list.add(afford(methodOn(HallControllerImpl.class).createHall(null)));
        }
        return list;
    }
}
