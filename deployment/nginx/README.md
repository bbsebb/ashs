# ASHS Nginx Configuration with mkcert

Ce document explique comment configurer des certificats SSL localement fiables en utilisant mkcert pour le projet ASHS.

## Problème résolu

Les certificats auto-signés traditionnels provoquent des avertissements de sécurité dans les navigateurs et peuvent causer des problèmes avec certaines applications. mkcert résout ce problème en créant une autorité de certification (CA) locale qui est approuvée par votre système et vos navigateurs.

## Prérequis

- Windows 10 ou 11
- PowerShell avec droits d'administrateur
- Fichier hosts configuré avec les domaines locaux (déjà fait selon la description du problème)

## Installation et configuration

1. **Exécuter le script PowerShell**

   Ouvrez PowerShell en tant qu'administrateur et exécutez le script `generate-mkcert.ps1` :

   ```powershell
   cd C:\Users\bbseb\Documents\programmation\ashs\deployment\nginx
   .\generate-mkcert.ps1
   ```

   Ce script va :
   - Installer Chocolatey (gestionnaire de paquets pour Windows) si nécessaire
   - Installer mkcert via Chocolatey si nécessaire
   - Installer l'autorité de certification locale
   - Générer des certificats pour tous les domaines utilisés dans la configuration Nginx
   - Copier les certificats générés dans le répertoire nginx

2. **Vérifier les certificats générés**

   Après l'exécution du script, vous devriez voir les fichiers suivants dans le répertoire nginx :
   - `mkcert.crt` - Le certificat généré
   - `mkcert.key` - La clé privée du certificat
   - Un dossier `certs` contenant les certificats originaux

3. **Démarrer les services**

   Lancez les services avec Docker Compose :

   ```powershell
   cd C:\Users\bbseb\Documents\programmation\ashs\deployment
   docker-compose -f docker-compose.yml -f nginx\docker-compose-staging.yml up -d
   ```

## Vérification

Après le démarrage des services, vous devriez pouvoir accéder à vos domaines locaux via HTTPS sans avertissements de sécurité. Par exemple :

- https://frontend/
- https://admin/
- https://api/
- https://auth/
- https://grafana/
- https://prometheus/
- https://tempo/
- https://loki/

## Dépannage

1. **Problèmes d'accès aux domaines**
   
   Vérifiez que votre fichier hosts est correctement configuré. Il devrait contenir des entrées comme :
   ```
   127.0.0.1 frontend admin api auth grafana prometheus tempo loki
   ```

2. **Certificat non reconnu**
   
   Si vous voyez encore des avertissements de sécurité :
   - Vérifiez que mkcert a bien installé l'autorité de certification locale
   - Redémarrez votre navigateur
   - Exécutez à nouveau le script `generate-mkcert.ps1`

3. **Problèmes avec Docker**
   
   Si les services ne démarrent pas correctement :
   - Vérifiez que les chemins dans `docker-compose-staging.yml` sont corrects
   - Assurez-vous que les fichiers `mkcert.crt` et `mkcert.key` existent dans le répertoire nginx

## Notes supplémentaires

- Les certificats générés par mkcert sont uniquement fiables sur la machine où ils ont été créés
- Si vous utilisez plusieurs machines pour le développement, vous devrez exécuter le script sur chaque machine
- Les certificats n'expirent pas, mais il est recommandé de les régénérer périodiquement