# Contact Service

## Description

Le **Contact Service** est un service simple d'envoi d'emails basé sur Spring Boot. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et permet aux visiteurs du site web d'envoyer des emails à l'organisation.

## Fonctionnalités

- **Envoi d'emails** : Service simple d'envoi d'emails depuis le site web
- **Validation des données** : Validation des champs email, nom et message
- **API REST** : Un endpoint REST pour l'envoi d'emails avec documentation Swagger
- **Intégration Eureka** : Enregistrement automatique auprès du service de découverte
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques
- **Support HATEOAS** : Navigation hypermedia pour les API REST

## Architecture

### Environnement de développement
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés

### Environnement de production
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `MAIL_HOST` | Serveur SMTP pour l'envoi d'emails | `smtp.gmail.com` |
| `MAIL_PORT` | Port du serveur SMTP | `587` |
| `MAIL_USERNAME` | Nom d'utilisateur SMTP | `contact@hoenheimsports.fr` |
| `MAIL_PASSWORD` | Mot de passe SMTP | `yourEmailPassword` |

### Ports

- **Port par défaut** : `8081`
- **Endpoints actuator** : `http://localhost:8081/actuator/*`
- **Documentation API** : `http://localhost:8081/swagger-ui.html`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- Accès au service Eureka
- Accès au Config Server
- Configuration SMTP pour l'envoi d'emails

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/contact-service

# Définir les variables d'environnement
export CONFIG_SERVER=http://localhost:8888
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=contact@hoenheimsports.fr
export MAIL_PASSWORD=yourEmailPassword

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8081:8081 \
  -e CONFIG_SERVER=http://config-service:8888 \
  -e EUREKA_SERVER_URI=http://eureka:8761/eureka \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  -e MAIL_HOST=smtp.gmail.com \
  -e MAIL_PORT=587 \
  -e MAIL_USERNAME=contact@hoenheimsports.fr \
  -e MAIL_PASSWORD=yourEmailPassword \
  --name contact-service \
  contact-service:latest
```

## Utilisation

### Endpoints principaux

```
POST /sendEmail - Envoyer un email
```

**Exemple d'envoi d'email :**
```bash
curl -X POST http://localhost:8081/sendEmail \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "message": "Bonjour, je souhaiterais avoir des informations sur vos activités."
  }'
```

### Documentation API

La documentation complète de l'API est disponible via Swagger UI :
- **URL** : `http://localhost:8081/swagger-ui.html`
- **Spécification OpenAPI** : `http://localhost:8081/v3/api-docs`

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels entre services.

## Structure du projet

```
contact-service/
├── src/main/java/fr/hoenheimsports/contactservice/
│   ├── ContactServiceApplication.java          # Application principale
│   ├── controller/                             # Contrôleurs REST
│   ├── dto/                                    # Objets de transfert de données
│   ├── service/                                # Services métier
│   ├── config/                                 # Configuration
│   └── exception/                              # Gestion des exceptions
├── src/main/resources/
│   ├── application.yml                         # Configuration de base
│   └── logback-spring.xml                      # Configuration logging
├── build.gradle.kts                            # Configuration Gradle
└── README.md                                   # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.4.4
- **Spring Boot Starter Mail** (envoi d'emails)
- **Spring Boot Starter Web** (API REST)
- **Spring Boot Starter HATEOAS** (hypermedia)
- **Spring Boot Starter Validation** (validation)
- **Spring Cloud Config Client** 2024.0.1
- **Spring Cloud Netflix Eureka Client** 2024.0.1
- **SpringDoc OpenAPI** 2.8.4 (documentation API)
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Validation des entrées** : Validation stricte des données de formulaire
- **Protection CSRF** : Protection contre les attaques CSRF
- **Sanitisation** : Nettoyage des données utilisateur avant traitement
- **Rate limiting** : Limitation du nombre de requêtes par IP (via configuration)

## Troubleshooting

### Problèmes courants

1. **Service non enregistré dans Eureka**
   - Vérifier la variable `EUREKA_SERVER_URI`
   - Vérifier que le service Eureka est démarré

2. **Erreur d'envoi d'email**
   - Vérifier la configuration SMTP (`MAIL_HOST`, `MAIL_PORT`)
   - Vérifier les identifiants (`MAIL_USERNAME`, `MAIL_PASSWORD`)
   - Vérifier la connectivité réseau vers le serveur SMTP

3. **Configuration non trouvée**
   - Vérifier la variable `CONFIG_SERVER`
   - Vérifier que le Config Server est démarré et accessible

### Logs utiles

```bash
# Voir les logs du service
docker logs contact-service

# Logs avec tracing
grep "traceId" logs/contact-service.log

# Logs d'envoi d'emails
grep "mail" logs/contact-service.log
```

## Contribution

Pour contribuer au développement du Contact Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Tester l'envoi d'emails en local
4. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.