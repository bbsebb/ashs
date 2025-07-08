package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.controller.HallControllerImpl;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Hall;
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
 * Implementation of HallAssembler that converts Hall entities to HATEOAS-compliant representations.
 * This class handles the creation of links and affordances for Hall resources.
 */
@Component
@Slf4j
public class HallAssemblerImpl extends AbstractAssembler<Hall, EntityModel<HallDTOResponse>> implements HallAssembler {
    public static final String ADMIN_ROLE = "ADMIN";
    private final HallMapper hallMapper;
    private final UserSecurityService userSecurityService;

    public HallAssemblerImpl(PagedResourcesAssembler<Hall> pagedResourcesAssembler, HallMapper hallMapper, UserSecurityService userSecurityService) {
        super(pagedResourcesAssembler);
        this.hallMapper = hallMapper;
        this.userSecurityService = userSecurityService;
    }

    @NonNull
    @Override
    public EntityModel<HallDTOResponse> toModel(@NonNull Hall hall) {
        log.debug("Conversion d'une entité Hall (ID: {}) en modèle", hall.getId());
        HallDTOResponse hallDTOResponse = hallMapper.toDto(hall);
        var entityModel = EntityModel.of(hallDTOResponse);

        log.debug("Ajout des liens au modèle Hall");
        entityModel.add(
                linkTo(methodOn(HallControllerImpl.class).getHalls(null)).withRel("halls"),
                linkTo(methodOn(HallControllerImpl.class).getHallById(hallDTOResponse.id())).withSelfRel().andAffordances(this.createAffordance(hallDTOResponse))
        );
        return entityModel;
    }

    private List<Affordance> createAffordance(HallDTOResponse hallDTOResponse) {
        log.debug("Création des affordances pour la salle avec l'ID: {}", hallDTOResponse.id());
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de suppression et mise à jour");
            list.add(afford(methodOn(HallControllerImpl.class).deleteHall(hallDTOResponse.id())));
            list.add(afford(methodOn(HallControllerImpl.class).deleteHall(hallDTOResponse.id())));
            list.add(afford(methodOn(HallControllerImpl.class).updateHall(hallDTOResponse.id(), null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }

    @NonNull
    @Override
    public PagedModel<EntityModel<HallDTOResponse>> toPagedModel(@NonNull Page<Hall> pageHalls) {
        log.debug("Conversion d'une page d'entités Hall en modèle paginé (page: {}, taille: {})",
                pageHalls.getNumber(), pageHalls.getSize());
        PagedModel<EntityModel<HallDTOResponse>> pagedModel = super.toPagedModel(pageHalls, HallDTOResponse.class);

        log.debug("Ajout des affordances et liens au modèle paginé");
        // Add affordances and links to the paged model
        if (!pagedModel.hasLink("self")) {
            log.debug("Ajout du lien 'self' au modèle paginé");
            pagedModel.add(linkTo(methodOn(HallControllerImpl.class).getHalls(pageHalls.getPageable())).withSelfRel());
        }
        pagedModel.mapLink(IanaLinkRelations.SELF, (link) -> link.andAffordances(createAffordance()));

        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(HallControllerImpl.class).getAllHalls()).withRel("allHalls"));

        log.debug("Modèle paginé créé avec {} éléments", pagedModel.getContent().size());
        return pagedModel;
    }


    @NonNull
    @Override
    public CollectionModel<EntityModel<HallDTOResponse>> toCollectionModel(@NonNull Iterable<? extends Hall> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités Hall en modèle de collection");

        CollectionModel<EntityModel<HallDTOResponse>> collectionModel = super.toCollectionModel(entities, HallDTOResponse.class);
        log.debug("Ajout des liens à la collection de salles");

        // Add links to the collection
        Link selfLink = linkTo(methodOn(HallControllerImpl.class).getAllHalls())
                .withSelfRel()
                .andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(HallControllerImpl.class).getHalls(null)).toUri().toString()));
        log.debug("Liens ajoutés à la collection de salles");

        return collectionModel;
    }

    private List<Affordance> createAffordance() {
        log.debug("Création des affordances pour les liens");
        var list = new ArrayList<Affordance>();
        if (userSecurityService.hasRole(ADMIN_ROLE)) {
            log.debug("Utilisateur avec rôle ADMIN, ajout des affordances de création");
            list.add(afford(methodOn(HallControllerImpl.class).createHall(null)));
            list.add(afford(methodOn(HallControllerImpl.class).createHall(null)));
        } else {
            log.debug("Utilisateur sans rôle ADMIN, aucune affordance ajoutée");
        }
        return list;
    }
}
