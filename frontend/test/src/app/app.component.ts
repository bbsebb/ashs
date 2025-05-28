import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MatCard} from '@angular/material/card';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatCard],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'test';
}
