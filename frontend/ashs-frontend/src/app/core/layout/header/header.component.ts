import {Component, computed, inject, input, output, Signal} from '@angular/core';
import {MatToolbar} from "@angular/material/toolbar";
import {RouterLink} from "@angular/router";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MatIconAnchor, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {ScrollDispatcher, ViewportRuler} from '@angular/cdk/overlay';
import {toSignal} from '@angular/core/rxjs-interop';
import {filter, map, Observable, scan, skip, tap} from 'rxjs';
import {NGX_LOGGER} from 'ngx-logger';

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
  private readonly logger = inject(NGX_LOGGER);
  showToolbar: Signal<boolean>;
  private static readonly TOP_LIMIT_SHOW_TOOLBAR = 490;

  constructor() {
    this.logger.debug('Initialisation du composant Header');
    this.logger.debug('Limite supérieure pour afficher la barre d\'outils', { limit: HeaderComponent.TOP_LIMIT_SHOW_TOOLBAR });

    const scrollLimit = toSignal(this.scrollLimit(this.scrollDispatcher, HeaderComponent.TOP_LIMIT_SHOW_TOOLBAR), {initialValue: true});
    const directionScroll = toSignal(this.scrollDirection(this.scrollDispatcher), {initialValue: true});

    // Calcul pour déterminer si la barre d'outils doit être affichée
    this.showToolbar = computed(() => {
      const result = directionScroll() || scrollLimit();
      this.logger.debug('Calcul de visibilité de la barre d\'outils', {
        directionScroll: directionScroll(),
        scrollLimit: scrollLimit(),
        showToolbar: result
      });
      return result;
    });
  }


  // Création d'un flux observable pour la position de défilement de la fenêtre
  getScrollTop(scrollDispatcher: ScrollDispatcher): Observable<number> {
    this.logger.debug('Création du flux observable pour la position de défilement');
    return scrollDispatcher.scrolled().pipe(
      filter((cdkScrollable) => {
        const isScrollable = !!cdkScrollable;
        this.logger.debug('Filtrage des événements de défilement', { isScrollable });
        return isScrollable;
      }),
      filter((cdkScrollable) => {
        const isMatSidenavContent = cdkScrollable.getElementRef().nativeElement.tagName === 'MAT-SIDENAV-CONTENT';
        this.logger.debug('Filtrage des événements de défilement par type d\'élément', { isMatSidenavContent });
        return isMatSidenavContent;
      }),
      map((cdkScrollable) => {
        const scrollTop = cdkScrollable.measureScrollOffset('top');
        this.logger.debug('Position de défilement mesurée', { scrollTop });
        return scrollTop;
      }),
    );
  }

  // Définition de la limite de défilement pour afficher la barre d'outils
  scrollLimit(scrollDispatcher: ScrollDispatcher, limit: number): Observable<boolean> {
    this.logger.debug('Création du flux observable pour la limite de défilement', { limit });
    return this.getScrollTop(scrollDispatcher).pipe(
      map((top) => {
        const belowLimit = top < limit;
        this.logger.debug('Vérification de la limite de défilement', { scrollTop: top, limit, belowLimit });
        return belowLimit;
      }),
    );
  }

  // Détermination de la direction du défilement pour afficher ou masquer la barre d'outils
  scrollDirection(scrollDispatcher: ScrollDispatcher): Observable<boolean> {
    this.logger.debug('Création du flux observable pour la direction de défilement');
    const initialScrollTop = this.viewportRuler.getViewportScrollPosition().top;
    this.logger.debug('Position initiale de défilement', { initialScrollTop });

    return this.getScrollTop(scrollDispatcher).pipe(
      scan((acc: number[], current: number) => {
        const newAcc = [acc[1], current];
        this.logger.debug('Accumulation des positions de défilement', { previous: acc[1], current, newAcc });
        return newAcc;
      }, [initialScrollTop, 0]),
      skip(1), // On a besoin d'au moins deux valeurs pour savoir si le défilement monte ou descent
      map(([prev, current]) => {
        const isScrollingUp = prev >= current;
        this.logger.debug('Détermination de la direction de défilement', {
          previous: prev,
          current,
          isScrollingUp
        });
        return isScrollingUp;
      }),
    );
  }
}
