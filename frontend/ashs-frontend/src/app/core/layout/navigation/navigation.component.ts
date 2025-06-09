import {Component} from '@angular/core';
import {MatListItem, MatNavList} from "@angular/material/list";
import {MatToolbar} from "@angular/material/toolbar";

@Component({
  selector: 'app-navigation',
  imports: [
    MatListItem,
    MatNavList,
    MatToolbar
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent {

}
