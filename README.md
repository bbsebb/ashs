# Documentation du Projet

## Introduction

Ce projet vise à fournir une solution complète de gestion pour un club sportif, en s'appuyant sur une architecture en microservices. Il comprend un site public pour la consultation des informations du club et un site administrateur permettant de gérer les entraîneurs, équipes, salles et créneaux d'entraînement. L'infrastructure repose sur des technologies modernes telles qu'Angular, Spring Boot, PostgreSQL et Keycloak, tout en intégrant des outils de monitoring et de sécurité avancés.

## 1. Vue d'ensemble de l'architecture

### Frontends

- **Site public (Angular sur Firebase Hosting)** :
  - Accessible à tous
  - Affiche les posts Instagram du club, ainsi que les informations sur les salles, entraîneurs, équipes et créneaux d'entraînement.
- **Site administrateur (Angular sur Firebase Hosting)** :
  - Restreint aux administrateurs via une authentification Keycloak
  - Permet de créer, modifier ou supprimer les entités liées aux salles, entraîneurs, équipes et créneaux.

### Backends (Spring Boot 3+)

Organisés en microservices indépendants :

- **Service Training** : Gestion des entraîneurs, salles, équipes, créneaux (PostgreSQL).
- **Service Contact** : Gestion des contacts du club et envoi de messages.
- **Service Instagram** : Récupération et exposition des posts Instagram.

### Infrastructure et services transverses

- **API Gateway** : Spring Gateway pour centraliser les requêtes et appliquer des règles (sécurité, logging, etc.).
- **Service de Découverte** : Eureka (Spring Cloud Netflix) pour l'enregistrement dynamique des services.
- **Service de Configuration** : Spring Cloud Config pour gérer la configuration via un repo GitHub.
- **Sécurité et Authentification** : Keycloak pour la gestion des accès.
- **Monitoring et Traçabilité** : Grafana, Prometheus, Loki, Zipkin.

---

## 2. Services fonctionnels

### 2.1 Service Training

- **Gestion des entités** : salles, entraîneurs, équipes, créneaux.
- **Base de données** : PostgreSQL.
- **Endpoints REST** :

| Méthode | Chemin                | Description           | Authentification | Accès UI |
| ------- | --------------------- | --------------------- | ---------------- | -------- |
| GET     | /training/salles      | Liste des salles      | Non              | Public   |
| GET     | /training/entraineurs | Liste des entraîneurs | Non              | Public   |
| POST    | /admin/training/salle | Créer une salle       | Oui (Keycloak)   | Admin    |

---

### 2.2 Service Contact

| Méthode | Chemin           | Description        | Authentification | Accès UI |
| ------- | ---------------- | ------------------ | ---------------- | -------- |
| GET     | /contact         | Infos de contact   | Non              | Public   |
| POST    | /contact/message | Envoyer un message | Non              | Public   |
| PUT     | /admin/contact   | Modifier les infos | Oui (Keycloak)   | Admin    |

---

### 2.3 Service Instagram

- **Connexion à l'API Instagram** pour récupérer les posts.
- **Endpoints REST** :

| Méthode | Chemin           | Description     | Authentification | Accès UI |
| ------- | ---------------- | --------------- | ---------------- | -------- |
| GET     | /instagram/posts | Liste des posts | Non              | Public   |

---

## 3. Infrastructure et services transverses

### 3.1 API Gateway

- Route les requêtes vers les services.
- Vérifie les tokens JWT Keycloak.
- Implémente un "circuit breaker" via Resilience4j.

### 3.2 Service de Découverte (Eureka)

- Permet aux microservices de s'enregistrer et de se découvrir.
- Implémentation : Spring Cloud Netflix Eureka.

### 3.3 Service de Configuration (Spring Cloud Config)

- Centralisation de la config via un repo GitHub.
- Configuration dynamique via @RefreshScope et /actuator/refresh.

### 3.4 Sécurité avec Keycloak

- Authentification JWT sur le front admin.
- Restrictions d'accès aux endpoints via @PreAuthorize.

### 3.5 Monitoring, Logging et Tracing

- **Monitoring** : Prometheus, Grafana.
- **Logging centralisé** : Grafana Loki.
- **Tracing distribué** : Zipkin.

---

## 4. Déploiement et Infrastructure CI/CD

### 4.1 Conteneurisation

- **Docker** : Microservices conteneurisés.
- **Orchestration** : Docker Compose ou Kubernetes.

### 4.2 CI/CD

- GitHub Actions/Travis CI pour la construction des images Docker.
- Déploiement automatique en staging/production.

### 4.3 Nettoyage Manuel

Un workflow GitHub Actions a été créé pour faciliter le nettoyage complet de l'environnement:

- **Workflow**: `manual-cleanup.yml`
- **Déclenchement**: Manuel uniquement (via l'interface GitHub Actions)
- **Fonctionnalités**:
  - Arrêt de tous les services Docker Compose
  - Suppression de toutes les images Docker
  - Suppression du répertoire ashs et de tout son contenu

**Comment utiliser le workflow de nettoyage**:
1. Accédez à l'onglet "Actions" du dépôt GitHub
2. Sélectionnez le workflow "Manual Cleanup" dans la liste
3. Cliquez sur "Run workflow"
4. Tapez "YES" dans le champ de confirmation pour confirmer l'opération
5. Cliquez sur "Run workflow" pour lancer le processus

⚠️ **Attention**: Cette opération est irréversible et supprimera toutes les données non sauvegardées.

---

## 5. Exemple de Flux de Requête

### Affichage des posts Instagram

1. L'utilisateur envoie une requête via l'API Gateway sur `/instagram/posts`.
2. La Gateway route la requête vers le service Instagram.
3. Le service récupère les posts (via API Instagram ou cache).
4. La réponse est affichée sur le site public.

### Modification d'un créneau par un admin

1. L'administrateur se connecte (authentification Keycloak).
2. Un token JWT est joint à la requête.
3. Une requête `/admin/training/creneaux/{id}` est envoyée via l'API Gateway.
4. La Gateway vérifie le token et redirige vers le service Training.
5. La modification est enregistrée dans PostgreSQL.
6. Confirmation envoyée au front admin.

---

## Conclusion

Ce projet repose sur une architecture moderne et modulaire, permettant une gestion optimisée des différents services d'un club sportif, avec une sécurité robuste et un monitoring avancé.

