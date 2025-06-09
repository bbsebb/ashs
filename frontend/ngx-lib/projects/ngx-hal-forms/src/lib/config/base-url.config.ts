import {InjectionToken} from '@angular/core';

export interface BaseUrlConfig {
  baseUrl: string;
}

export const BASE_URL_CONFIG = new InjectionToken<BaseUrlConfig>('BASE_URL_CONFIG');
