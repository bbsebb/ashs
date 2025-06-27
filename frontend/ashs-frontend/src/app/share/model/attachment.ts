import {Media} from './media';
import {SubAttachment} from './sub-attachment';

export interface Attachment {
  id: number;
  mediaType: string;
  type: string;
  media: Media;
  subAttachments: SubAttachment[];
}
