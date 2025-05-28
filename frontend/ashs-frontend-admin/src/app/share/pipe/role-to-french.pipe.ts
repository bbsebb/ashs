import {Pipe, PipeTransform} from '@angular/core';
import {Role} from '@app/share/model/role';

@Pipe({
  name: 'roleToFrench'
})
export class RoleToFrenchPipe implements PipeTransform {

  transform(role: Role | string): string {
    const roleInFrench = {
      [Role.MAIN.toString()]: 'principal',
      [Role.ASSISTANT.toString()]: 'adjoint',
      [Role.SUPPORT_STAFF.toString()]: 'accompagnateur',
    };

    return roleInFrench[role] || '';  // Valeur par défaut si non trouvée
  }

}
