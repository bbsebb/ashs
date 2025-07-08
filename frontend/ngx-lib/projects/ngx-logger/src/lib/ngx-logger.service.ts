import {InjectionToken} from '@angular/core';

export interface NgxLoggerService {
  /**
   * Log a debug message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  debug(message: string, ...optionalParams: any[]): void;

  /**
   * Log an info message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  info(message: string, ...optionalParams: any[]): void;

  /**
   * Log a warning message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  warn(message: string, ...optionalParams: any[]): void;

  /**
   * Log an error message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  error(message: string, ...optionalParams: any[]): void;
}

export const NGX_LOGGER = new InjectionToken<NgxLoggerService>("logger");
