import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate',
  standalone: true
})
export class TruncatePipe implements PipeTransform {

  transform(value: string, limit: number = 100, completeWords: boolean = true): {text:string , isTruncated:boolean} {
    let truncated = value;
    let isTruncated = false;
    if (value.length > limit) {
      isTruncated = true;
      truncated = value.substring(0, limit);
      if (completeWords) {
        const lastIndex = truncated.lastIndexOf(' ');
        truncated = truncated.substring(0, lastIndex);
      }
    }
    return {text:truncated, isTruncated:isTruncated}
  }

}
