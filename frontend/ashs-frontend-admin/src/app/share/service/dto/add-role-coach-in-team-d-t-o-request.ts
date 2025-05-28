import {Role} from '../../model/role';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';

export interface AddRoleCoachInTeamDTORequest {
  coachId?: number;
  role: Role
}


export function toAddRoleCoachInTeamDTORequest(formRoleCoachDTO: FormRoleCoachDTO): AddRoleCoachInTeamDTORequest {
  return {
    coachId: formRoleCoachDTO.coach.id,
    role: formRoleCoachDTO.role
  }
}
