# Discovery Service

## Description

Le **Discovery Service** est un serveur de découverte de services basé sur Netflix Eureka Server. Il fait partie de l'architecture microservices du projet ASHS (Association Sportive de Hoenheim) et permet aux services de s'enregistrer automatiquement et de se découvrir mutuellement pour faciliter la communication inter-services.

## Fonctionnalités

- **Serveur Eureka** : Serveur de découverte de services Netflix Eureka
- **Registre de services** : Enregistrement automatique de tous les microservices
- **Découverte de services** : Résolution des adresses des services par leur nom
- **Interface web** : Console d'administration Eureka pour visualiser les services enregistrés
- **Intégration Config Server** : Configuration centralisée via le Config Server
- **Monitoring et tracing** : Intégration avec Zipkin pour le tracing distribué et Prometheus pour les métriques

## Architecture

### Environnement de développement
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 100% des requêtes tracées
- **Endpoints** : Tous les endpoints actuator exposés
- **Interface web** : Accessible pour le debugging

### Environnement de production
- **Configuration** : Récupérée depuis le Config Server
- **Tracing** : 10% des requêtes tracées (échantillonnage)
- **Endpoints** : Endpoints limités (health, info, prometheus)
- **Sécurité** : Stacktraces masquées
- **Haute disponibilité** : Déploiement en cluster recommandé

## Configuration

### Variables d'environnement requises

| Variable | Description | Exemple |
|----------|-------------|---------|
| `CONFIG_SERVER` | URI du serveur de configuration | `http://localhost:8888` |
| `ZIPKIN_TRACING_ENDPOINT` | Endpoint Zipkin pour le tracing | `http://localhost:9411/api/v2/spans` |
| `EUREKA_INSTANCE_HOSTNAME` | Nom d'hôte de l'instance Eureka | `localhost` (dev) ou `eureka-server` (prod) |
| `EUREKA_CLIENT_REGISTER_WITH_EUREKA` | Enregistrement avec Eureka | `false` (serveur standalone) |
| `EUREKA_CLIENT_FETCH_REGISTRY` | Récupération du registre | `false` (serveur standalone) |

### Ports

- **Port par défaut** : `8761`
- **Interface web** : `http://localhost:8761`
- **Endpoints actuator** : `http://localhost:8761/actuator/*`

## Installation et démarrage

### Prérequis

- Java 24+
- Gradle 8+
- Accès au Config Server

### Démarrage local

```bash
# Cloner le projet
git clone <repository-url>
cd backend/discovery-service

# Définir les variables d'environnement
export CONFIG_SERVER=http://localhost:8888
export ZIPKIN_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
export EUREKA_INSTANCE_HOSTNAME=localhost
export EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
export EUREKA_CLIENT_FETCH_REGISTRY=false

# Démarrer le service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Démarrage avec Docker

```bash
# Construire l'image
./gradlew bootBuildImage

# Démarrer le conteneur
docker run -p 8761:8761 \
  -e CONFIG_SERVER=http://config-service:8888 \
  -e ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans \
  -e EUREKA_INSTANCE_HOSTNAME=eureka-server \
  -e EUREKA_CLIENT_REGISTER_WITH_EUREKA=false \
  -e EUREKA_CLIENT_FETCH_REGISTRY=false \
  --name discovery-service \
  discovery-service:latest
```

## Utilisation

### Interface web

L'interface web d'Eureka est accessible à l'adresse :
- **URL** : `http://localhost:8761`
- **Fonctionnalités** :
  - Visualisation des services enregistrés
  - État de santé des instances
  - Informations sur les réplicas Eureka
  - Statistiques générales

### Enregistrement d'un service client

Les services clients s'enregistrent automatiquement en ajoutant la dépendance Eureka Client :

```yaml
# application.yml du service client
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Découverte de services

```java
// Exemple d'utilisation côté client
@Autowired
private DiscoveryClient discoveryClient;

public List<ServiceInstance> getServiceInstances(String serviceName) {
    return discoveryClient.getInstances(serviceName);
}
```

## Monitoring

### Endpoints de santé

- **Health check** : `GET /actuator/health`
- **Informations** : `GET /actuator/info`
- **Métriques Prometheus** : `GET /actuator/prometheus`
- **Eureka health** : `GET /health`

### Métriques importantes

- Nombre de services enregistrés
- Nombre d'instances par service
- Taux de renouvellement des baux (lease renewal)
- Taux d'éviction des instances

### Tracing

Le service est intégré avec Zipkin pour le tracing distribué. Chaque requête est tracée avec un identifiant unique permettant de suivre les appels entre services.

## Structure du projet

```
discovery-service/
├── src/main/java/fr/hoenheimsports/discoveryservice/
│   └── DiscoveryServiceApplication.java        # Application principale avec @EnableEurekaServer
├── src/main/resources/
│   ├── application.yml                         # Configuration de base
│   └── logback-spring.xml                      # Configuration logging
├── build.gradle.kts                            # Configuration Gradle
└── README.md                                   # Cette documentation
```

## Dépendances principales

- **Spring Boot** 3.4.4
- **Spring Cloud Netflix Eureka Server** 2024.0.1
- **Spring Cloud Config Client** 2024.0.1
- **Micrometer Tracing** (Zipkin, Prometheus)
- **Loki Logback Appender** 1.6.0

## Sécurité

- **Authentification** : Support de l'authentification HTTP Basic (configurable)
- **HTTPS** : Support SSL/TLS pour les communications sécurisées
- **Filtrage IP** : Possibilité de restreindre l'accès par IP
- **Rate limiting** : Protection contre les attaques DDoS

## Haute disponibilité

### Configuration en cluster

Pour un déploiement en production, il est recommandé de déployer plusieurs instances d'Eureka :

```yaml
# Configuration pour le cluster
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
```

### Stratégies de déploiement

- **Multi-zone** : Déploiement dans plusieurs zones de disponibilité
- **Peer-to-peer** : Réplication entre instances Eureka
- **Health checks** : Surveillance continue des instances

## Troubleshooting

### Problèmes courants

1. **Services non visibles dans l'interface**
   - Vérifier la configuration `eureka.client.service-url.defaultZone`
   - Vérifier la connectivité réseau
   - Vérifier les logs du service client

2. **Instances marquées comme DOWN**
   - Vérifier les health checks du service
   - Vérifier la configuration `eureka.instance.lease-renewal-interval-in-seconds`
   - Vérifier les timeouts réseau

3. **Problèmes de réplication en cluster**
   - Vérifier la configuration des peers Eureka
   - Vérifier la résolution DNS entre instances
   - Vérifier les ports et firewalls

### Logs utiles

```bash
# Voir les logs du service
docker logs discovery-service

# Logs avec tracing
grep "traceId" logs/discovery-service.log

# Logs d'enregistrement de services
grep "register" logs/discovery-service.log

# Logs de health checks
grep "renew" logs/discovery-service.log
```

### Commandes de diagnostic

```bash
# Vérifier les services enregistrés
curl http://localhost:8761/eureka/apps

# Vérifier une instance spécifique
curl http://localhost:8761/eureka/apps/SERVICE-NAME

# Vérifier l'état du serveur Eureka
curl http://localhost:8761/actuator/health
```

## Contribution

Pour contribuer au développement du Discovery Service :

1. Créer une branche feature
2. Implémenter les modifications
3. Tester avec plusieurs services clients
4. Vérifier la haute disponibilité si applicable
5. Créer une pull request

## Licence

Ce projet fait partie du système ASHS et est soumis aux conditions de licence du projet principal.