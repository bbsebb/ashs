import {inject, Injectable} from '@angular/core';
import {PaginationOption} from '../model/ngx-pagination-option.type';
import {delay, forkJoin, MonoTypeOperatorFunction, Observable, ReplaySubject, throwError} from 'rxjs';
import {AllHalResources, HalLink, HalResource, PaginatedHalResource} from '../model/ngx-hal.model';
import {HttpClient} from '@angular/common/http';
import {BASE_URL_CONFIG} from '../config/base-url.config';
import {DELAY} from '../config/delay.config';
import {NGX_LOGGER, NgxLoggerService} from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class NgxHalFormsService {


  private _root: ReplaySubject<HalResource> = new ReplaySubject<HalResource>(1);
  private readonly baseUrl = inject(BASE_URL_CONFIG).baseUrl // 'http://localhost:8082/api';
  private readonly delay = inject(DELAY);
  private readonly http = inject(HttpClient);
  private readonly logger = inject(NGX_LOGGER);
  private readonly header = {
    headers: {
      Accept: 'application/prs.hal-forms+json', // Ou 'application/prs.hal-forms+json' si HAL-FORMS est attendu
    }
  };

  private simulateDelay<T>(ms: number = this.delay): MonoTypeOperatorFunction<T> {
    return delay(ms);
  }

  constructor() {
    this.logger.debug('Initialisation du service NgxHalFormsService');
    this.logger.debug('URL de base configurée:', this.baseUrl);
    this.logger.debug('Délai configuré:', this.delay);

    this.logger.info('Chargement de la ressource racine HAL');
    this.loadRoot().subscribe({
      next: r => {
        this.logger.info('Ressource racine HAL chargée avec succès');
        this.logger.debug('Ressource racine:', r);
        this.root = r;
      },
      error: err => {
        this.logger.error('Erreur lors du chargement de la ressource racine HAL', err);
        this._root.error(err);   // 🔥 transmet l'erreur à root
      }
    });
  }

  get root(): Observable<HalResource> {
    return this._root.asObservable();
  }

  set root(value: HalResource) {
    this._root.next(value);
  }


  private loadRoot(): Observable<HalResource> {
    this.logger.debug('Chargement de la ressource racine depuis:', this.baseUrl);
    return this.http.get<HalResource>(this.baseUrl, this.header).pipe(
      // delay(2000),
      /*      map(() => {
              throw new Error("test")
            })*/
    );
  }

  public loadResource<T extends HalResource>(href: string): Observable<T> {
    this.logger.debug('Chargement de la ressource depuis:', href);
    return this.http.get<T>(href, this.header).pipe(
      this.simulateDelay()
    );
  }

  public hasFollow(resource: HalResource | PaginatedHalResource<HalResource>, linkName: string): boolean {
    return !!resource._links[linkName];
  }

  public follow<T extends HalResource | HalResource[] | PaginatedHalResource<HalResource>>(resource: HalResource | PaginatedHalResource<HalResource> | AllHalResources, linkName: string, params?: Param): Observable<T> {
    if (!this.hasFollow(resource, linkName)) {
      throw new Error("Link inexistant.");
    }

    if (Array.isArray(resource._links[linkName])) {
      return forkJoin(resource._links[linkName].map(
        l => {
          const url = this.createUrl(l);
          this.addParams(params, url);
          return this.http.get(url.toString(), this.header);
        }
      )).pipe(this.simulateDelay()) as Observable<T>;
    }
    const url = this.createUrl(resource._links[linkName]);
    this.addParams(params, url);

    return this.http.get<T>(url.toString(), this.header).pipe(this.simulateDelay());
  }

  private createUrl(halLink: HalLink): URL {
    const REGEX_TEMPLATE = /{[^}]*}/g;
    const hrefSansTemplate = halLink.templated
      ? halLink.href.replace(REGEX_TEMPLATE, '')
      : halLink.href;
    return new URL(hrefSansTemplate);
  }


  private addParams(params: Param | undefined, url: URL) {
    if (params) {
      Object.keys(params).forEach(key => {
        url.searchParams.append(key, String(params[key]));
      });
    }
  }

  public canAction(resource: HalResource, actionName: string): boolean {
    return !!(resource._templates && resource._templates[actionName]);
  }

  public doAction<T extends HalResource | HalResource[] | void>(resource: HalResource, actionName: string, payload?: any): Observable<T> {
    if (!this.canAction(resource, actionName)) {
      return throwError(() => new Error("The action " + actionName + " is not defined in the resource " + resource + ""));
    }
    const url = (resource._templates![actionName].target) ?? resource._links["self"].href;
    const template = resource._templates![actionName];
    const method = template.method;
    switch (method) {
      case 'POST':
        return this.http.post<T>(url, payload, this.header)
          .pipe(this.simulateDelay());
      case 'PUT':
        return this.http.put<T>(url, payload, this.header)
          .pipe(this.simulateDelay());
      case 'DELETE':
        return this.http.delete<T>(url, this.header)
          .pipe(this.simulateDelay());
      default:
        throw new Error("The method " + method + " is not defined in the resource " + resource + "");
    }
  }


  /**
   * Builds the pagination parameters for API requests
   * @returns The pagination parameters
   * @param paginationOption
   */
  buildParamPage(paginationOption: PaginationOption) {
    let paramPage: {
      page?: number;
      size?: number;
      sort?: string;
    } = {}
    if (paginationOption !== 'all') {
      paramPage = {
        page: paginationOption.page,
        size: paginationOption.size
      }
    }
    return paramPage;
  }
}

type Param = {
  [key: string]: string | number | boolean;
}
