import {Media} from './media';
import {Target} from './target';

export interface SubAttachment {
  id: number;
  type: string;
  url: string;
  media: Media;
  target: Target;
}
