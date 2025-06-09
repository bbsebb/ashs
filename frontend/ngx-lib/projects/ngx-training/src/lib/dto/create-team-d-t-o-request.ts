import {Gender} from '../model/gender';
import {Category} from '../model/category';


export interface CreateTeamDTORequest {
  gender: Gender;
  category: Category;
  teamNumber: number;
}


