import {Attachment} from './attachment';
import {HalResource} from 'ngx-hal-forms';

export interface Feed extends HalResource {
  graphApiId: string;
  message: string;
  createdTime: string; // OffsetDateTime → string en ISO-8601
  attachments: Attachment[];
}
