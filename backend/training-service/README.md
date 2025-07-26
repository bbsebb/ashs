# Training Service

## Description

Le **Training Service** est un service de gestion des entraînements et des sessions sportives basé sur Spring Boot. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et permet de gérer les plannings d'entraînement, les inscriptions, les présences et les statistiques des activités sportives.

## Fonctionnalités

- **Gestion des entraînements** : Création, modification et suppression des sessions d'entraînement
- **Planification** : Gestion des créneaux horaires et des récurrences
- **Inscriptions** : Gestion des inscriptions des membres aux sessions
- **Présences** : Suivi des présences et absences
- **Statistiques** : Génération de rapports et statistiques d'activité
- **API REST sécurisée** : Endpoints REST avec authentification OAuth2 et documentation Swagger
- **Persistance des données** : Stockage en base PostgreSQL avec migrations Flyway
- **Mapping automatique** : Conversion des objets avec MapStruct
- **Validation des données** : Validation stricte avec Spring Validation
- **Support HATEOAS** : Navigation hypermedia pour les API REST
- **Intégration Eureka** : Enregistrement automatique auprès du service de découverte
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques

## Architecture

### Environnement de développement
- **Configuration** : Récupérée depuis le Config Server
- **Base de données** : PostgreSQL avec Docker Compose
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés
- **Sécurité** : Configuration de développement avec authentification simplifiée

### Environnement de production
- **Configuration** : Récupérée depuis le Config Server
- **Base de données** : PostgreSQL en cluster avec réplication
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées, authentification OAuth2 stricte

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `DATABASE_URL` | URL de la base de données PostgreSQL | `jdbc:postgresql://localhost:5432/training_db` |
| `DATABASE_USERNAME` | Nom d'utilisateur de la base de données | `training_user` |
| `DATABASE_PASSWORD` | Mot de passe de la base de données | `training_password` |
| `OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI` | URI de l'émetteur JWT | `http://localhost:8080/realms/ashs` |
| `TRAINING_DEFAULT_DURATION` | Durée par défaut des entraînements (minutes) | `90` |
| `TRAINING_MAX_PARTICIPANTS` | Nombre maximum de participants par session | `25` |

### Ports

- **Port par défaut** : `8082`
- **Endpoints actuator** : `http://localhost:8082/actuator/*`
- **Documentation API** : `http://localhost:8082/swagger-ui.html`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- PostgreSQL 15+
- Accès au service Eureka
- Accès au Config Server
- Serveur OAuth2/OIDC (Keycloak)

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/training-service

# Démarrer PostgreSQL avec Docker Compose
docker-compose up -d postgres

# Définir les variables d'environnement
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
export DATABASE_URL=jdbc:postgresql://localhost:5432/training_db
export DATABASE_USERNAME=training_user
export DATABASE_PASSWORD=training_password
export OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://localhost:8080/realms/ashs
export TRAINING_DEFAULT_DURATION=90
export TRAINING_MAX_PARTICIPANTS=25

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8082:8082 \
  -e CONFIG_SERVER=http://config-service:8888 \
  -e EUREKA_SERVER_URI=http://eureka:8761/eureka \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  -e DATABASE_URL=jdbc:postgresql://postgres:5432/training_db \
  -e DATABASE_USERNAME=training_user \
  -e DATABASE_PASSWORD=training_password \
  -e OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/ashs \
  -e TRAINING_DEFAULT_DURATION=90 \
  -e TRAINING_MAX_PARTICIPANTS=25 \
  --name training-service \
  training-service:latest
```

## Utilisation

### Endpoints principaux

```
# Gestion des équipes (Teams)
GET /teams - Lister les équipes (paginé)
GET /teams/all - Lister toutes les équipes
POST /teams - Créer une nouvelle équipe (ADMIN)
GET /teams/{id} - Détails d'une équipe
PUT /teams/{id} - Modifier une équipe (ADMIN)
DELETE /teams/{id} - Supprimer une équipe (ADMIN)
POST /teams/{teamId}/training-sessions - Ajouter une session d'entraînement à une équipe (ADMIN)
POST /teams/{teamId}/coach - Ajouter un coach à une équipe (ADMIN)

# Gestion des sessions d'entraînement (Training Sessions)
GET /training-sessions - Lister les sessions (paginé)
GET /training-sessions/all - Lister toutes les sessions
POST /training-sessions - Créer une nouvelle session (ADMIN)
GET /training-sessions/{id} - Détails d'une session
PUT /training-sessions/{id} - Modifier une session (ADMIN)
DELETE /training-sessions/{id} - Supprimer une session (ADMIN)

# Gestion des coachs (Coaches)
GET /coaches - Lister les coachs (paginé)
GET /coaches/all - Lister tous les coachs
POST /coaches - Créer un nouveau coach (ADMIN)
GET /coaches/{id} - Détails d'un coach
PUT /coaches/{id} - Modifier un coach (ADMIN)
DELETE /coaches/{id} - Supprimer un coach (ADMIN)

