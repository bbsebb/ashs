import {FormRoleCoachDTO} from './form-role-coach-d-t-o';
import {Role} from '../model/role';

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
