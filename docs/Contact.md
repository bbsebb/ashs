# Modélisation Fonctionnelle et Validation de l'Envoi d'un E-mail via Formulaire

## 1. Cas d'Utilisation : Transmission d'un Message via un Formulaire

### 1.1 Description Générale
Dans le cadre d'une application web, un utilisateur soumet un formulaire de contact. Suite à cette action, un e-mail est généré et transmis à une adresse prédéfinie (non accessible depuis le frontend). Le système fournit un retour d'état indiquant le succès ou l'échec de l'opération.

---

## 2. Structuration en Modules Fonctionnels

### 2.1 Envoi du Message

#### 2.1.1 Interface Utilisateur (Frontend - Angular)
**Décomposition Fonctionnelle**
- **Présentation du formulaire** incluant les champs suivants :
    - Nom
    - Adresse e-mail
    - Contenu du message
- **Validation des entrées utilisateur** :
    - Nom : contrainte de longueur (3 à 50 caractères)
    - Adresse e-mail : respect du format standard
    - Message : contrainte de longueur (10 à 1000 caractères)
- **Transmission des données** au backend via une requête HTTP POST
- **Gestion des réponses serveur** :
    - Succès : affichage d'un message de confirmation
    - Erreur : affichage d'un message d'erreur approprié

#### 2.1.2 Architecture Backend - Service de Gestion des E-mails (Spring Boot)
**Décomposition Fonctionnelle**
- **Validation et filtrage des données entrantes**
- **Encapsulation des informations sous forme d'objet e-mail**
- **Transmission du message via un protocole SMTP** (ex : Gmail SMTP, SendGrid...)
- **Traitement des erreurs et retours de transmission**
    - Succès : réponse HTTP 200
    - Erreur : codes HTTP 400/500 accompagnés d'un message d'explication

### 2.2 Gestion du Contact

#### 2.2.1 Gestion Technique des E-mails
**Décomposition Fonctionnelle**
- Configuration du serveur SMTP
- Intégration d'un gabarit (template) d'e-mail
- Surveillance du bon fonctionnement de la connexion SMTP
- Enregistrement des logs de transmission et des erreurs associées

#### 2.2.2 Gestion de l'Adresse de Destination (Admin)
**Décomposition Fonctionnelle**
- **Affichage de l'adresse e-mail de destination**
    - Accessible uniquement aux administrateurs via une interface sécurisée
- **Rafraîchissement dynamique de l'adresse**
    - Utilisation de `@RefreshScope` pour récupérer automatiquement la nouvelle adresse stockée dans les propriétés de l'application
- **Mise à jour des propriétés**
    - Modification manuelle du fichier de configuration
    - Application des changements en temps réel sans redémarrage du service

---

## 3. Sécurité et Protection des Données

### 3.1 Protection contre les attaques
- **Protection contre le spam et les robots** :
    - Implémentation d'un CAPTCHA (ex : Google reCAPTCHA)
    - Limitation du nombre de requêtes via un mécanisme de rate limiting (ex : Spring Rate Limiter)
- **Validation stricte des entrées utilisateur** :
    - Filtrage des données pour éviter les injections SQL/XSS
    - Vérification des formats et encodage des données sensibles
- **Authentification et autorisation** :
    - Restriction des accès aux services SMTP via des identifiants sécurisés
    - Utilisation de OAuth2 pour les services e-mail tiers (ex : Gmail, SendGrid)

### 3.2 Sécurisation des communications
- **Chiffrement des données en transit** :
    - Utilisation de TLS/SSL pour toutes les communications entre le frontend et le backend
    - Chiffrement des e-mails via S/MIME ou PGP si nécessaire
- **Protection des informations sensibles** :
    - Masquage des adresses e-mail dans les réponses JSON
    - Journalisation des erreurs sans exposer les détails des configurations SMTP

---

## 4. Validation et Tests Unitaires

### 4.1 Tests Unitaires - Frontend
| Composant | Méthode évaluée | Type de test |
|------------|----------------|---------------|
| Validation du nom | `validateName()` | Test de validation |
| Validation de l'e-mail | `validateEmail()` | Test de validation |
| Validation du message | `validateMessage()` | Test de validation |
| Envoi du formulaire | `submitForm()` | Test unitaire (Mock HttpClient) |
| Gestion des erreurs | `handleError()` | Test unitaire |

### 4.2 Tests Unitaires - Backend
| Module | Méthode testée | Type de test |
|---------|----------------|--------------|
| `EmailService` | `sendEmail()` | Test unitaire (Mock SMTP) |
| `ContactController` | `sendMessage()` | Test API (Mock EmailService) |
| `EmailValidator` | `isValidEmail()` | Test de validation |
| `AdminContactService` | `getContactEmail()` | Test unitaire |
| `AdminContactService` | `refreshContactEmail()` | Test d'intégration |

### 4.3 Tests d’Intégration
| Scénario | Type de test |
|----------|--------------|
| Transmission effective d'un e-mail via SMTP | Test d'intégration |
| Gestion d'une erreur SMTP (ex : authentification incorrecte) | Test d'intégration |
| Modification et rafraîchissement de l'adresse e-mail de destination | Test d'intégration |

---

## 5. Mécanismes de Suivi et de Résolution des Dysfonctionnements
- Surveillance des logs pour analyser les erreurs de transmission
- Signalement automatisé des anomalies critiques
- Réévaluation continue des cas de test pour inclure les scénarios de panne identifiés

Ce document vise à assurer une validation rigoureuse de l’envoi d’e-mails via le formulaire de contact, en couvrant à la fois les aspects fonctionnels, techniques et de sécurité du système.

