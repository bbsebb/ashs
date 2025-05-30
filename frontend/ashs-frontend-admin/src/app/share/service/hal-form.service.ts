import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {delay, forkJoin, Observable, ReplaySubject} from 'rxjs';
import {AllHalResources, HalLink, HalResource, PaginatedHalResource} from '../model/hal/hal';
import {Pagination} from '@app/share/model/hal/pagination';
import {PaginationOption} from '@app/share/service/pagination-option';

@Injectable({
  providedIn: 'root'
})
export class HalFormService {


  private _root: ReplaySubject<HalResource> = new ReplaySubject<HalResource>(1);
  private readonly baseUrl = 'http://localhost:8082/api';
  private readonly http = inject(HttpClient);
  private readonly header = {
    headers: {
      Accept: 'application/prs.hal-forms+json', // Ou 'application/prs.hal-forms+json' si HAL-FORMS est attendu
    }
  };

  constructor() {
    this.loadRoot().subscribe({
      next: r => this.root = r,
      error: err => this._root.error(err)   // 🔥 transmet l'erreur à root
    });
  }

  get root(): Observable<HalResource> {
    return this._root.asObservable();
  }

  set root(value: HalResource) {
    this._root.next(value);
  }


  private loadRoot(): Observable<HalResource> {
    return this.http.get<HalResource>(this.baseUrl, this.header).pipe(
      delay(2000),
      /*      map(() => {
              throw new Error("test")
            })*/
    );

  }

  public loadResource<T extends HalResource>(href: string): Observable<T> {
    return this.http.get<T>(href, this.header);
  }

  public hasFollow(resource: HalResource | PaginatedHalResource<HalResource>, linkName: string): boolean {
    return !!resource._links[linkName];
  }

  public follow<T extends HalResource | HalResource[] | PaginatedHalResource<HalResource>>(resource: HalResource | PaginatedHalResource<HalResource> | AllHalResources<HalResource>, linkName: string, params?: Param): Observable<T> {
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
      )).pipe(delay(2000)) as Observable<T>;
    }
    const url = this.createUrl(resource._links[linkName]);
    this.addParams(params, url);

    return this.http.get<T>(url.toString(), this.header).pipe(delay(2000));
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
      throw new Error("The action " + actionName + " is not defined in the resource " + resource + "");
    }
    const url = (resource._templates![actionName].target) ?? resource._links["self"].href;
    const template = resource._templates![actionName];
    const method = template.method;
    switch (method) {
      case 'POST':
        return this.http.post<T>(url, payload, this.header)
          .pipe(delay(2000));
      case 'PUT':
        return this.http.put<T>(url, payload, this.header)
          .pipe(delay(2000));
      case 'DELETE':
        return this.http.delete<T>(url, this.header)
          .pipe(delay(2000));
      default:
        throw new Error("The method " + method + " is not defined in the resource " + resource + "");
    }
  }

  public unwrap<T extends HalResource | HalResource[]>(resource: HalResource, embeddedName: string): T {
    if (!resource._embedded || !resource._embedded[embeddedName]) {
      throw new Error("You don't have the embedded resource " + embeddedName + " in the resource " + resource + "");
    }
    return resource._embedded[embeddedName] as T;
  }

  public removeLink<T extends HalResource>(resource: T, linkName: string, href: string): T {
    if (!resource._links[linkName]) {
      throw new Error("The link " + linkName + " is not defined in the resource " + resource + "");
    }
    if (Array.isArray(resource._links[linkName])) {
      resource._links[linkName] = resource._links[linkName].filter(link => link.href !== href);
    } else {
      delete resource._links[linkName];
    }
    return resource;
  }

  public addLink<T extends HalResource>(resource: T, linkName: string, href: string): T {
    if (!resource._links[linkName]) {
      resource._links = {
        ...resource._links,
        [linkName]: {
          href: href
        }
      }
    } else if (Array.isArray(resource._links[linkName])) {
      resource._links[linkName] = [...resource._links[linkName], {
        href: href
      }];
    } else {
      resource._links[linkName] = [resource._links[linkName], {
        href: href
      }];
    }
    return resource;
  }

  public hasPagination<T extends HalResource>(resource: AllHalResources<T> | PaginatedHalResource<T>): resource is PaginatedHalResource<T> {
    return (resource as Pagination).page !== undefined;
  }

  getPagination<T extends HalResource>(resource: AllHalResources<T> | PaginatedHalResource<T> | undefined): Pagination | undefined {
    if (resource && this.hasPagination(resource)) {
      return {page: resource.page, _links: resource._links};
    } else {
      return undefined;
    }
  }

  /**
   * Builds the pagination parameters for API requests
   * @returns The pagination parameters
   * @param paginationOption
   */
  buildParamPage(paginationOption: PaginationOption) {
    let paramPage: ParamPage = {}
    if (paginationOption !== 'all') {
      paramPage = {
        page: paginationOption.page,
        size: paginationOption.size
      }
    }
    return paramPage;
  }
}

export type Param = {
  [key: string]: string | number | boolean;
}

export type ParamPage = {
  page?: number;
  size?: number;
  sort?: string;
}
