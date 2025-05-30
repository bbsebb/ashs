import {Address} from "./address";
import {HalResource} from '@app/share/model/hal/hal';

export interface Hall extends HalResource {
  id: number;
  name: string;
  address: Address;
}

