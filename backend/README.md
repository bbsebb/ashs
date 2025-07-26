# Backend ASHS - Architecture Microservices

## Description

Le **Backend ASHS** est une architecture microservices complète pour l'Association Sportive de Hoenheim, développée avec Spring Boot et Spring Cloud. Cette architecture modulaire permet de gérer tous les aspects de l'association sportive : gestion des entraînements, communication, intégration sociale, et services d'infrastructure.

## Architecture générale

L'architecture suit les principes des microservices avec les composants suivants :

```
                    ┌─────────────────────────┐
                    │     Gateway Service     │
                    │     (Port 8080)         │
                    └─────────────┬───────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                        │
┌───────┴───────┐    ┌─────────┴─────────┐    ┌─────────┴─────────┐
│ Discovery     │    │ Config Service    │    │ Observability     │
│ Service       │    │ (Port 8888)       │    │ (Zipkin/Prometheus)│
│ (Port 8761)   │    │                   │    │                   │
└───────────────┘    └───────────────────┘    └───────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                        │
┌───────┴───────┐    ┌─────────┴─────────┐    ┌─────────┴─────────┐
│ Training      │    │ Contact Service   │    │ Facebook Service  │
│ Service       │    │ (Port 8081)       │    │ (Port 8083)       │
│ (Port 8082)   │    │                   │    │                   │
└───────────────┘    └───────────────────┘    └───────────────────┘
```

## Services

### Services d'infrastructure

#### 1. **Discovery Service** (Port 8761)
- **Rôle** : Serveur de découverte de services basé sur Netflix Eureka
- **Fonctionnalités** :
  - Enregistrement automatique de tous les microservices
  - Résolution des adresses des services par leur nom
  - Interface web de monitoring des services
  - Support haute disponibilité en cluster

#### 2. **Config Service** (Port 8888)
- **Rôle** : Service de configuration centralisée basé sur Spring Cloud Config
- **Fonctionnalités** :
  - Gestion centralisée de la configuration de tous les services
  - Support multi-environnements (dev/prod)
  - Chiffrement des données sensibles avec keystore PKCS12
  - Actualisation dynamique de la configuration

#### 3. **Gateway Service** (Port 8080)
- **Rôle** : Passerelle API basée sur Spring Cloud Gateway
- **Fonctionnalités** :
  - Point d'entrée unique pour tous les microservices
  - Routage intelligent et load balancing
  - Authentification JWT centralisée
  - Rate limiting et circuit breaker
  - Configuration CORS
  - Documentation API centralisée

### Services métier

#### 4. **Training Service** (Port 8082)
- **Rôle** : Gestion des entraînements et sessions sportives
- **Fonctionnalités** :
  - Gestion des équipes, coachs et salles
  - Planification des sessions d'entraînement
  - Gestion des créneaux horaires
  - Suivi des présences et statistiques
  - API REST sécurisée avec validation stricte

#### 5. **Contact Service** (Port 8081)
- **Rôle** : Service d'envoi d'emails pour le contact
- **Fonctionnalités** :
  - Envoi d'emails depuis le site web
  - Validation des données de formulaire
  - Protection contre le spam
  - API REST simple et sécurisée

#### 6. **Facebook Service** (Port 8083)
- **Rôle** : Intégration avec l'API Facebook
- **Fonctionnalités** :
  - Récupération des publications Facebook
  - Gestion des tokens d'accès
  - Cache intelligent avec Caffeine
  - Circuit breaker pour la résilience

## Technologies communes

### Framework principal
- **Spring Boot** 3.4.4+ - Framework principal
- **Spring Cloud** 2024.0.1+ - Outils microservices
- **Java** 24+ - Langage de programmation

### Persistance
- **PostgreSQL** 15+ - Base de données principale
- **Spring Data JPA** - ORM et repositories
- **Flyway** - Migrations de base de données

### Sécurité
- **Spring Security** - Framework de sécurité
- **OAuth2 Resource Server** - Authentification JWT
- **Keycloak** - Serveur d'authentification

### Communication
- **OpenFeign** - Clients HTTP déclaratifs
- **Spring HATEOAS** - APIs hypermedia
- **MapStruct** 1.6.3+ - Mapping d'objets

