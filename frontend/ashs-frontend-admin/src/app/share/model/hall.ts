import {Address} from "./address";
import {HalResource} from '@app/share/model/hal/hal';

export interface Hall extends HalResource {
  id: number;
  name: string;
  address: Address;
}

export const emptyHall: Hall = {
  id: 0,
  name: '',
  address: {
    street: '',
    city: '',
    postalCode: '',
    country: ''
  },
  _links: {
    self: {href: ''}
  }
};
