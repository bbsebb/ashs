import {HalResource} from './hal/hal';
import {Role} from './role';
import {Coach} from './coach';
import {Team} from './team';

export interface RoleCoach extends HalResource {
  id: number;
  role: Role;
  coach: Coach;
  team: Team;
}
