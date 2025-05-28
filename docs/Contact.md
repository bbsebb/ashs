# Documentation de la Fonctionnalité : Contact

---

## 1. Introduction

Le service **Contact** permet aux utilisateurs d'un site web d'envoyer des messages à l'administration via un formulaire de contact. Une fois le formulaire soumis, le backend valide les données, formate un e-mail et l'envoie à une adresse prédéfinie.

Ce document est structuré autour des aspects suivants :
- **Frontend** : Interface visible par l'utilisateur (place laissée pour future implémentation).
- **Backend** : Ensemble des services gérant la validation, l'envoi des données et la configuration.
- **Diagrammes (Structurizr)** : À ajouter ultérieurement pour représenter les flux et l'architecture de ce module.

---

## 2. Vision fonctionnelle

Ce module se compose des éléments suivants :
1. **Frontend** : Présente un formulaire utilisateur, valide les entrées, et communique les données au backend (partie non encore implémentée).
2. **Backend** : Reçoit, valide et transmet les messages sous forme d'e-mails via un serveur SMTP.
3. **Flux** (général) :
  - Un utilisateur envoie des informations (nom, e-mail, message) via le formulaire.
  - Le backend vérifie les données (sécurité, erreurs).
  - Un e-mail est généré et transmis à une adresse administrateur.

---

## 3. Architecture

### 3.1 Backend

[Voir la documentation spécifique du backend](../backend/contact-service/HELP.md)

#### Endpoints exposés

| Méthode | URI          | Description                                     | Exemples de réponses                                |
|---------|--------------|-------------------------------------------------|-----------------------------------------------------|
| POST    | `/sendEmail` | Envoi d'un message via le formulaire de contact | Succès (`204 No Content`), Erreurs (`400` ou `500`) |

Les réponses de l'API sont formatées en HAL (Hypertext Application Language), ce qui permet une représentation
hypermédia standardisée des ressources.
##### Exemple d'appel à l'API :
**Requête POST :**
```json
{
  "email": "john.doe@example.com",
  "name": "John Doe",
  "message": "Bonjour, je souhaiterais avoir plus d'informations."
}
```

**Réponse en cas de succès :**

Pas de contenu (204)


**Réponse en cas d'erreur :**
```json
{
  "type": "https://example.com/probs/email-error",
  "title": "Invalid email address",
  "status": 400,
  "detail": "The provided email address is not in a valid format.",
  "instance": "/contact/message"
}
```
### 3.2 Front end

---

## 4. Sécurité

### 4.1 Backend

Le backend communique avec le frontend via le protocole HTTPS afin de garantir une transmission sécurisée des données
entre les deux composants.


### 4.1 Backend

[Voir la documentation spécifique du backend](../backend/contact-service/HELP.md)

---

## 5. Tests et Validation

### 5.1 Tests unitaires

[Voir la documentation spécifique du backend](../backend/contact-service/HELP.md)

### 5.2 Tests d'intégration et E2E


---

## 6. Documentation Structurizr

**Diagrammes à ajouter :**
1. **Diagramme de contexte :**
  - Illustre les interactions globales entre l'utilisateur, le frontend, et le backend.

1. **Diagramme de conteneurs :**
  - Décrit la structure interne du module backend et ses dépendances.

1. **Diagramme de composant (backend uniquement) :**
  - Visualise les principales classes ou services impliqués dans le traitement des e-mails.

---

## 7. Améliorations futures

### Fonctionnalités envisageables
- **Confirmation à l'utilisateur** : Ajouter une notification par e-mail pour confirmer au demandeur que son message a bien été reçu.
- **Gestion des spams** : Intégration de Google reCAPTCHA pour limiter les abus.
- **Journalisation persistante** : Enregistrement des messages reçus dans une base de données pour suivi.

### Optimisation technique
- **Évolutivité** :
  - Modularisation d'autres points (comme un service email générique si d'autres modules en ont besoin).
- **Monitoring avancé** :
  - Ajouter des métriques Prometheus spécifiques à ce module (e.g., nombre de messages reçus par heure).

---

Ce document offre une base solide pour comprendre et utiliser le module de contact. Les sections incomplètes (comme le frontend ou les diagrammes Structurizr) pourront être enrichies dès qu'elles seront développées ou disponibles.