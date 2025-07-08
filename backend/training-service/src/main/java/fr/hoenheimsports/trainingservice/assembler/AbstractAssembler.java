package fr.hoenheimsports.trainingservice.assembler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
public abstract class AbstractAssembler<T, D extends RepresentationModel<?>> implements RepresentationModelAssembler<T, D> {
    protected final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected AbstractAssembler(PagedResourcesAssembler<T> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        log.debug("Initialisation de AbstractAssembler avec PagedResourcesAssembler");
    }

    /**
     * Converts a Spring Data Page containing entities of type T into a HATEOAS-compliant PagedModel containing DTOs of type D.
     *
     * @param page the paginated data containing entities of type T
     * @return a HATEOAS-compliant PagedModel containing DTOs of type D
     */
    public <R> PagedModel<D> toPagedModel(Page<T> page, Class<R> dtoClass) {
        Assert.notNull(page, "Page must not be null!");
        log.debug("Conversion d'une page en PagedModel pour la classe {}", dtoClass.getSimpleName());
        log.debug("Informations de la page: numéro={}, taille={}, éléments totaux={}", 
                page.getNumber(), page.getSize(), page.getTotalElements());

        PagedModel<D> pagedModel;

        // Handle empty pages by creating an empty wrapper
        if (page.isEmpty()) {
            log.debug("La page est vide, création d'un wrapper vide pour {}", dtoClass.getSimpleName());
            EmbeddedWrapper emptyWrapper = new EmbeddedWrappers(false)
                    .emptyCollectionOf(dtoClass);

            // In HAL, the "_embedded.<resource-name>" relation will be the lowercase class name + 'List' by default
            // Force type casting as we know there will never be direct access to the wrapper itself
            @SuppressWarnings("unchecked")
            PagedModel<D> emptyPagedModel = (PagedModel<D>) (PagedModel<?>) PagedModel.of(
                    Collections.singletonList(emptyWrapper),
                    new PagedModel.PageMetadata(
                            page.getSize(),
                            page.getNumber(),
                            page.getTotalElements(),
                            page.getTotalPages()
                    )
            );
            pagedModel = emptyPagedModel;
            log.debug("PagedModel vide créé avec succès");
        } else {
            log.debug("Conversion de {} éléments avec pagedResourcesAssembler", page.getNumberOfElements());
            pagedModel = pagedResourcesAssembler.toModel(page, this);
            log.debug("PagedModel créé avec succès contenant {} éléments", page.getNumberOfElements());
        }
        return pagedModel;
    }

    /**
     * Creates a templated link for pagination.
     *
     * @param uri The base URI to create the templated link from
     * @return A Link with pagination template variables
     */
    public Link getTemplatedAndPagedLink(String uri) {
        log.debug("Création d'un lien paginé templated pour l'URI: {}", uri);
        UriTemplate uriTemplate = UriTemplate.of(uri)
                .with("page", TemplateVariable.VariableType.REQUEST_PARAM)
                .with("size", TemplateVariable.VariableType.REQUEST_PARAM)
                .with("sort", TemplateVariable.VariableType.REQUEST_PARAM);
        log.debug("Lien paginé templated créé avec succès: {}", uriTemplate);
        return Link.of(uriTemplate, "page");
    }

    /**
     * Converts a collection of entities to a CollectionModel with appropriate links.
     *
     * @param entities The collection of entities to convert
     * @return CollectionModel containing converted entities
     */

    public <R> CollectionModel<D> toCollectionModel(Iterable<? extends T> entities, Class<R> dtoClass) {
        Assert.notNull(entities, "Entities must not be null!");
        log.debug("Conversion d'une collection d'entités en CollectionModel pour la classe {}", dtoClass.getSimpleName());

        List<D> models = StreamSupport.stream(entities.spliterator(), false) //
                .map(this::toModel) //
                .toList();
        log.debug("Conversion de {} entités en modèles", models.size());

        CollectionModel<D> collectionModel;

        // Handle empty collections by creating an empty wrapper
        if (models.isEmpty()) {
            log.debug("La collection est vide, création d'un wrapper vide pour {}", dtoClass.getSimpleName());
            EmbeddedWrapper emptyWrapper = new EmbeddedWrappers(false)
                    .emptyCollectionOf(dtoClass);

            // Force type casting as we know there will never be direct access to the wrapper itself
            @SuppressWarnings("unchecked")
            CollectionModel<D> emptyModel =
                    (CollectionModel<D>) (CollectionModel<?>) CollectionModel.of(Collections.singletonList(emptyWrapper));
            collectionModel = emptyModel;
            log.debug("CollectionModel vide créé avec succès");
        } else {
            log.debug("Création d'un CollectionModel avec {} éléments", models.size());
            collectionModel = CollectionModel.of(models);
            log.debug("CollectionModel créé avec succès");
        }
        return collectionModel;
    }
}
