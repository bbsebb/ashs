import {Component} from '@angular/core';
import {MatCard, MatCardActions, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton} from '@angular/material/button';


@Component({
  selector: 'app-hall',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardActions,
    MatCardTitle
  ],
  templateUrl: './hall.component.html',
  standalone: true,
  styleUrl: './hall.component.css'
})
export class HallComponent {



}
