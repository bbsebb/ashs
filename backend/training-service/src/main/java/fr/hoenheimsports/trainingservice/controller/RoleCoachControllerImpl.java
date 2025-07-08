package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.RoleCoachAssemblerImpl;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.service.RoleCoachServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the RoleCoachController interface for handling coach role operations.
 * 
 * <p>This controller provides REST API endpoints for retrieving and deleting coach role relationships,
 * which represent the association between coaches and teams with specific roles. It delegates
 * the actual business logic to the RoleCoachService and uses RoleCoachAssembler to convert
 * the domain entities to HATEOAS-enabled DTOs with appropriate links.</p>
 * 
 * <p>The controller logs all requests and responses for monitoring purposes.</p>
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/api/role-coaches")
@Slf4j
public class RoleCoachControllerImpl implements RoleCoachController {

    /**
     * The service used for coach role operations.
     */
    private final RoleCoachServiceImpl roleCoachService;

    /**
     * The assembler used to convert coach role entities to DTOs with HATEOAS links.
     */
    private final RoleCoachAssemblerImpl roleCoachAssembler;

    /**
     * Constructs a new RoleCoachControllerImpl with the specified dependencies.
     * 
     * @param roleCoachService The service to use for coach role operations
     * @param roleCoachAssembler The assembler to use for converting coach role entities to DTOs with HATEOAS links
     */
    public RoleCoachControllerImpl(RoleCoachServiceImpl roleCoachService, RoleCoachAssemblerImpl roleCoachAssembler) {
        this.roleCoachService = roleCoachService;
        this.roleCoachAssembler = roleCoachAssembler;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates to the RoleCoachService to retrieve the coach role entity,
     * then uses the RoleCoachAssembler to convert it to a HATEOAS-enabled DTO with appropriate links.
     * It logs the request and response details for monitoring purposes.</p>
     * 
     * @param id The unique identifier of the coach role relationship
     * @return A ResponseEntity containing the coach role with HATEOAS links
     */
    @Override
    public ResponseEntity<EntityModel<RoleCoachDTOResponse>> getRoleCoachById(@PathVariable Long id) {
        log.info("Réception d'une requête pour obtenir le rôle de coach avec l'ID: {}", id);
        RoleCoach roleCoach = roleCoachService.getRoleCoachById(id);
        log.info("Rôle de coach trouvé et renvoyé: ID={}, rôle={}", roleCoach.getId(), roleCoach.getRole());
        return ResponseEntity.ok(this.roleCoachAssembler.toModel(roleCoach));
    }


    /**
     * {@inheritDoc}
     * 
     * <p>This implementation delegates to the RoleCoachService to delete the coach role entity.
     * It logs the request and confirmation of deletion for monitoring purposes.
     * The method returns a 204 No Content response upon successful deletion.</p>
     * 
     * @param id The unique identifier of the coach role relationship to delete
     * @return A ResponseEntity with HTTP status 204 (No Content) if the deletion was successful
     */
    @Override
    public ResponseEntity<Void> deleteRoleCoach(@PathVariable long id) {
        log.info("Réception d'une requête de suppression du rôle de coach avec l'ID: {}", id);
        roleCoachService.deleteRoleCoach(id);
        log.info("Rôle de coach supprimé avec succès, ID: {}", id);
        return ResponseEntity.noContent().build();
    }

}
