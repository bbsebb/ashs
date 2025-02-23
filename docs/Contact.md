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

#### 3.1.1 Structure technique

| Éléments         | Description                                                                 |
|------------------|-----------------------------------------------------------------------------|
| **API REST**     | Expose un endpoint POST pour la réception des messages.                    |
| **Validation**   | Vérifie les données envoyées par l'utilisateur (via annotations Jakarta). |
| **Service SMTP** | Se charge de la transmission de l'e-mail au destinataire configuré.        |

#### 3.1.2 Modules/Classes

- **DTO (Data Transfer Object)** :
  - `EmailRequest` : Représente les données transmises par le frontend.
  - `ApiErrorModel` : Standardise les réponses d’erreur renvoyées par l'API.

- **Service** :
  - `EmailServiceImpl` : Implémente la logique pour la génération et l’envoi des e-mails.

- **Controller** :
  - `EmailController` :
    - Expose l’endpoint REST `/contact/message` pour recevoir les données utilisateur.
    - Appelle les services nécessaires à la validation et au traitement.

#### 3.1.3 Endpoints exposés

| Méthode | URI                | Authentification | Description                  |
|---------|--------------------|------------------|------------------------------|
| POST    | `/contact/message` | Aucune           | Reçoit un message utilisateur.|

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
```json
{
  "status": "Message envoyé avec succès"
}
```

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

---

## 4. Sécurité

### 4.1 Backend

1. **Validation stricte des données entrantes :**
  - Les annotations Jakarta Validation (@NotBlank, @Email...) protègent contre des données malformées.
  - Les tailles minimales et maximales des champs empêchent les abus.

2. **Protection des communications :**
  - Toutes les communications se font via HTTPS.
  - Les e-mails sont transmis via un serveur SMTP configuré avec **TLS/SSL**.

3. **Restriction accès administratif (adresse e-mail) :**
  - L'adresse e-mail de destination est configurée dans les propriétés d'application (`application.yml`) et sécurisée avec des variables d'environnement si nécessaire.

4. **Journalisation des incidents :**
  - Les erreurs (comme des échecs d'envoi d'e-mail) sont loguées avec Logback pour une analyse ultérieure.

---

## 5. Tests et Validation

### 5.1 Tests unitaires

| Composant              | Méthode      | Type de test         |
|------------------------|--------------|----------------------|
| **DTO** - Validation   | EmailRequest | Validation des entrées |
| **Service** - Email    | EmailService | Tests des cas d'envoi d'e-mails. |
| **Controller** - REST  | EmailController | Validation des endpoints exposés. |

#### Cas validés :
1. Champs manquants (`name`, `email`, ou `message`) renvoient des erreurs.
2. Formats invalides pour les champs (`email` incorrect, messages trop courts).
3. Test des réponses aux requêtes HTTP dans des cas d'erreur ou de succès.

### 5.2 Tests d'intégration

- **Via Mailhog** (Docker) :
  - Un conteneur local capture les e-mails envoyés par le service.
  - Exécution d'appels API pour valider que les messages formatés sont correctement transmis.

---

## 6. Documentation Structurizr

**Diagrammes à ajouter :**
1. **Diagramme de contexte :**
  - Illustre les interactions globales entre l'utilisateur, le frontend, et le backend.

2. **Diagramme de conteneurs :**
  - Décrit la structure interne du module backend et ses dépendances.

3. **Diagramme de composant (backend uniquement) :**
  - Visualise les principales classes ou services impliqués dans le traitement des e-mails.

---

## 7. Frontend (Placeholder)

### Éléments prévus
1. Affichage d'un formulaire utilisateur avec les champs nécessaires :
  - **Nom** (input text)
  - **E-mail** (input text)
  - **Message** (textarea)

2. Validation des entrées saisies côté client :
  - Contrôle de format (e-mail, taille des champs).
  - Feedback interactif à l'utilisateur (erreurs directes sous les champs).

3. Appel API :
  - Envoi des données au backend via une requête HTTP POST.

4. Interfaces responsives :
  - Adaptation aux écrans mobiles/tablettes pour une meilleure expérience utilisateur.

### Remarque :
Les détails de la structure et de l'implémentation Angular seront complétés après le développement.

---

## 8. Améliorations futures

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