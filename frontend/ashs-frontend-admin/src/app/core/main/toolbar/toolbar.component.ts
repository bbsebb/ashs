import {Component, input, output} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-toolbar',
  imports: [
    MatToolbar,
    MatIcon,
    MatIconButton,
    RouterLink
  ],
  templateUrl: './toolbar.component.html',
  standalone: true,
  styleUrl: './toolbar.component.css'
})
export class ToolbarComponent {
  isHandset = input.required();
  opening = output<void>()
}
