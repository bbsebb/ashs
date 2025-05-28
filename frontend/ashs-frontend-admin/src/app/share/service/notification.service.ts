import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from '@angular/material/snack-bar';
import {SuccessComponent} from '@app/share/component/snack-bar/success/success.component';
import {ErrorComponent} from '@app/share/component/snack-bar/error/error.component';
import {InfoComponent} from '@app/share/component/snack-bar/info/info.component';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {
  }

  private defaultConfig: MatSnackBarConfig = {
    duration: undefined,
    horizontalPosition: 'center',
    verticalPosition: 'bottom',
    panelClass: [],
    data: undefined,
  };

  showSuccess(message: string | undefined = undefined): void {
    message = message || "L'opération a réussi"
    this.snackBar.openFromComponent(SuccessComponent, {
      ...this.defaultConfig,
      panelClass: ['snackbar-success'],
      data: message
    });
  }

  showError(message: string | undefined = undefined): void {
    message = message || 'Une erreur est survenue';
    this.snackBar.openFromComponent(ErrorComponent, {
      ...this.defaultConfig,
      panelClass: ['snackbar-success'],
      data: message
    });
  }

  showInfo(message: string): void {
    this.snackBar.openFromComponent(InfoComponent, {
      ...this.defaultConfig,
      panelClass: ['snackbar-success'],
      data: message
    });
  }

  show(message: string, action: string = '', config?: MatSnackBarConfig): void {
    this.snackBar.open(message, action, {...this.defaultConfig, ...config});
  }
}
