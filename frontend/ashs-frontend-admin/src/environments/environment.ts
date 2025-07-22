export const environment = {
  production: true,
  baseUrl: 'http://gateway:8080/api',
  keycloak: {
    realm: 'ashs',
    url: 'http://keycloak:8080',
    clientId: 'angular-frontend-admin'
  }
};
