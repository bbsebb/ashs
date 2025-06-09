import {Component, computed, inject, input, output, Signal} from '@angular/core';
import {MatToolbar} from "@angular/material/toolbar";
import {RouterLink} from "@angular/router";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MatIconAnchor, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ScrollDispatcher, ViewportRuler} from '@angular/cdk/overlay';
import {toSignal} from '@angular/core/rxjs-interop';
import {filter, map, Observable, scan, skip} from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbar,
    RouterLink,
    MatIconAnchor
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  animations: [
    trigger('toolbarAnimation', [
      state('open', style({transform: 'translateY(0px)'})),
      state('close', style({transform: 'translateY(-100px)'})),
      transition('open => close', [animate('300ms ease-out')]),
      transition('close  => open', [animate('300ms ease-in')]),
    ]),
  ],
})
export class HeaderComponent {
  isHandset = input.required();
  opening = output<void>()
  private readonly scrollDispatcher = inject(ScrollDispatcher);
  private readonly viewportRuler = inject(ViewportRuler);
  showToolbar: Signal<boolean>;
  private static readonly TOP_LIMIT_SHOW_TOOLBAR = 490;

  constructor() {
    const scrollLimit = toSignal(this.scrollLimit(this.scrollDispatcher, HeaderComponent.TOP_LIMIT_SHOW_TOOLBAR), {initialValue: true});
    const directionScroll = toSignal(this.scrollDirection(this.scrollDispatcher), {initialValue: true});
    // Calcul pour déterminer si la barre d'outils doit être affichée
    this.showToolbar = computed(() => directionScroll() || scrollLimit());
  }

  // Création d'un flux observable pour la position de défilement de la fenêtre
  getScrollTop(scrollDispatcher: ScrollDispatcher): Observable<number> {
    return scrollDispatcher.scrolled().pipe(
      filter((cdkScrollable) => !!cdkScrollable),
      filter((cdkScrollable) => cdkScrollable.getElementRef().nativeElement.tagName === 'MAT-SIDENAV-CONTENT'),
      map((cdkScrollable) => cdkScrollable.measureScrollOffset('top')),
    );
  }

  // Définition de la limite de défilement pour afficher la barre d'outils
  scrollLimit(scrollDispatcher: ScrollDispatcher, limit: number): Observable<boolean> {
    return this.getScrollTop(scrollDispatcher).pipe(
      map((top) => top < limit),
    );
  }

  // Détermination de la direction du défilement pour afficher ou masquer la barre d'outils
  scrollDirection(scrollDispatcher: ScrollDispatcher): Observable<boolean> {
    return this.getScrollTop(scrollDispatcher).pipe(
      scan((acc: number[], current: number) => [acc[1], current], [this.viewportRuler.getViewportScrollPosition().top, 0]),
      skip(1), // On a besoin d'au moins deux valeurs pour savoir si le défilement monte ou descent
      map(([prev, current]) => prev >= current),
    );
  }


}
