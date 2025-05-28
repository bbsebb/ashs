import {Gender} from "./gender";
import {Category} from './category';
import {HalResource} from './hal/hal';


export interface Team extends HalResource{
  id?: number;
  gender: Gender;
  category: Category;
  teamNumber: number;
}

