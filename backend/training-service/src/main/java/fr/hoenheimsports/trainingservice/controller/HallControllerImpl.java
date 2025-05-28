package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.HallAssembler;
import fr.hoenheimsports.trainingservice.dto.request.HallDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.mapper.HallMapper;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.service.HallService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>REST controller for managing Hall-related operations.</p>
 *
 * <p>This controller provides:</p>
 * <ul>
 *   <li>CRUD operations (Create, Retrieve, Update, Delete) for Hall resources</li>
 *   <li>Paginated and non-paginated retrieval of resources</li>
 *   <li>HATEOAS-compliant representations (EntityModel, CollectionModel, PagedModel)</li>
 * </ul>
 */

@RestController
@RequestMapping("/api/halls")
public class HallControllerImpl implements HallController {
    private final HallService hallService;
    private final HallAssembler hallAssembler;
    private final HallMapper hallMapper;

    public HallControllerImpl(HallService hallService, HallAssembler hallAssembler, HallMapper hallMapper) {
        this.hallService = hallService;
        this.hallAssembler = hallAssembler;
        this.hallMapper = hallMapper;
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> createHall(@Valid @RequestBody HallDTORequest hallDTO) {
        Hall hall = hallService.createHall(hallMapper.toEntity(hallDTO));
        return new ResponseEntity<>(this.hallAssembler.toModel(hall), HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> getHallById(@PathVariable Long id) {
        Hall hall = hallService.getHallById(id);
        return ResponseEntity.ok(this.hallAssembler.toModel(hall));
    }


    @Override
    public ResponseEntity<PagedModel<EntityModel<HallDTOResponse>>> getHalls(@ParameterObject Pageable pageable) {
        Page<Hall> pagedHalls = hallService.getHalls(pageable);
        return ResponseEntity.ok(this.hallAssembler.toPagedModel(pagedHalls));
    }


    @Override
    public ResponseEntity<EntityModel<HallDTOResponse>> updateHall(@PathVariable Long id, @Valid @RequestBody HallDTORequest updatedHallDTO) {
        Hall persistedHall = hallService.updateHall(id, hallMapper.toEntity(updatedHallDTO));
        return ResponseEntity.ok(this.hallAssembler.toModel(persistedHall));
    }


    @Override
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<CollectionModel<EntityModel<HallDTOResponse>>> getAllHalls() {
        List<Hall> halls = hallService.getAllHalls();
        return ResponseEntity.ok(this.hallAssembler.toCollectionModel(halls));
    }


}
