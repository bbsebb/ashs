export interface EmailDTORequest {
  /**
   * L'e-mail de l'expéditeur.
   * Doit être un e-mail valide, non vide.
   */
  email: string;

  /**
   * Le nom de l'expéditeur.
   * Doit comporter entre 3 et 50 caractères, non vide.
   */
  name: string;

  /**
   * Le contenu du message.
   * Doit comporter entre 10 et 1000 caractères, non vide.
   */
  message: string;

}
