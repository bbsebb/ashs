# Gateway Service

## Description

Le **Gateway Service** est une passerelle API basée sur Spring Cloud Gateway. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et sert de point d'entrée unique pour tous les appels vers les microservices backend. Il gère le routage et la découverte automatique des services.

## Fonctionnalités

- **Passerelle API** : Point d'entrée unique pour tous les microservices
- **Routage** : Routage des requêtes vers les microservices appropriés
- **Load balancing** : Répartition de charge automatique entre les instances de services
- **CORS** : Configuration CORS pour les applications web
- **Configuration centralisée** : Configuration via le Config Server
- **Documentation API** : Documentation Swagger centralisée
- **Intégration Eureka** : Découverte automatique des services via Eureka
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques

## Architecture

### Environnement de développement
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés
- **CORS** : Configuration permissive pour le développement

### Environnement de production
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées, CORS restrictif
- **Rate limiting** : Limites strictes par IP et utilisateur

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI` | URI de l'émetteur JWT | `http://localhost:8080/realms/ashs` |
| `CORS_ALLOWED_ORIGINS` | Origines autorisées pour CORS | `http://localhost:3000,http://localhost:4200` |
| `RATE_LIMIT_REQUESTS_PER_SECOND` | Limite de requêtes par seconde | `10` |
| `RATE_LIMIT_BURST_CAPACITY` | Capacité de rafale | `20` |

### Ports

- **Port par défaut** : `8080`
- **Endpoints actuator** : `http://localhost:8080/actuator/*`
- **Documentation API** : `http://localhost:8080/swagger-ui.html`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- Accès au service Eureka
- Accès au Config Server
- Serveur OAuth2/OIDC (Keycloak) pour l'authentification

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/gateway-service

# Définir les variables d'environnement
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
export OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://localhost:8080/realms/ashs
export CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
export RATE_LIMIT_REQUESTS_PER_SECOND=10
export RATE_LIMIT_BURST_CAPACITY=20

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8080:8080 \
  -e CONFIG_SERVER=http://config-service:8888 \
  -e EUREKA_SERVER_URI=http://eureka:8761/eureka \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  -e OAUTH2_RESOURCE_SERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/ashs \
  -e CORS_ALLOWED_ORIGINS=http://frontend:3000 \
  -e RATE_LIMIT_REQUESTS_PER_SECOND=10 \
  -e RATE_LIMIT_BURST_CAPACITY=20 \
  --name gateway-service \
  gateway-service:latest
