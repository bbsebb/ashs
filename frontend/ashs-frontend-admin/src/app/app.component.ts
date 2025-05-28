import { Component } from '@angular/core';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MainComponent} from './core/main/main.component';


@Component({
  selector: 'app-root',
  imports: [
    MainComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'ashs-frontend-admin';
}
