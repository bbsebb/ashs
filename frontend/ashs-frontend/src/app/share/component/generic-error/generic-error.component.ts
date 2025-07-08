import {Component} from '@angular/core';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-generic-error',
  imports: [
    MatCard,
    MatCardContent,
    MatIcon
  ],
  templateUrl: './generic-error.component.html',
  styleUrl: './generic-error.component.scss'
})
export class GenericErrorComponent {

}