# Gestion des salles (Halls)
GET /halls - Lister les salles (paginé)
GET /halls/all - Lister toutes les salles
POST /halls - Créer une nouvelle salle (ADMIN)
GET /halls/{id} - Détails d'une salle
PUT /halls/{id} - Modifier une salle (ADMIN)
DELETE /halls/{id} - Supprimer une salle (ADMIN)

# Gestion des rôles des coachs (Role Coach)
GET /role-coaches - Lister les rôles des coachs (paginé)
GET /role-coaches/all - Lister tous les rôles des coachs
POST /role-coaches - Créer un nouveau rôle de coach (ADMIN)
GET /role-coaches/{id} - Détails d'un rôle de coach
PUT /role-coaches/{id} - Modifier un rôle de coach (ADMIN)
DELETE /role-coaches/{id} - Supprimer un rôle de coach (ADMIN)
```

**Exemple de création d'équipe :**
```bash
curl -X POST http://localhost:8082/teams \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "MALE",
    "category": "SENIOR",
    "teamNumber": 1
  }'
```

**Exemple de création de session d'entraînement :**
```bash
curl -X POST http://localhost:8082/training-sessions \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": "MONDAY",
    "timeSlot": {
      "startTime": "18:00",
      "endTime": "19:30"
    },
    "hallId": 1,
    "teamId": 1
  }'
```

### Documentation API

La documentation complète de l'API est disponible via Swagger UI :
- **URL** : `http://localhost:8082/swagger-ui.html`
- **Spécification OpenAPI** : `http://localhost:8082/v3/api-docs`

### Modèle de données

