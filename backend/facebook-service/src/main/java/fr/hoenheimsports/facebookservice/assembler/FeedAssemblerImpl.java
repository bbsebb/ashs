package fr.hoenheimsports.facebookservice.assembler;

import fr.hoenheimsports.facebookservice.controller.AccessTokenControllerImpl;
import fr.hoenheimsports.facebookservice.controller.FeedControllerImpl;
import fr.hoenheimsports.facebookservice.controller.dto.AccessTokenDTORequest;
import fr.hoenheimsports.facebookservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.facebookservice.mapper.FeedEntityMapper;
import fr.hoenheimsports.facebookservice.model.FeedEntity;
import fr.hoenheimsports.facebookservice.service.UserSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@Slf4j
public class FeedAssemblerImpl extends AbstractAssembler<FeedEntity, EntityModel<FeedDTOResponse>> implements FeedAssembler {

    private final FeedEntityMapper feedEntityMapper;
    private final UserSecurityService userSecurityService;

    protected FeedAssemblerImpl(PagedResourcesAssembler<FeedEntity> pagedResourcesAssembler, FeedEntityMapper feedEntityMapper, UserSecurityService userSecurityService) {
        super(pagedResourcesAssembler);
        this.feedEntityMapper = feedEntityMapper;
        this.userSecurityService = userSecurityService;
    }

    @NonNull
    @Override
    public EntityModel<FeedDTOResponse> toModel(@NonNull FeedEntity entity) {
        Assert.notNull(entity, "Entity must not be null!");
        log.debug("Conversion d'une entité Feed (graphApiId: {}) en modèle", entity.getGraphApiId());
        FeedDTOResponse feedDTOResponse = this.feedEntityMapper.toDto(entity);
        EntityModel<FeedDTOResponse> entityModel = EntityModel.of(feedDTOResponse);
        log.debug("Ajout des liens au modèle Feed");
        entityModel.add(
                linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).withRel("feeds")
        );
        return entityModel;
    }

    @NonNull
    @Override
    public CollectionModel<EntityModel<FeedDTOResponse>> toCollectionModel(@NonNull Iterable<? extends FeedEntity> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités Feed en modèle de collection");

        CollectionModel<EntityModel<FeedDTOResponse>> collectionModel = super.toCollectionModel(entities);
        log.debug("Ajout des liens à la collection de feeds");

        // Add links to the collection
        Link selfLink = linkTo(methodOn(FeedControllerImpl.class).getAllFeeds())
                .withSelfRel().andAffordances(createAffordance());

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).toUri().toString()));
        log.debug("Liens ajoutés à la collection de feeds");

        return super.toCollectionModel(entities);
    }

    @NonNull
    @Override
    public PagedModel<EntityModel<FeedDTOResponse>> toPagedModel(@NonNull Page<FeedEntity> page) {
        Assert.notNull(page, "Page must not be null!");
        log.debug("Conversion d'une page d'entités Feed en modèle paginé (page: {}, taille: {})", 
                page.getNumber(), page.getSize());
        PagedModel<EntityModel<FeedDTOResponse>> pagedModel = super.toPagedModel(page, FeedDTOResponse.class);

        log.debug("Ajout des affordances et liens au modèle paginé");
        // Add affordances and links to the paged model
        if (!pagedModel.hasLink("self")) {
            log.debug("Ajout du lien 'self' au modèle paginé");
            pagedModel.add(linkTo(methodOn(FeedControllerImpl.class).getFeeds(page.getPageable())).withSelfRel());
        }
        pagedModel.mapLink(IanaLinkRelations.SELF, (link) -> link.andAffordances(createAffordance()));
        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(FeedControllerImpl.class).getAllFeeds()).withRel("allCoaches"));
        log.debug("Modèle paginé créé avec {} éléments", pagedModel.getContent().size());
        return pagedModel;
    }

    private List<Affordance> createAffordance() {
        log.debug("Création des affordances pour les liens");
        if (!userSecurityService.hasRole("ADMIN_ROLE")) {
            log.debug("Utilisateur sans rôle ADMIN_ROLE, aucune affordance créée");
            return List.of();
        }
        log.debug("Utilisateur avec rôle ADMIN_ROLE, création de l'affordance d'échange de token");
        return List.of(
                afford(methodOn(AccessTokenControllerImpl.class).exchangeToken(new AccessTokenDTORequest("accessToken")))
        );
    }
}
