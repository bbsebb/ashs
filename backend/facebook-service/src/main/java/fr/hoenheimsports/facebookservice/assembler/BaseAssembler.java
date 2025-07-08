package fr.hoenheimsports.facebookservice.assembler;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;


public interface
BaseAssembler<T, D extends RepresentationModel<?>> extends RepresentationModelAssembler<T, D> {
    /**
     * Converts a Spring Data Page containing entities of type T into a HATEOAS-compliant PagedModel containing DTOs of type D.
     *
     * @param page the paginated data containing entities of type T
     * @return a HATEOAS-compliant PagedModel containing DTOs of type D
     */
    PagedModel<D> toPagedModel(Page<T> page);
}