import {Injectable} from '@angular/core';
import {NgxLoggerService} from './ngx-logger.service';


@Injectable({
  providedIn: 'root'
})
export class NgxConsoleLoggerService implements NgxLoggerService {

  /**
   * Internal method to log messages with the specified level
   * @param level The log level ('debug', 'info', 'warn', 'error')
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  private log(level: 'debug' | 'info' | 'warn' | 'error', message: string, ...optionalParams: any[]): void {
    switch (level) {
      case 'debug':
        console.debug(message, ...optionalParams);
        break;
      case 'info':
        console.info(message, ...optionalParams);
        break;
      case 'warn':
        console.warn(message, ...optionalParams);
        break;
      case 'error':
        console.error(message, ...optionalParams);
        break;
    }
  }

  /**
   * Log a debug message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  debug(message: string, ...optionalParams: any[]): void {
    this.log('debug', message, ...optionalParams);
  }

  /**
   * Log an info message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  info(message: string, ...optionalParams: any[]): void {
    this.log('info', message, ...optionalParams);
  }

  /**
   * Log a warning message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  warn(message: string, ...optionalParams: any[]): void {
    this.log('warn', message, ...optionalParams);
  }

  /**
   * Log an error message
   * @param message The message to log
   * @param optionalParams Optional parameters to include in the log
   */
  error(message: string, ...optionalParams: any[]): void {
    this.log('error', message, ...optionalParams);
  }
}
