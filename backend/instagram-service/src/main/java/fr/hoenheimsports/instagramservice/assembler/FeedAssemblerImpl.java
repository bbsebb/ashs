package fr.hoenheimsports.instagramservice.assembler;

import fr.hoenheimsports.instagramservice.controller.FeedControllerImpl;
import fr.hoenheimsports.instagramservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.instagramservice.mapper.FeedEntityMapper;
import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class FeedAssemblerImpl extends AbstractAssembler<FeedEntity, EntityModel<FeedDTOResponse>> implements FeedAssembler {

    private final FeedEntityMapper feedEntityMapper;

    protected FeedAssemblerImpl(PagedResourcesAssembler<FeedEntity> pagedResourcesAssembler, FeedEntityMapper feedEntityMapper) {
        super(pagedResourcesAssembler);
        this.feedEntityMapper = feedEntityMapper;
    }

    @NonNull
    @Override
    public EntityModel<FeedDTOResponse> toModel(@NonNull FeedEntity entity) {
        Assert.notNull(entity, "Entity must not be null!");
        FeedDTOResponse feedDTOResponse = this.feedEntityMapper.toDto(entity);
        EntityModel<FeedDTOResponse> entityModel = EntityModel.of(feedDTOResponse);
        entityModel.add(
                linkTo(methodOn(FeedControllerImpl.class).getFeedById(entity.getGraphApiId())).withSelfRel(),
                linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).withRel("feeds")
        );
        return entityModel;
    }

    @NonNull
    @Override
    public CollectionModel<EntityModel<FeedDTOResponse>> toCollectionModel(@NonNull Iterable<? extends FeedEntity> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        CollectionModel<EntityModel<FeedDTOResponse>> collectionModel = super.toCollectionModel(entities);

        // Add links to the collection
        Link selfLink = linkTo(methodOn(FeedControllerImpl.class).getAllFeeds())
                .withSelfRel();

        collectionModel.add(selfLink);
        collectionModel.add(getTemplatedAndPagedLink(linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).toUri().toString()));

        return super.toCollectionModel(entities);
    }

    @NonNull
    @Override
    public PagedModel<EntityModel<FeedDTOResponse>> toPagedModel(@NonNull Page<FeedEntity> page) {
        Assert.notNull(page, "Page must not be null!");
        PagedModel<EntityModel<FeedDTOResponse>> pagedModel = super.toPagedModel(page, FeedDTOResponse.class);
        // Add affordances and links to the paged model
        if (!pagedModel.hasLink("self")) {
            pagedModel.add(linkTo(methodOn(FeedControllerImpl.class).getFeeds(page.getPageable())).withSelfRel());
        }
        pagedModel.mapLink(IanaLinkRelations.SELF, (link) -> link.andAffordances(createAffordance()));
        pagedModel.add(getTemplatedAndPagedLink(linkTo(methodOn(FeedControllerImpl.class).getFeeds(null)).toUri().toString()));
        pagedModel.add(linkTo(methodOn(FeedControllerImpl.class).getAllFeeds()).withRel("allCoaches"));
        return pagedModel;
    }

    private List<Affordance> createAffordance() {
        return List.of();
    }
}
