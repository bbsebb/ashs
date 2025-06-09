import {AllHalResources, HalResource, PaginatedHalResource} from '../model/ngx-hal.model';
import {Pagination} from '../model/ngx-pagination.model';

export function unwrap<T extends HalResource | HalResource[]>(resource: HalResource, embeddedName: string): T {
  if (!resource._embedded || !resource._embedded[embeddedName]) {
    throw new Error(`You don't have the embedded resource ${embeddedName} in the resource ${resource}`);
  }
  return resource._embedded[embeddedName] as T;
}

export function removeLink<T extends HalResource>(resource: T, linkName: string, href: string): T {
  if (!resource._links[linkName]) {
    throw new Error(`The link ${linkName} is not defined in the resource ${resource}`);
  }
  if (Array.isArray(resource._links[linkName])) {
    resource._links[linkName] = resource._links[linkName].filter(link => link.href !== href);
  } else {
    delete resource._links[linkName];
  }
  return resource;
}

export function addLink<T extends HalResource>(resource: T, linkName: string, href: string): T {
  if (!resource._links[linkName]) {
    resource._links = {
      ...resource._links,
      [linkName]: {
        href: href
      }
    };
  } else if (Array.isArray(resource._links[linkName])) {
    resource._links[linkName] = [...resource._links[linkName], {href}];
  } else {
    resource._links[linkName] = [resource._links[linkName], {href}];
  }
  return resource;
}

export function hasPagination<T extends HalResource>(resource: AllHalResources<T> | PaginatedHalResource<T>): resource is PaginatedHalResource<T> {
  return (resource as Pagination).page !== undefined;
}

export function getPagination<T extends HalResource>(
  resource: AllHalResources<T> | PaginatedHalResource<T> | undefined
): Pagination | undefined {
  if (resource && hasPagination(resource)) {
    return {page: resource.page, _links: resource._links};
  } else {
    return undefined;
  }
}

export function deleteItemInEmbedded<R extends HalResource>(
  resource: R | undefined,
  embeddedName: string,
  itemToDelete: HalResource
): R | undefined {
  if (!resource) {
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const newEmbeddedItem = items.filter(i => i._links.self.href !== itemToDelete._links.self.href);
  return {
    ...resource,
    _embedded: {
      ...resource._embedded,
      [embeddedName]: newEmbeddedItem
    }
  };
}

export function addItemInEmbedded<R extends HalResource>(
  resource: R | undefined,
  embeddedName: string,
  itemToAdd: HalResource
): R | undefined {
  if (!resource) {
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const newEmbeddedItem = [...items, itemToAdd];
  return {
    ...resource,
    _embedded: {
      ...resource._embedded,
      [embeddedName]: newEmbeddedItem
    }
  };
}

export function setItemInEmbedded<R extends HalResource>(
  resource: R | undefined,
  embeddedName: string,
  itemToSet: HalResource
): R | undefined {
  if (!resource) {
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const newEmbeddedItem = items.map(i => i._links.self.href === itemToSet._links.self.href ? itemToSet : i);
  return {
    ...resource,
    _embedded: {
      ...resource._embedded,
      [embeddedName]: newEmbeddedItem
    }
  };
}
