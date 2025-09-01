import {Component, inject} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {map, shareReplay} from 'rxjs/operators';
import {RouterOutlet} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {NavigationComponent} from './navigation/navigation.component';
import {ToolbarComponent} from './toolbar/toolbar.component';
import {HasRolesDirective} from 'keycloak-angular';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    RouterOutlet,
    NavigationComponent,
    ToolbarComponent,
    HasRolesDirective,

  ]
})
export class MainComponent {
  private breakpointObserver = inject(BreakpointObserver);

  isHandset = toSignal(this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    ), {initialValue: false});
}
