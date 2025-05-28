import {HalResource} from './hal/hal';

export interface Coach extends HalResource {
  id?: number;
  name: string;
  surname: string;
  email: string;
  phone: string;
}

export const coachMinimum: Coach = {
  name: '',
  surname: '',
  email: '',
  phone: '',
  _links: {
    self: {
      href: ''
    }
  }
};
