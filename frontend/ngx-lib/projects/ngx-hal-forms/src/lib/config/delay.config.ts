import {InjectionToken} from '@angular/core';

export const DELAY = new InjectionToken<number>('DELAY', {
  providedIn: 'root',
  factory: () => 2000,
});
