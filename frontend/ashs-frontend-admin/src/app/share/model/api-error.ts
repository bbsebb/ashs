type ApiErrorModel = {
  type: string;        // URI identifiant le type d’erreur
  title: string;       // Titre ou résumé de l’erreur
  status: number;      // Code HTTP correspondant
  detail: string;      // Détails du problème
  instance: string;    // Chemin ou identifiant de la requête concernée
};

export class ApiError {
  private readonly error: ApiErrorModel;

  // Les erreurs parsées au format : [{ field: string, message: string }]
  private _parsedDetails: { field: string; message: string }[] = [];

  constructor(error: ApiErrorModel) {
    this.error = error;
    this.parseDetails();
  }

  static of(error: ApiErrorModel): ApiError {
    return new ApiError(error);
  }

  /**
   * Parse le champ `detail` sous la forme :
   * "champA: messageA, champB: messageB" → [{ field: "champA", message: "messageA" }, ...]
   */
  private parseDetails() {
    if (!this.error || !this.error.detail) {
      this._parsedDetails = [];
      return;
    }

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
    if (!field) {
      return this.getGenericMessage();
    }
    const found = this._parsedDetails.find(detail => detail.field === field);
    if (!found) {
      return this.getGenericMessage();
    }
    return found?.message;
  }

  public getGenericMessage(): string {
    return 'Une erreur est survenue.';
  }
}
