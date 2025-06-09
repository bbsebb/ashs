import {RoleCoach} from '../model/role-coach';

export interface DeleteRoleCoachDTORequest {
  id: number;
}

export function toDeleteRoleCoachDTORequest(roleCoach: RoleCoach): DeleteRoleCoachDTORequest {
  return {
    id: roleCoach.id
  }
}
