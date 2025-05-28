import {Gender} from '../../model/gender';
import {Category} from '../../model/category';


export interface UpdateTeamDTORequest {
  gender: Gender;
  category: Category;
  teamNumber: number;
}