### Observabilité
- **Micrometer Tracing** - Tracing distribué
- **Zipkin** - Collecte et visualisation des traces
- **Prometheus** - Métriques et monitoring
- **Loki** - Centralisation des logs

### Documentation
- **SpringDoc OpenAPI** 2.8.4+ - Documentation API automatique
- **Swagger UI** - Interface de test des APIs

### Cache et résilience
- **Caffeine** - Cache en mémoire
- **Resilience4j** - Circuit breaker et retry

## Installation et démarrage

### Prérequis

- **Java 24+**
- **Gradle 8+**
- **PostgreSQL 15+**
- **Docker & Docker Compose** (recommandé)
- **Keycloak** (serveur OAuth2/OIDC)

### Démarrage rapide avec Docker Compose

```bash
# Cloner le projet
git clone <repository-url>
cd backend

# Démarrer l'infrastructure (PostgreSQL, Keycloak, Zipkin, etc.)
docker-compose -f docker-compose-support/docker-compose.yml up -d

# Démarrer les services dans l'ordre
./start-services.sh
```

### Démarrage manuel des services

#### 1. Démarrer les services d'infrastructure

```bash
# 1. Config Service (doit être démarré en premier)
cd config-service
export CONFIG_SERVICE_KEYSTORE_PASS=yourPassword
export EUREKA_SERVER_URI=http://localhost:8761/eureka
./gradlew bootRun --args='--spring.profiles.active=dev'

# 2. Discovery Service
cd ../discovery-service
export CONFIG_SERVER=http://localhost:8888
./gradlew bootRun --args='--spring.profiles.active=dev'

# 3. Gateway Service
cd ../gateway-service
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 2. Démarrer les services métier

```bash
# Training Service
cd training-service
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export DATABASE_URL=jdbc:postgresql://localhost:5432/training_db
export DATABASE_USERNAME=training_user
export DATABASE_PASSWORD=training_password
./gradlew bootRun --args='--spring.profiles.active=dev'

# Contact Service
cd ../contact-service
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export MAIL_HOST=smtp.gmail.com
export MAIL_USERNAME=contact@hoenheimsports.fr
export MAIL_PASSWORD=yourEmailPassword
./gradlew bootRun --args='--spring.profiles.active=dev'

# Facebook Service
cd ../facebook-service
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export DATABASE_URL=jdbc:postgresql://localhost:5432/facebook_db
export FACEBOOK_APP_ID=your_facebook_app_id
export FACEBOOK_APP_SECRET=your_facebook_app_secret
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Configuration

### Variables d'environnement communes

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI` | URI de l'émetteur JWT | `http://localhost:8080/realms/ashs` |

### Profils d'environnement

- **dev** : Environnement de développement
  - Tracing 100% des requêtes
  - Tous les endpoints actuator exposés
  - Configuration permissive
  - Base de données locale

- **prod** : Environnement de production
  - Tracing 10% des requêtes (échantillonnage)
  - Endpoints limités (health, info, prometheus)
  - Sécurité renforcée
  - Base de données en cluster

## Monitoring et observabilité

### Endpoints de santé

Chaque service expose les endpoints suivants :

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`

### Services de monitoring

- **Zipkin** : `http://localhost:9411` - Tracing distribué
- **Prometheus** : `http://localhost:9090` - Métriques
- **Grafana** : `http://localhost:3000` - Dashboards

### Eureka Dashboard

- **URL** : `http://localhost:8761`
- Visualisation de tous les services enregistrés
- État de santé des instances

## Documentation API

### Swagger UI centralisé

- **Gateway** : `http://localhost:8080/swagger-ui.html`
- Documentation de tous les services via le gateway

### Documentation par service

- **Training Service** : `http://localhost:8082/swagger-ui.html`
- **Contact Service** : `http://localhost:8081/swagger-ui.html`
- **Facebook Service** : `http://localhost:8083/swagger-ui.html`

## Sécurité

### Authentification

- **OAuth2/OIDC** avec Keycloak
- **JWT tokens** pour l'authentification
- **Rôles** : ADMIN, COACH, MEMBER

### Autorisation

- Contrôle d'accès basé sur les rôles
- Protection des endpoints sensibles
- Validation stricte des données

