#!/bin/bash

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
until curl -s http://keycloak:8080/health/ready > /dev/null; do
    sleep 5
done

# Import realm
echo "Importing realm..."
/opt/keycloak/bin/kc.sh import --file /opt/keycloak/data/import/realm-export.json

echo "Realm import completed."