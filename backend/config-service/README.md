# Config Service

## Description

Le **Config Service** est un service de configuration centralisée basé sur Spring Cloud Config Server. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et permet de gérer la configuration de tous les services de manière centralisée et dynamique.

## Fonctionnalités

- **Configuration centralisée** : Gestion de la configuration de tous les microservices depuis un point central
- **Support multi-environnements** : Configurations spécifiques pour les environnements de développement et production
- **Chiffrement des données sensibles** : Utilisation d'un keystore PKCS12 pour chiffrer les propriétés sensibles
- **Intégration Eureka** : Enregistrement automatique auprès du service de découverte
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques
- **Actualisation dynamique** : Support de `@RefreshScope` et endpoint `/actuator/refresh`

## Architecture

### Environnement de développement
- **Source de configuration** : Système de fichiers local (native)
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés

### Environnement de production
- **Source de configuration** : Dépôt Git distant
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVICE_KEYSTORE_PASS` | Mot de passe du keystore pour le chiffrement | `mySecretPassword` |
| `EUREKA_SERVER_URI` | URI du serveur Eureka | `http://localhost:8761/eureka` |
| `CONFIG_SERVER_URI` | URI du dépôt de configuration | `file:///path/to/config` (dev) ou `https://github.com/user/config-repo.git` (prod) |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |

### Ports

- **Port par défaut** : `8888`
- **Endpoints actuator** : `http://localhost:8888/actuator/*`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- Accès au service Eureka
- Keystore configuré (`config-keystore.p12`)

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/config-service

# Définir les variables d'environnement
export CONFIG_SERVICE_KEYSTORE_PASS=yourKeystorePassword
export EUREKA_SERVER_URI=http://localhost:8761/eureka
export CONFIG_SERVER_URI=file:///path/to/your/config/directory
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8888:8888 \
  -e CONFIG_SERVICE_KEYSTORE_PASS=yourPassword \
  -e EUREKA_SERVER_URI=http://eureka:8761/eureka \
  -e CONFIG_SERVER_URI=https://github.com/user/config-repo.git \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  --name config-service \
  config-service:latest
```

## Utilisation

### Récupération de configuration

Les autres services peuvent récupérer leur configuration via les endpoints suivants :

```
GET /{application}/{profile}[/{label}]
GET /{application}-{profile}.yml
GET /{application}-{profile}.properties
GET /{label}/{application}-{profile}.yml
GET /{label}/{application}-{profile}.properties
```

**Exemples :**
```bash
# Configuration du service training en profil dev
curl http://localhost:8888/training-service/dev

# Configuration du service contact en profil prod
curl http://localhost:8888/contact-service/prod

# Configuration avec label spécifique (branche Git)
curl http://localhost:8888/main/gateway-service-prod.yml
```

### Chiffrement des propriétés

Pour chiffrer une propriété sensible :

```bash
# Chiffrer une valeur
curl -X POST http://localhost:8888/encrypt -d "mySecretValue"

# Utiliser la valeur chiffrée dans la configuration
password: '{cipher}AQA...'
```

### Actualisation de la configuration

```bash
# Actualiser la configuration d'un service client
curl -X POST http://service-client:port/actuator/refresh
```

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels entre services.

## Structure du projet

```
config-service/
├── src/main/java/fr/hoenheimsports/configservice/
│   └── ConfigServiceApplication.java          # Application principale
├── src/main/resources/
│   ├── application.yml                        # Configuration de base
│   ├── application-dev.yml                    # Configuration développement
│   ├── application-prod.yml                   # Configuration production
│   ├── config-keystore.p12                    # Keystore pour chiffrement
│   └── logback-spring.xml                     # Configuration logging
├── build.gradle.kts                           # Configuration Gradle
└── README.md                                  # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.4.4
- **Spring Cloud Config Server** 2024.0.1
- **Spring Cloud Netflix Eureka Client** 2024.0.1
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Chiffrement** : Les propriétés sensibles sont chiffrées avec un keystore PKCS12
- **SSL** : Support SSL/TLS pour les communications sécurisées
- **Validation** : Validation SSL désactivée en production pour les dépôts Git internes

## Troubleshooting

### Problèmes courants

1. **Service non enregistré dans Eureka**
   - Vérifier la variable `EUREKA_SERVER_URI`
   - Vérifier que le service Eureka est démarré

2. **Erreur de chiffrement**
   - Vérifier le mot de passe du keystore `CONFIG_SERVICE_KEYSTORE_PASS`
   - Vérifier la présence du fichier `config-keystore.p12`

3. **Configuration non trouvée**
   - Vérifier l'URI du dépôt de configuration `CONFIG_SERVER_URI`
   - Vérifier les permissions d'accès au dépôt Git (prod)

### Logs utiles

```bash
# Voir les logs du service
docker logs config-service

# Logs avec tracing
grep "traceId" logs/config-service.log
```

## Contribution

Pour contribuer au développement du Config Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Tester avec les profils dev et prod
4. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.