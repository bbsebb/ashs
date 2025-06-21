import {Component, inject} from '@angular/core';
import {
  MAT_SNACK_BAR_DATA,
  MatSnackBarAction,
  MatSnackBarActions,
  MatSnackBarLabel,
  MatSnackBarRef
} from '@angular/material/snack-bar';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-success',
  imports: [
    MatSnackBarLabel,
    MatSnackBarActions,
    MatSnackBarAction,
    MatIcon,
    MatButton
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
  background-color: var(--mat-sys-tertiary-container);
  color: var(--mat-sys-on-tertiary-container);
}
`
})
export class SuccessComponent {
  data: string = inject(MAT_SNACK_BAR_DATA);
  snackBarRef = inject(MatSnackBarRef);
}
