# ASHS Deployment Configuration

This directory contains the configuration files for deploying the ASHS application with all its services, including backend microservices, frontend applications, observability tools, and Keycloak authentication.

## Directory Structure

- `observability/`: Configuration files for observability tools (Prometheus, Tempo, Loki, Grafana)
  - `prometheus/`: Prometheus configuration
  - `tempo/`: Tempo configuration
  - `grafana/`: Grafana configuration
- `keycloak/`: Configuration files for Keycloak authentication
- `.env`: Environment variables for all services
- `docker-compose.yml`: Main Docker Compose file that includes all services

## Getting Started

1. Make sure you have Docker and Docker Compose installed on your system.
2. Review and modify the `.env` file if needed to customize credentials and other settings.
3. Start all services using the following command:

```bash
cd deployment
docker-compose up -d
```

## Accessing Services

### Frontend Applications
- **Main Frontend**: http://localhost:4200
- **Admin Frontend**: http://localhost:4201

### API Gateway
- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Observability Tools
- **Grafana**: http://localhost:3000 (admin/password by default)
- **Prometheus**: http://localhost:9090
- **Tempo UI**: http://localhost:3200

### Authentication
- **Keycloak Admin Console**: http://localhost:8079 (admin/admin by default)
- **MailHog Web UI**: http://localhost:8025 (for email testing)

## Configuration

### Backend Services

The backend services include:

- **Discovery Service**: Service registry using Eureka
- **Config Service**: Centralized configuration server
- **Gateway Service**: API gateway using Spring Cloud Gateway
- **Contact Service**: Handles contact form submissions
- **Training Service**: Manages training data with PostgreSQL database
- **Facebook Service**: Manages Facebook integration with PostgreSQL database

### Frontend Applications

The frontend applications include:

- **ASHS Frontend**: Main user-facing application
- **ASHS Frontend Admin**: Administration interface

### Databases

The deployment includes the following databases:

- **PostgreSQL for Training Service**: Stores training data
- **PostgreSQL for Facebook Service**: Stores Facebook integration data
- **PostgreSQL for Keycloak**: Stores authentication data

### Email Service

- **MailHog**: SMTP server for testing email functionality

### Observability

The observability stack includes:

- **Prometheus**: For metrics collection and storage
- **Tempo**: For distributed tracing
- **Loki**: For log aggregation
- **Grafana**: For visualization of metrics, traces, and logs

The configuration files are located in the `observability/` directory:

- `prometheus/prometheus.yml`: Defines scrape targets for Prometheus
- `tempo/tempo.yml`: Configures Tempo for trace collection
- `grafana/datasources.yml`: Configures Grafana datasources

### Keycloak

Keycloak is configured with a pre-defined realm for the ASHS application. The configuration files are located in the `keycloak/` directory:

- `realm-export.json`: Contains the realm configuration with roles, clients, and users
- `import-realm.sh`: Script to import the realm into Keycloak

## Customization

To customize the configuration:

1. Modify the `.env` file to change credentials and other settings
2. Update the configuration files in the `observability/` and `keycloak/` directories
3. Restart the services using `docker-compose down` and `docker-compose up -d`

## Troubleshooting

If you encounter issues:

1. Check the logs using `docker-compose logs <service-name>`
2. Verify that all services are running using `docker-compose ps`
3. Ensure that the configuration files are correctly mounted in the containers