#### Session d'entraînement
```json
{
  "id": 123,
  "title": "Entraînement Football Senior",
  "description": "Entraînement technique et physique",
  "startTime": "2024-07-26T18:00:00",
  "endTime": "2024-07-26T19:30:00",
  "duration": 90,
  "maxParticipants": 25,
  "currentParticipants": 18,
  "location": "Terrain principal",
  "category": "FOOTBALL",
  "level": "SENIOR",
  "status": "SCHEDULED",
  "createdAt": "2024-07-20T10:00:00",
  "updatedAt": "2024-07-25T15:30:00"
}
```

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`
- **Base de données** : `GET /actuator/health/db`

### Métriques importantes

- Nombre de sessions créées par jour
- Taux d'occupation des sessions
- Nombre d'inscriptions/désinscriptions
- Taux de présence moyen
- Temps de réponse des API
- Connexions à la base de données

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels entre services.

## Structure du projet

```
training-service/
├── src/main/java/fr/hoenheimsports/trainingservice/
│   ├── TrainingServiceApplication.java         # Application principale
│   ├── controller/                             # Contrôleurs REST
│   │   ├── TrainingSessionController.java      # Gestion des sessions
│   │   ├── RegistrationController.java         # Gestion des inscriptions
│   │   ├── AttendanceController.java           # Gestion des présences
│   │   └── StatisticsController.java           # Statistiques
│   ├── service/                                # Services métier
│   │   ├── TrainingSessionService.java         # Logique des sessions
│   │   ├── RegistrationService.java            # Logique des inscriptions
│   │   ├── AttendanceService.java              # Logique des présences
│   │   └── StatisticsService.java              # Logique des statistiques
│   ├── repository/                             # Repositories JPA
│   │   ├── TrainingSessionRepository.java      # Repository des sessions
│   │   ├── RegistrationRepository.java         # Repository des inscriptions
│   │   └── AttendanceRepository.java           # Repository des présences
│   ├── entity/                                 # Entités JPA
│   │   ├── TrainingSession.java                # Entité session
│   │   ├── Registration.java                   # Entité inscription
│   │   └── Attendance.java                     # Entité présence
│   ├── dto/                                    # Objets de transfert de données
│   ├── mapper/                                 # Mappers MapStruct
│   ├── config/                                 # Configuration
│   │   ├── SecurityConfig.java                 # Configuration sécurité
│   │   └── JpaConfig.java                      # Configuration JPA
│   └── exception/                              # Gestion des exceptions
├── src/main/resources/
│   ├── application.yml                         # Configuration de base
│   ├── db/migration/                           # Scripts Flyway
│   │   ├── V1__Create_training_sessions.sql    # Création des tables
│   │   ├── V2__Create_registrations.sql        # Table des inscriptions
│   │   └── V3__Create_attendance.sql           # Table des présences
│   └── logback-spring.xml                      # Configuration logging
├── build.gradle.kts                            # Configuration Gradle
└── README.md                                   # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.4.4
- **Spring Boot Starter Data JPA** (persistance)
- **Spring Boot Starter Web** (API REST)
- **Spring Boot Starter HATEOAS** (hypermedia)
- **Spring Boot Starter Security** (sécurité)
- **Spring Boot Starter OAuth2 Resource Server** (authentification)
- **Spring Boot Starter Validation** (validation)
- **Spring Cloud Config Client** 2024.0.1
- **Spring Cloud Netflix Eureka Client** 2024.0.1
- **PostgreSQL Driver** (base de données)
- **Flyway** (migrations de base de données)
- **MapStruct** 1.6.3 (mapping d'objets)
- **SpringDoc OpenAPI** 2.8.4 (documentation API)
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Authentification OAuth2** : Protection des endpoints avec JWT
- **Autorisation** : Contrôle d'accès basé sur les rôles (ADMIN, COACH, MEMBER)
- **Validation des entrées** : Validation stricte des données avec Bean Validation
- **Protection CSRF** : Protection contre les attaques CSRF
- **Audit** : Traçabilité des modifications avec Spring Data JPA Auditing
- **Chiffrement** : Chiffrement des données sensibles en base

### Rôles et permissions

- **ADMIN** : Accès complet à toutes les fonctionnalités
- **COACH** : Gestion des sessions et consultation des statistiques
- **MEMBER** : Inscription/désinscription aux sessions, consultation de ses données

## Base de données

### Entités principales

Le service gère les entités suivantes :
- **Team** : Équipes sportives avec genre, catégorie et numéro
- **Coach** : Entraîneurs avec informations personnelles
- **Hall** : Salles d'entraînement avec adresse
- **TrainingSession** : Sessions d'entraînement avec créneaux horaires
- **RoleCoach** : Rôles des entraîneurs dans les équipes
- **TimeSlot** : Créneaux horaires (heure de début/fin)
- **Address** : Adresses des salles

### Migrations Flyway

Les migrations sont gérées automatiquement au démarrage du service avec les scripts SQL dans `src/main/resources/db/migration/`.

## Validation et règles métier

### Règles de validation

- **Session** : Date de début dans le futur, durée entre 30 et 180 minutes
- **Inscription** : Limite du nombre de participants, pas de double inscription
- **Présence** : Marquage possible uniquement le jour de la session

### Contraintes métier

```java
@Entity
@Table(name = "training_sessions")
public class TrainingSession {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;
    
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime startTime;
    
    @Min(value = 30, message = "La durée minimum est de 30 minutes")
    @Max(value = 180, message = "La durée maximum est de 180 minutes")
    private Integer duration;
    
    @Min(value = 1, message = "Au moins 1 participant requis")
    @Max(value = 50, message = "Maximum 50 participants autorisés")
    private Integer maxParticipants;
}
```

## Troubleshooting

### Problèmes courants

1. **Erreur de connexion à la base de données**
   - Vérifier la connectivité PostgreSQL
   - Vérifier les credentials de base de données
   - Vérifier les migrations Flyway

2. **Erreur d'authentification**
   - Vérifier la configuration OAuth2
   - Vérifier la validité du token JWT
   - Vérifier les rôles utilisateur

3. **Problème d'inscription**
   - Vérifier le nombre maximum de participants
   - Vérifier que la session n'est pas complète
   - Vérifier que l'utilisateur n'est pas déjà inscrit

4. **Erreur de validation**
   - Vérifier les contraintes de validation
   - Vérifier le format des données envoyées
   - Consulter les messages d'erreur détaillés

### Logs utiles

```bash
# Voir les logs du service
docker logs training-service

# Logs avec tracing
grep "traceId" logs/training-service.log

# Logs de base de données
grep "SQL" logs/training-service.log

# Logs d'authentification
grep "auth" logs/training-service.log

# Logs de validation
grep "validation" logs/training-service.log
```

### Commandes de diagnostic

```bash
# Vérifier l'état de la base de données
curl http://localhost:8082/actuator/health/db

# Vérifier les métriques JPA
curl http://localhost:8082/actuator/metrics/jpa.repositories.query.time

# Tester l'authentification
curl -H "Authorization: Bearer invalid_token" http://localhost:8082/api/training/sessions
```

## Performance et optimisation

### Optimisation des requêtes

```java
// Utilisation de projections pour limiter les données
@Query("SELECT new TrainingSessionSummary(t.id, t.title, t.startTime) FROM TrainingSession t")
List<TrainingSessionSummary> findAllSummaries();

// Pagination pour les grandes listes
Page<TrainingSession> findByCategory(String category, Pageable pageable);
```

### Cache des données

```java
@Cacheable(value = "training-stats", key = "#memberId")
public MemberStatistics getMemberStatistics(String memberId) {
    // Calcul coûteux des statistiques
}
```

### Optimisation des performances

Le service utilise :
- **Pagination** : Toutes les listes sont paginées pour optimiser les performances
- **HATEOAS** : Navigation hypermedia pour une meilleure découvrabilité de l'API
- **MapStruct** : Mapping efficace entre entités et DTOs
- **Validation** : Validation côté serveur avec annotations Bean Validation

## Contribution

Pour contribuer au développement du Training Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Ajouter les tests unitaires et d'intégration
4. Vérifier les migrations de base de données
5. Tester les règles de validation et de sécurité
6. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.