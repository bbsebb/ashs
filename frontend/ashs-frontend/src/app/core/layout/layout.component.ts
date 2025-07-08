import {Component, inject} from '@angular/core';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {NavigationComponent} from './navigation/navigation.component';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {toSignal} from '@angular/core/rxjs-interop';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {map, shareReplay} from 'rxjs';
import {RouterOutlet} from '@angular/router';
import {NGX_LOGGER} from 'ngx-logger';


@Component({
  selector: 'app-layout',
  imports: [
    MatSidenav,
    MatSidenavContainer,
    MatSidenavContent,
    NavigationComponent,
    NavigationComponent,
    HeaderComponent,
    RouterOutlet,
    FooterComponent
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent {
  private breakpointObserver = inject(BreakpointObserver);
  private logger = inject(NGX_LOGGER);

  isHandset = toSignal(this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => {
        this.logger.debug('Breakpoint observer result', { matches: result.matches });
        return result.matches;
      }),
      shareReplay()
    ), {initialValue: false});

  constructor() {
    this.logger.debug('Initialisation du composant Layout');
    this.logger.debug('État initial du mode Handset', { isHandset: this.isHandset() });
  }
}
