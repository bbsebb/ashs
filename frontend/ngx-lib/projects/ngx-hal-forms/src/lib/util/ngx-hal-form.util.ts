import {AllHalResources, HalResource, PaginatedHalResource} from '../model/ngx-hal.model';
import {Pagination} from '../model/ngx-pagination.model';
import {NgxLoggerService} from 'ngx-logger';


// Inject loggerService
export function unwrap<T extends HalResource | HalResource[]>(resource: HalResource, embeddedName: string, loggerService?: NgxLoggerService): T {
  loggerService?.debug(`Unwrapping embedded resource: ${embeddedName}`);
  if (!resource._embedded || !resource._embedded[embeddedName]) {
    throw new Error(`You don't have the embedded resource ${embeddedName} in the resource ${resource}`);
  }
  return resource._embedded[embeddedName] as T;
}

export function removeLink<T extends HalResource>(resource: T, linkName: string, href: string, loggerService?: NgxLoggerService): T {
  loggerService?.debug(`Removing link: ${linkName} with href: ${href}`);
  if (!resource._links[linkName]) {
    loggerService?.error(`Link removal failed: The link ${linkName} is not defined in the resource`);
    throw new Error(`The link ${linkName} is not defined in the resource ${resource}`);
  }
  if (Array.isArray(resource._links[linkName])) {
    resource._links[linkName] = resource._links[linkName].filter(link => link.href !== href);
    loggerService?.debug(`Filtered array of links for ${linkName}`);
  } else {
    delete resource._links[linkName];
    loggerService?.debug(`Deleted single link ${linkName}`);
  }
  return resource;
}

export function addLink<T extends HalResource>(resource: T, linkName: string, href: string, loggerService?: NgxLoggerService): T {
  loggerService?.debug(`Adding link: ${linkName} with href: ${href}`);
  if (!resource._links[linkName]) {
    resource._links = {
      ...resource._links,
      [linkName]: {
        href: href
      }
    };
    loggerService?.debug(`Created new link ${linkName}`);
  } else if (Array.isArray(resource._links[linkName])) {
    resource._links[linkName] = [...resource._links[linkName], {href}];
    loggerService?.debug(`Added to existing array of links for ${linkName}`);
  } else {
    resource._links[linkName] = [resource._links[linkName], {href}];
    loggerService?.debug(`Converted single link to array for ${linkName}`);
  }
  return resource;
}

export function hasPagination<T extends HalResource>(resource: AllHalResources<T> | PaginatedHalResource<T> | undefined, loggerService?: NgxLoggerService): resource is PaginatedHalResource<T> {
  loggerService?.debug('Checking if resource has pagination');
  if (!resource) {
    loggerService?.debug('Resource is undefined, no pagination');
    return false;
  }
  const hasPagination = (resource as Pagination).page !== undefined;
  loggerService?.debug(`Resource ${hasPagination ? 'has' : 'does not have'} pagination`);
  return hasPagination;
}

export function getPagination<T extends HalResource>(
  resource: AllHalResources<T> | PaginatedHalResource<T> | undefined,
  loggerService?: NgxLoggerService
): Pagination | undefined {
  loggerService?.debug('Getting pagination from resource');
  if (resource && hasPagination(resource)) {
    loggerService?.debug(`Pagination found: page ${resource.page.number}, size ${resource.page.size}, total elements ${resource.page.totalElements}`);
    return {page: resource.page, _links: resource._links};
  } else {
    loggerService?.debug('No pagination found in resource');
    return undefined;
  }
}

export function deleteItemInEmbedded<R extends HalResource>(
  resource: R | undefined,
  embeddedName: string,
  itemToDelete: HalResource,
  loggerService?: NgxLoggerService
): R | undefined {
  loggerService?.debug(`Deleting item from embedded resource: ${embeddedName}`);
  if (!resource) {
    loggerService?.debug('Resource is undefined, nothing to delete');
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const itemSelfLink = itemToDelete._links.self.href;
  loggerService?.debug(`Deleting item with self link: ${itemSelfLink}`);
  const newEmbeddedItem = items.filter(i => i._links.self.href !== itemSelfLink);
  loggerService?.debug(`Filtered ${items.length - newEmbeddedItem.length} items from ${embeddedName}`);
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
  itemToAdd: HalResource,
  loggerService?: NgxLoggerService
): R | undefined {
  loggerService?.debug(`Adding item to embedded resource: ${embeddedName}`);
  if (!resource) {
    loggerService?.debug('Resource is undefined, nothing to add to');
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const itemSelfLink = itemToAdd._links.self?.href;
  loggerService?.debug(`Adding item with self link: ${itemSelfLink || 'undefined'}`);
  const newEmbeddedItem = [...items, itemToAdd];
  loggerService?.debug(`Added item to ${embeddedName}, new count: ${newEmbeddedItem.length}`);
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
  itemToSet: HalResource,
  loggerService?: NgxLoggerService
): R | undefined {
  loggerService?.debug(`Setting item in embedded resource: ${embeddedName}`);
  if (!resource) {
    loggerService?.debug('Resource is undefined, nothing to set');
    return resource;
  }
  const items = unwrap<HalResource[]>(resource, embeddedName);
  const itemSelfLink = itemToSet._links.self.href;
  loggerService?.debug(`Setting item with self link: ${itemSelfLink}`);

  const newEmbeddedItem = items.map(i => {
    const matches = i._links.self.href === itemSelfLink;
    if (matches) {
      loggerService?.debug(`Found matching item to update in ${embeddedName}`);
    }
    return matches ? itemToSet : i;
  });

  const itemWasUpdated = newEmbeddedItem.some(i => i === itemToSet);
  loggerService?.debug(`Item ${itemWasUpdated ? 'was' : 'was not'} updated in ${embeddedName}`);

  return {
    ...resource,
    _embedded: {
      ...resource._embedded,
      [embeddedName]: newEmbeddedItem
    }
  };
}