### Chiffrement

- Données sensibles chiffrées en base
- Configuration chiffrée avec keystore PKCS12
- Support HTTPS/TLS

## Base de données

### Schéma par service

- **training_db** : Base de données du Training Service
- **facebook_db** : Base de données du Facebook Service
- **config_db** : Base de données du Config Service (optionnel)

### Migrations

Chaque service gère ses propres migrations avec Flyway :
- Scripts dans `src/main/resources/db/migration/`
- Exécution automatique au démarrage

## Développement

### Structure des projets

Chaque service suit la même structure :

```
service-name/
├── src/main/java/fr/hoenheimsports/servicename/
│   ├── ServiceNameApplication.java
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── mapper/
│   ├── config/
│   └── exception/
├── src/main/resources/
│   ├── application.yml
│   ├── db/migration/
│   └── logback-spring.xml
├── build.gradle.kts
└── README.md
```

### Tests

```bash
# Tests unitaires
./gradlew test

# Tests d'intégration
./gradlew integrationTest

# Tests de tous les services
./gradlew build
```

### Build et déploiement

```bash
# Build d'un service
./gradlew bootBuildImage

# Build de tous les services
for service in config-service discovery-service gateway-service training-service contact-service facebook-service; do
  cd $service
  ./gradlew bootBuildImage
  cd ..
done
```

## Troubleshooting

### Problèmes courants

1. **Service non enregistré dans Eureka**
   - Vérifier `EUREKA_SERVER_URI`
   - Vérifier que Discovery Service est démarré
   - Vérifier les logs du service

2. **Configuration non trouvée**
   - Vérifier `CONFIG_SERVER`
   - Vérifier que Config Service est démarré
   - Vérifier les profils actifs

3. **Erreurs d'authentification**
   - Vérifier la configuration Keycloak
   - Vérifier `OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI`
   - Vérifier la validité des tokens JWT

4. **Problèmes de base de données**
   - Vérifier la connectivité PostgreSQL
   - Vérifier les credentials de base de données
   - Vérifier les migrations Flyway

### Logs utiles

```bash
# Logs avec tracing
grep "traceId" logs/*.log

# Logs d'erreur
grep "ERROR" logs/*.log

# Logs d'authentification
grep "auth" logs/*.log
```

### Commandes de diagnostic

```bash
# Vérifier l'état de tous les services
curl http://localhost:8761  # Eureka dashboard

# Vérifier les routes du gateway
curl http://localhost:8080/actuator/gateway/routes

# Vérifier la santé des services
for port in 8080 8081 8082 8083 8761 8888; do
  echo "Service on port $port:"
  curl -s http://localhost:$port/actuator/health | jq .status
done
```

## Performance et optimisation

### Recommandations JVM

```bash
# Variables d'environnement pour la production
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Configuration de cache

- **Facebook Service** : Cache des posts Facebook avec Caffeine
- **Config Service** : Cache des configurations

### Monitoring des performances

- Métriques JVM via Micrometer
- Métriques applicatives personnalisées
- Dashboards Grafana pour la visualisation

## Contribution

### Workflow de développement

1. Créer une branche feature depuis `main`
2. Développer et tester localement
3. Exécuter les tests unitaires et d'intégration
4. Vérifier la documentation API
5. Créer une pull request
6. Review et merge après validation

### Standards de code

- **Java** : Respect des conventions Oracle
- **Tests** : Couverture minimale de 80%
- **Documentation** : Javadoc pour les APIs publiques
- **Logs** : Utilisation de SLF4J avec des niveaux appropriés

### Environnements

- **Développement** : Local avec Docker Compose
- **Test** : Environnement d'intégration continue
- **Production** : Cluster Kubernetes avec haute disponibilité

## Licence

Ce projet fait partie du système ASHS (Association Sportive de Hoenheim) et est soumis aux conditions de licence du projet principal.

## Support

Pour toute question ou problème :

1. Consulter la documentation des services individuels
2. Vérifier les logs et métriques
3. Utiliser les outils de diagnostic fournis
4. Contacter l'équipe de développement

---

**Version** : 1.0.0  
**Dernière mise à jour** : 26 juillet 2025  
**Équipe** : ASHS Development Team