# ASHS Deployment Configuration

This directory contains the configuration files for deploying the ASHS application with all its services, including
backend microservices, frontend applications, observability tools, and Keycloak authentication.

## Directory Structure

- `observability/`: Configuration files for observability tools (Prometheus, Tempo, Loki, Grafana)
    - `prometheus/`: Prometheus configuration
    - `tempo/`: Tempo configuration
    - `grafana/`: Grafana configuration
- `keycloak/`: Configuration files for Keycloak authentication
- `nginx/`: Configuration files for Nginx reverse proxy
    - `nginx.conf`: Nginx configuration file
    - `docker-compose.yml`: Docker Compose file for Nginx service
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

All services are now accessible through the Nginx reverse proxy. You need to configure your hosts file to map the server
names to localhost.

Add the following entries to your hosts file (`/etc/hosts` on Linux/Mac or `C:\Windows\System32\drivers\etc\hosts` on
Windows):

```
127.0.0.1 frontend
127.0.0.1 admin
127.0.0.1 api
127.0.0.1 auth
127.0.0.1 grafana
127.0.0.1 prometheus
127.0.0.1 tempo
127.0.0.1 loki
127.0.0.1 keycloak-admin
```

### Frontend Applications

- **Main Frontend**: https://frontend
- **Admin Frontend**: https://admin

### API Gateway

- **API Gateway**: https://api
- **Swagger UI**: https://api/swagger-ui.html

### Observability Tools

- **Grafana**: https://grafana (admin/password by default)
- **Prometheus**: https://prometheus
- **Tempo UI**: https://tempo

### Authentication

- **Keycloak Admin Console**: https://keycloak-admin (admin/admin by default)

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

Keycloak is configured with a pre-defined realm for the ASHS application. The configuration files are located in the
`keycloak/` directory:

- `realm-export.json`: Contains the realm configuration with roles, clients, and users
- `import-realm.sh`: Script to import the realm into Keycloak

### Nginx Reverse Proxy

Nginx is configured as a reverse proxy to provide a unified access point for all services. The configuration files are
located in the `nginx/` directory:

- `nginx.conf`: Contains the Nginx configuration with proxy settings for all services
- `docker-compose.yml`: Contains the Docker Compose configuration for the Nginx service

The Nginx reverse proxy provides the following benefits:

- Single entry point for all services
- Improved security by not exposing services directly
- Simplified access through consistent URLs
- Ability to add SSL/TLS termination in one place

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
4. For Nginx-related issues:
    - Check Nginx logs: `docker-compose logs nginx`
    - Verify that your hosts file is correctly configured
    - Ensure that all services are healthy and accessible from within the Docker network
    - Test direct access to services by temporarily re-enabling their port mappings