```

## Utilisation

### Routage des services

Le gateway route automatiquement les requêtes vers les services appropriés :

```
# Exemples de routage
GET /api/contact/**     -> contact-service
GET /api/training/**    -> training-service
GET /api/facebook/**    -> facebook-service
```

### Authentification

Les endpoints protégés nécessitent un token JWT valide :

```bash
# Exemple d'appel authentifié
curl -X GET http://localhost:8080/api/training/sessions \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json"
```

### Documentation API centralisée

La documentation Swagger de tous les services est accessible via :
- **URL** : `http://localhost:8080/swagger-ui.html`
- **Spécification OpenAPI** : `http://localhost:8080/v3/api-docs`

### Configuration des routes

```yaml
# Exemple de configuration de route
spring:
  cloud:
    gateway:
      routes:
        - id: contact-service
          uri: lb://contact-service
          predicates:
            - Path=/api/contact/**
          filters:
            - StripPrefix=2
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.requests-per-second: 10
                redis-rate-limiter.burst-capacity: 20
```

## Filtres disponibles

### Filtres globaux

- **Authentication Filter** : Validation des tokens JWT
- **CORS Filter** : Gestion des politiques CORS
- **Logging Filter** : Journalisation des requêtes/réponses
- **Tracing Filter** : Ajout des headers de tracing

### Filtres par route

- **Rate Limiting** : Limitation du débit par route
- **Circuit Breaker** : Protection contre les pannes
- **Retry** : Nouvelle tentative en cas d'échec
- **Request/Response Transformation** : Modification des requêtes/réponses

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`
- **Routes** : `GET /actuator/gateway/routes`
- **Filtres** : `GET /actuator/gateway/filters`

### Métriques importantes

- Nombre de requêtes par service
- Temps de réponse par route
- Taux d'erreur par service
- Utilisation du rate limiting
- État des circuit breakers

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels à travers tous les services.

## Structure du projet

```
gateway-service/
├── src/main/java/fr/hoenheimsports/gatewayservice/
│   ├── GatewayServiceApplication.java          # Application principale
│   ├── config/                                 # Configuration
│   │   ├── GatewayConfig.java                  # Configuration des routes
│   │   ├── CorsConfig.java                     # Configuration CORS
│   │   └── SecurityConfig.java                 # Configuration sécurité
│   ├── filter/                                 # Filtres personnalisés
│   │   ├── AuthenticationFilter.java           # Filtre d'authentification
│   │   ├── LoggingFilter.java                  # Filtre de logging
│   │   └── RateLimitingFilter.java             # Filtre de rate limiting
│   └── exception/                              # Gestion des exceptions
├── src/main/resources/
│   ├── application.yml                         # Configuration de base
│   └── logback-spring.xml                      # Configuration logging
├── build.gradle.kts                            # Configuration Gradle
└── README.md                                   # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.4.4
- **Spring Cloud Gateway** 2024.0.1
- **Spring Boot Starter WebFlux** (programmation réactive)
- **Spring Cloud Config Client** 2024.0.1
- **Spring Cloud Netflix Eureka Client** 2024.0.1
- **Spring HATEOAS** (hypermedia)
- **SpringDoc OpenAPI WebFlux** 2.8.6 (documentation API)
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Authentification JWT** : Validation des tokens sur les routes protégées
- **CORS** : Configuration des politiques CORS
- **Rate limiting** : Protection contre les attaques DDoS
- **Headers de sécurité** : Ajout automatique des headers de sécurité
- **Validation des entrées** : Validation des paramètres de requête
- **Chiffrement** : Support HTTPS/TLS

## Rate Limiting

### Configuration Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  cloud:
    gateway:
      filter:
        request-rate-limiter:
          redis-rate-limiter:
            requests-per-second: 10
            burst-capacity: 20
```

### Stratégies de limitation

- **Par IP** : Limitation par adresse IP
- **Par utilisateur** : Limitation par utilisateur authentifié
- **Par route** : Limitation spécifique par service
- **Globale** : Limitation générale du gateway

## Circuit Breaker

### Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      default:
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

1. **Service non accessible via le gateway**
   - Vérifier l'enregistrement du service dans Eureka
   - Vérifier la configuration des routes
   - Vérifier les logs du gateway

2. **Erreurs d'authentification**
   - Vérifier la configuration OAuth2
   - Vérifier la validité du token JWT
   - Vérifier la connectivité vers Keycloak

3. **Problèmes de CORS**
   - Vérifier la configuration CORS
   - Vérifier les origines autorisées
   - Vérifier les headers de requête

4. **Rate limiting trop restrictif**
   - Ajuster les limites de débit
   - Vérifier la configuration Redis
   - Monitorer les métriques de rate limiting

### Logs utiles

```bash
# Voir les logs du service
docker logs gateway-service

# Logs avec tracing
grep "traceId" logs/gateway-service.log

# Logs de routage
grep "route" logs/gateway-service.log

# Logs d'authentification
grep "auth" logs/gateway-service.log

# Logs de rate limiting
grep "rate" logs/gateway-service.log
```

### Commandes de diagnostic

```bash
# Vérifier les routes configurées
curl http://localhost:8080/actuator/gateway/routes

# Vérifier les filtres disponibles
curl http://localhost:8080/actuator/gateway/filters

# Tester une route spécifique
curl -v http://localhost:8080/api/contact/health

# Vérifier l'état des circuit breakers
curl http://localhost:8080/actuator/circuitbreakers
```

## Performance et optimisation

### Tuning JVM

```bash
# Variables d'environnement pour la production
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
```

### Configuration WebFlux

```yaml
spring:
  webflux:
    multipart:
      max-in-memory-size: 1MB
      max-disk-usage-per-part: 10MB
```

### Optimisation des connexions

- **Connection pooling** : Pool de connexions vers les services
- **Keep-alive** : Réutilisation des connexions HTTP
- **Timeout configuration** : Configuration des timeouts appropriés

## Contribution

Pour contribuer au développement du Gateway Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Tester le routage vers tous les services
4. Vérifier l'authentification et l'autorisation
5. Tester les filtres et le rate limiting
6. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.