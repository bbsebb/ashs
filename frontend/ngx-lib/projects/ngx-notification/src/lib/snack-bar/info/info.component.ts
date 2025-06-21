import {Component, inject} from '@angular/core';
import {
  MAT_SNACK_BAR_DATA,
  MatSnackBarAction,
  MatSnackBarActions,
  MatSnackBarLabel,
  MatSnackBarRef
} from "@angular/material/snack-bar";
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-info',
  imports: [
    MatButton,
    MatIcon,
    MatSnackBarAction,
    MatSnackBarActions,
    MatSnackBarLabel
  ],
  template: `
<span class="snackbar-success" matSnackBarLabel>
  {{ data }}
</span>
<span matSnackBarActions>
  <button mat-button matSnackBarAction (click)="snackBarRef.dismissWithAction()"><mat-icon>close</mat-icon></button>
</span>
`,
  styles: `
:host {
  display: flex;
}
`
})
export class InfoComponent {
  data: string = inject(MAT_SNACK_BAR_DATA);
  snackBarRef = inject(MatSnackBarRef);
}
