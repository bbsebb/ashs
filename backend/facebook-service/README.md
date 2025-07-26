# Facebook Service

## Description

Le **Facebook Service** est un service d'intégration avec l'API Facebook basé sur Spring Boot. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et permet de gérer les interactions avec Facebook, notamment la récupération de posts, la gestion des événements et la synchronisation des données sociales.

## Fonctionnalités

- **Récupération des feeds Facebook** : Récupération des publications Facebook via l'API Graph
- **Gestion des tokens d'accès** : Échange de tokens court-terme vers long-terme
- **Cache intelligent** : Mise en cache des données avec Caffeine pour optimiser les performances
- **API REST sécurisée** : Endpoints REST avec authentification OAuth2 et documentation Swagger
- **Persistance des données** : Stockage en base PostgreSQL avec migrations Flyway
- **Mapping automatique** : Conversion des objets avec MapStruct
- **Client Feign** : Communication avec l'API Facebook Graph via OpenFeign
- **Intégration Eureka** : Enregistrement automatique auprès du service de découverte
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques

## Architecture

### Environnement de développement
- **Configuration** : Récupérée depuis le Config Server
- **Base de données** : PostgreSQL avec Docker Compose
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés
- **Cache** : Configuration de développement avec TTL court

### Environnement de production
- **Configuration** : Récupérée depuis le Config Server
- **Base de données** : PostgreSQL en cluster
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées, authentification OAuth2
- **Cache** : Configuration optimisée pour la production

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `DATABASE_URL` | URL de la base de données PostgreSQL | `jdbc:postgresql://localhost:5432/facebook_db` |
| `DATABASE_USERNAME` | Nom d'utilisateur de la base de données | `facebook_user` |
| `DATABASE_PASSWORD` | Mot de passe de la base de données | `facebook_password` |
| `FACEBOOK_APP_ID` | ID de l'application Facebook | `your_facebook_app_id` |
| `FACEBOOK_APP_SECRET` | Secret de l'application Facebook | `your_facebook_app_secret` |
| `FACEBOOK_ACCESS_TOKEN` | Token d'accès Facebook | `your_facebook_access_token` |
| `OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI` | URI de l'émetteur JWT | `http://localhost:8080/realms/ashs` |

### Ports

- **Port par défaut** : `8083`
- **Endpoints actuator** : `http://localhost:8083/actuator/*`
- **Documentation API** : `http://localhost:8083/swagger-ui.html`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- PostgreSQL 15+
- Accès au service Eureka
- Accès au Config Server
- Application Facebook configurée
- Serveur OAuth2/OIDC (Keycloak)

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/facebook-service

# Démarrer PostgreSQL avec Docker Compose
docker-compose up -d postgres

# Définir les variables d'environnement
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
export DATABASE_URL=jdbc:postgresql://localhost:5432/facebook_db
export DATABASE_USERNAME=facebook_user
export DATABASE_PASSWORD=facebook_password
export FACEBOOK_APP_ID=your_facebook_app_id
export FACEBOOK_APP_SECRET=your_facebook_app_secret
export FACEBOOK_ACCESS_TOKEN=your_facebook_access_token
export OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://localhost:8080/realms/ashs

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8083:8083 \
  -e CONFIG_SERVER=http://config-service:8888 \
  -e EUREKA_SERVER_URI=http://eureka:8761/eureka \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  -e DATABASE_URL=jdbc:postgresql://postgres:5432/facebook_db \
  -e DATABASE_USERNAME=facebook_user \
  -e DATABASE_PASSWORD=facebook_password \
  -e FACEBOOK_APP_ID=your_facebook_app_id \
  -e FACEBOOK_APP_SECRET=your_facebook_app_secret \
  -e FACEBOOK_ACCESS_TOKEN=your_facebook_access_token \
  -e OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/ashs \
  --name facebook-service \
  facebook-service:latest
```

## Utilisation

### Endpoints principaux

```
GET / - Récupérer les feeds Facebook (paginé)
GET /all - Récupérer tous les feeds Facebook
POST /exchange - Échanger un token court-terme contre un token long-terme
```

**Exemple de récupération des feeds (paginé) :**
```bash
curl -X GET "http://localhost:8083/?page=0&size=10" \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json"
```

**Exemple d'échange de token :**
```bash
curl -X POST http://localhost:8083/exchange \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "your_short_lived_token"
  }'
```

### Documentation API

La documentation complète de l'API est disponible via Swagger UI :
- **URL** : `http://localhost:8083/swagger-ui.html`
- **Spécification OpenAPI** : `http://localhost:8083/v3/api-docs`

### Client Feign

Le service utilise OpenFeign pour communiquer avec l'API Facebook :

