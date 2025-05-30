import {Address} from '@app/share/model/address';

export interface CreateHallDTORequest {
  name: string;
  address: Address;
}
