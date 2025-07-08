import {NgxLoggerService} from 'ngx-logger';


type ApiErrorModel = {
  type: string;        // URI identifiant le type d’erreur
  title: string;       // Titre ou résumé de l’erreur
  status: number;      // Code HTTP correspondant
  detail: string;      // Détails du problème
  instance: string;    // Chemin ou identifiant de la requête concernée
};

export class NgxApiError {
  private readonly error: ApiErrorModel;
  private readonly logger;

  // Les erreurs parsées au format : [{ field: string, message: string }]
  private _parsedDetails: { field: string; message: string }[] = [];

  constructor(error: ApiErrorModel, logger?: NgxLoggerService) {
    this.logger = logger;
    this.logger?.debug('Creating NgxApiError instance');
    this.error = error;
    this.logger?.debug('API Error details:', error);
    this.parseDetails();
  }

  static of(error: ApiErrorModel): NgxApiError {
    return new NgxApiError(error);
  }

  /**
   * Parse le champ `detail` sous la forme :
   * "champA: messageA, champB: messageB" → [{ field: "champA", message: "messageA" }, ...]
   */
  private parseDetails() {
    this.logger?.debug('Parsing API error details');
    if (!this.error || !this.error.detail) {
      this.logger?.debug('No error details to parse');
      this._parsedDetails = [];
      return;
    }

    this.logger?.debug('Raw error detail:', this.error.detail);
    this._parsedDetails = this.error.detail
      .split(',')
      .map(part => part.trim())
      .filter(Boolean)
      .map(item => {
        // Sépare sur le premier ":"
        const [field, ...rest] = item.split(':');
        return {
          field: (field ?? '').trim(),
          message: rest.join(':').trim(), // au cas où ":" dans le message
        };
      })
      .filter(detail => detail.field && detail.message); // filtre les objets vides

    this.logger?.debug('Parsed error details:', this._parsedDetails);
  }

  public get parsedDetails(): { field: string; message: string }[] {
    return this._parsedDetails;
  }

  /**
   * Retourne l’objet d’erreur d’origine si besoin.
   */
  public get apiError(): ApiErrorModel {
    return this.error;
  }

  /**
   * Permet de récupérer directement le message d’erreur pour un champ donné.
   */
  public getMessageForField(field: string | undefined = undefined): string | undefined {
    this.logger?.debug(`Getting error message for field: ${field || 'undefined'}`);
    if (!field) {
      this.logger?.debug('No field specified, returning generic message');
      return this.getGenericMessage();
    }
    const found = this._parsedDetails.find(detail => detail.field === field);
    if (!found) {
      this.logger?.debug(`No error found for field: ${field}, returning generic message`);
      return this.getGenericMessage();
    }
    this.logger?.debug(`Found error message for field ${field}:`, found.message);
    return found?.message;
  }

  public getGenericMessage(): string {
    this.logger?.debug('Getting generic error message');
    return 'Une erreur est survenue.';
  }
}
