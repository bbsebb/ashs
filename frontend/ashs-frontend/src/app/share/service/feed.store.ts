import {computed, inject, Injectable, signal, Signal, WritableSignal} from '@angular/core';

import {FeedService} from '@app/share/service/feed.service';
import {getPagination, hasPagination, PaginationOption, unwrap} from 'ngx-hal-forms';
import {Feed} from '@app/share/model/feed';
import {rxResource} from '@angular/core/rxjs-interop';
import {NGX_LOGGER} from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class FeedStore {
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 6,
    page: 0
  };
  private readonly instagramService = inject(FeedService);
  private readonly logger = inject(NGX_LOGGER);
  _paginationOption: WritableSignal<PaginationOption> = signal(FeedStore.PAGINATION_OPTION_DEFAULT);
  _feedsResource;

  constructor() {
    this.logger.debug('Initialisation du FeedStore');
    this._feedsResource = this.loadFeeds();
    this.logger.debug('FeedStore initialisé avec les options de pagination par défaut',
      FeedStore.PAGINATION_OPTION_DEFAULT);
  }

  get paginationOption() {
    this.logger.debug('Récupération des options de pagination actuelles');
    return this._paginationOption;
  }

  loadFeeds() {
    this.logger.info('Chargement des feeds');
    return rxResource({
      request: () => {
        const options = this._paginationOption();
        this.logger.debug('Préparation de la requête avec les options', options);
        return options;
      },
      loader: ({request}) => {
        this.logger.debug('Exécution du chargement des feeds avec les options', request);
        return this.instagramService.getFeeds(request);
      }
    });
  }

  getFeeds(): Signal<Feed[]> {
    this.logger.debug('Récupération du signal de feeds');
    return computed(() => {
      const feedsResource = this._feedsResource.value();
      if (feedsResource) {
        this.logger.debug('Extraction des feeds depuis la ressource');
        const feeds = unwrap<Feed[]>(feedsResource, 'feedDTOResponseList');
        this.logger.debug('Feeds extraits avec succès', {count: feeds.length});
        return feeds;
      } else {
        this.logger.debug('Aucune ressource de feeds disponible, retour d\'un tableau vide');
        return [];
      }
    });
  }

  getError() {
    this.logger.debug('Récupération des erreurs éventuelles');
    const error = this._feedsResource.error;
    if (error()) {
      this.logger.warn('Erreur détectée dans la ressource de feeds', error());
    }
    return error;
  }

  isLoading() {
    const loading = this._feedsResource.isLoading;
    this.logger.debug('État de chargement des feeds', {loading});
    return loading;
  }

  getStatus() {
    const status = this._feedsResource.status;
    this.logger.debug('Statut de la ressource de feeds', {status});
    return status;
  }

  hasPagination(): Signal<boolean> {
    this.logger.debug('Vérification de la présence de pagination');
    return computed(() => {
      const hasPaginationValue = hasPagination(this._feedsResource.value());
      this.logger.debug('Résultat de la vérification de pagination', {hasPagination: hasPaginationValue});
      return hasPaginationValue;
    });
  }

  getPagination() {
    this.logger.debug('Récupération des informations de pagination');
    return computed(() => {
      const pagination = getPagination(this._feedsResource.value());
      this.logger.debug('Informations de pagination récupérées', pagination);
      return pagination;
    });
  }

  updatePagination(pageSize: number, page: number) {
    this.logger.info('Mise à jour de la pagination', {pageSize, page});

    this._paginationOption.update((paginationOption): PaginationOption => {
      if (paginationOption === 'all') {
        this.logger.error('Impossible de changer la taille de page pour une pagination inexistante');
        return 'all'; // ou page = 0 ou autre
      }

      this.logger.debug('Pagination mise à jour', {
        previous: paginationOption,
        new: {size: pageSize, page: page}
      });

      return {
        size: pageSize,
        page: page,
      };
    });
  }

  next() {
    this.logger.info('Passage à la page suivante');

    if (!this.hasPagination()) {
      this.logger.error('Tentative de passage à la page suivante sans pagination');
      throw new Error('No pagination');
    }

    this._paginationOption.update((pagination) => {
      if (pagination === 'all') {
        this.logger.warn('Pagination "all" détectée, impossible de passer à la page suivante');
        return pagination;
      }

      const nextPage = pagination.page + 1;
      this.logger.debug('Passage à la page suivante', {
        currentPage: pagination.page,
        nextPage: nextPage
      });

      return {
        ...pagination,
        page: nextPage
      };
    });
  }
}