```java
@FeignClient(name = "facebook-api", url = "https://graph.facebook.com")
public interface FacebookClient {
    @GetMapping("/v18.0/{page-id}/posts")
    FacebookPostsResponse getPosts(@PathVariable("page-id") String pageId,
                                   @RequestParam("access_token") String accessToken);
}
```

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`
- **Circuit breaker** : `GET /actuator/circuitbreakers`

### Métriques importantes

- Nombre d'appels à l'API Facebook
- Taux de succès des requêtes
- Temps de réponse moyen
- État du circuit breaker
- Statistiques de cache (hit ratio)
- Connexions à la base de données

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels entre services et vers l'API Facebook.

## Structure du projet

```
facebook-service/
├── src/main/java/fr/hoenheimsports/facebookservice/
│   ├── FacebookServiceApplication.java         # Application principale
│   ├── controller/                             # Contrôleurs REST
│   ├── service/                                # Services métier
│   ├── repository/                             # Repositories JPA
│   ├── entity/                                 # Entités JPA
│   ├── dto/                                    # Objets de transfert de données
│   ├── mapper/                                 # Mappers MapStruct
│   ├── client/                                 # Clients Feign
│   ├── config/                                 # Configuration
│   └── exception/                              # Gestion des exceptions
├── src/main/resources/
│   ├── application.yml                         # Configuration de base
│   ├── db/migration/                           # Scripts Flyway
│   └── logback-spring.xml                      # Configuration logging
├── build.gradle.kts                            # Configuration Gradle
└── README.md                                   # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.5.1-SNAPSHOT
- **Spring Boot Starter Data JPA** (persistance)
- **Spring Boot Starter Web** (API REST)
- **Spring Boot Starter HATEOAS** (hypermedia)
- **Spring Boot Starter Security** (sécurité)
- **Spring Boot Starter OAuth2 Resource Server** (authentification)
- **Spring Cloud Config Client** 2025.0.0
- **Spring Cloud Netflix Eureka Client** 2025.0.0
- **Spring Cloud OpenFeign** (clients HTTP)
- **Spring Cloud Circuit Breaker Resilience4j** (circuit breaker)
- **PostgreSQL Driver** (base de données)
- **Flyway** (migrations de base de données)
- **Caffeine** 3.2.0 (cache)
- **MapStruct** 1.6.3 (mapping d'objets)
- **SpringDoc OpenAPI** 2.8.9 (documentation API)
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Authentification OAuth2** : Protection des endpoints avec JWT
- **Autorisation** : Contrôle d'accès basé sur les rôles
- **Validation des entrées** : Validation stricte des données
- **Protection CSRF** : Protection contre les attaques CSRF
- **Rate limiting** : Limitation du nombre de requêtes par utilisateur
- **Chiffrement** : Chiffrement des données sensibles en base

## Cache et performances

### Configuration du cache

```java
@Cacheable(value = "facebook-posts", key = "#pageId")
public List<FacebookPost> getCachedPosts(String pageId) {
    return facebookClient.getPosts(pageId);
}
```

### Stratégies de cache

- **TTL** : Expiration automatique des données
- **Eviction** : Éviction LRU des entrées anciennes
- **Refresh ahead** : Rafraîchissement proactif des données

## Circuit Breaker

### Configuration Resilience4j

```yaml
resilience4j:
  circuitbreaker:
    instances:
      facebook-api:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
```

### États du circuit breaker

- **CLOSED** : Fonctionnement normal
- **OPEN** : Circuit ouvert, requêtes rejetées
- **HALF_OPEN** : Test de récupération

## Troubleshooting

### Problèmes courants

1. **Erreur d'authentification Facebook**
   - Vérifier le token d'accès `FACEBOOK_ACCESS_TOKEN`
   - Vérifier les permissions de l'application Facebook
   - Vérifier la validité du token

2. **Circuit breaker ouvert**
   - Vérifier la connectivité vers l'API Facebook
   - Vérifier les logs d'erreur
   - Attendre la période de récupération

3. **Erreurs de base de données**
   - Vérifier la connectivité PostgreSQL
   - Vérifier les migrations Flyway
   - Vérifier les permissions de base de données

4. **Problèmes de cache**
   - Vérifier la configuration Caffeine
   - Monitorer les métriques de cache
   - Vider le cache si nécessaire

### Logs utiles

```bash
# Voir les logs du service
docker logs facebook-service

# Logs avec tracing
grep "traceId" logs/facebook-service.log

# Logs d'appels Facebook API
grep "facebook-api" logs/facebook-service.log

# Logs de circuit breaker
grep "CircuitBreaker" logs/facebook-service.log

# Logs de cache
grep "cache" logs/facebook-service.log
```

### Commandes de diagnostic

```bash
# Vérifier l'état du circuit breaker
curl http://localhost:8083/actuator/circuitbreakers

# Vérifier les métriques de cache
curl http://localhost:8083/actuator/caches

# Tester la connectivité Facebook
curl http://localhost:8083/actuator/health/facebook
```

## Contribution

Pour contribuer au développement du Facebook Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Tester avec l'API Facebook en sandbox
4. Vérifier les migrations de base de données
5. Tester les circuit breakers et le cache
6. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.