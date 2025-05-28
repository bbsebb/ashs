import {Pipe, PipeTransform} from '@angular/core';
import {Category} from '../model/category';


@Pipe({
  name: 'category',
  standalone: true
})
export class CategoryPipe implements PipeTransform {
  private categoryTranslations: { [key in Category]: string } = {
    [Category.SENIOR]: 'Sénior',
    [Category.U11]: '-11 ans',
    [Category.U13]: '-13 ans',
    [Category.U15]: '-15 ans',
    [Category.U18]: '-18 ans',
  };

  transform(value: Category): string {
    return this.categoryTranslations[value] || value;
  }

}
