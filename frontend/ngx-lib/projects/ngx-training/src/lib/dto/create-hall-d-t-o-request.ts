import {Address} from '../model/address';

export interface CreateHallDTORequest {
  name: string;
  address: Address;
}
