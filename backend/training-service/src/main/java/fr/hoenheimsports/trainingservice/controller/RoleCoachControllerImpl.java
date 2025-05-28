package fr.hoenheimsports.trainingservice.controller;

import fr.hoenheimsports.trainingservice.assembler.RoleCoachAssemblerImpl;
import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import fr.hoenheimsports.trainingservice.service.RoleCoachServiceImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role-coaches")
public class RoleCoachControllerImpl implements RoleCoachController {

    private final RoleCoachServiceImpl roleCoachService;
    private final RoleCoachAssemblerImpl roleCoachAssembler;

    public RoleCoachControllerImpl(RoleCoachServiceImpl roleCoachService, RoleCoachAssemblerImpl roleCoachAssembler) {
        this.roleCoachService = roleCoachService;
        this.roleCoachAssembler = roleCoachAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<RoleCoachDTOResponse>> getRoleCoachById(@PathVariable Long id) {
        RoleCoach roleCoach = roleCoachService.getRoleCoachById(id);
        return ResponseEntity.ok(this.roleCoachAssembler.toModel(roleCoach));
    }


    @Override
    public ResponseEntity<Void> deleteRoleCoach(@PathVariable long id) {
        roleCoachService.deleteRoleCoach(id);
        return ResponseEntity.noContent().build();
    }

}
