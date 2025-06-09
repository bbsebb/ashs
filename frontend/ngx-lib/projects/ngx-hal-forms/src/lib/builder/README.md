# HAL Resource Builders

Ce module fournit des builders pour faciliter la création d'objets HAL (Hypertext Application Language) conformes aux spécifications.

## Table des matières

- [Introduction](#introduction)
- [Builders disponibles](#builders-disponibles)
- [Exemples d'utilisation](#exemples-dutilisation)
  - [Création d'un HalLink](#création-dun-hallink)
  - [Création d'un HalResource simple](#création-dun-halresource-simple)
  - [Création d'un AllHalResources](#création-dun-allhalresources)
  - [Création d'un PaginatedHalResource](#création-dun-paginatedhalresource)
- [Bonnes pratiques](#bonnes-pratiques)

## Introduction

Les builders HAL permettent de construire facilement des objets HAL typés et conformes aux spécifications, tout en offrant une API fluide et intuitive. Ils gèrent automatiquement la validation des propriétés requises et la structure correcte des objets.

## Builders disponibles

- **HalLinkBuilder** : Pour créer des liens HAL (`HalLink`)
- **HalLinksBuilder** : Pour créer des collections de liens HAL (`HalLinks`)
- **HalResourceBuilder** : Pour créer des ressources HAL de base (`HalResource`)
- **AllHalResourcesBuilder** : Pour créer des collections de ressources HAL (`AllHalResources`)
- **PaginatedHalResourceBuilder** : Pour créer des collections paginées de ressources HAL (`PaginatedHalResource`)

## Exemples d'utilisation

### Création d'un HalLink

```typescript
import { HalLinkBuilder } from './ngx-hal-builder';

// Création d'un lien simple
const simpleLink = new HalLinkBuilder()
  .href('http://api.example.com/resources/1')
  .build();

// Création d'un lien avec toutes les propriétés
const fullLink = new HalLinkBuilder()
  .href('http://api.example.com/resources{?page,size}')
  .templated(true)
  .type('application/hal+json')
  .deprecation('http://api.example.com/deprecated')
  .name('resource')
  .profile('http://api.example.com/profiles/resource')
  .title('Resource Link')
  .hreflang('fr')
  .customProp('priority', 'high')
  .build();
```

### Création d'un HalResource simple

```typescript
import { HalResourceBuilder, HalLinksBuilder } from './ngx-hal-builder';

// Définition d'une interface pour les propriétés de la ressource
interface Person {
  name: string;
  age: number;
  email: string;
}

// Création d'une ressource HAL typée
const person = new HalResourceBuilder<Person>()
  // Méthode 1 : Utilisation directe d'un objet HalLinks
  .links({
    self: { href: 'http://api.example.com/people/1' }
  })
  
  // Méthode 2 : Utilisation du builder de liens (recommandée)
  .withLinks(linksBuilder => linksBuilder
    .selfUrl('http://api.example.com/people/1')
    .link('profile', { href: 'http://api.example.com/profiles/person' })
  )
  
  // Ajout des propriétés individuellement
  .property('name', 'John Doe')
  .property('age', 30)
  
  // Ou ajout de plusieurs propriétés à la fois
  .properties({
    email: 'john.doe@example.com'
  })
  
  // Ajout d'un template de formulaire
  .template('update', {
    key: 'update',
    method: 'PATCH',
    properties: [
      { name: 'name', required: false },
      { name: 'age', required: false, type: 'number' },
      { name: 'email', required: false, type: 'email' }
    ]
  })
  
  // Construction de l'objet final
  .build();
```

### Création d'un AllHalResources

```typescript
import { AllHalResourcesBuilder, HalResourceBuilder } from './ngx-hal-builder';

// Création de ressources à inclure dans la collection
const resource1 = new HalResourceBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items/1'))
  .property('name', 'Item 1')
  .build();

const resource2 = new HalResourceBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items/2'))
  .property('name', 'Item 2')
  .build();

// Création de la collection de ressources
const allResources = new AllHalResourcesBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items'))
  .embeddedResources('items', [resource1, resource2])
  .build();
```

### Création d'un PaginatedHalResource

```typescript
import { PaginatedHalResourceBuilder, HalResourceBuilder } from './ngx-hal-builder';

// Création de ressources à inclure dans la collection paginée
const resource1 = new HalResourceBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items/1'))
  .property('name', 'Item 1')
  .build();

const resource2 = new HalResourceBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items/2'))
  .property('name', 'Item 2')
  .build();

// Création de la collection paginée
const paginatedResources = new PaginatedHalResourceBuilder()
  .withLinks(builder => builder.selfUrl('http://api.example.com/items?page=0&size=10'))
  .embeddedResources('items', [resource1, resource2])
  
  // Ajout des informations de pagination
  .pagination(
    10,             // size: taille de la page
    100,            // totalElements: nombre total d'éléments
    10,             // totalPages: nombre total de pages
    0               // number: numéro de la page courante
  )
  
  // Ajout des liens de navigation
  .nextLink('http://api.example.com/items?page=1&size=10')
  .prevLink('http://api.example.com/items?page=0&size=10')
  .firstLink('http://api.example.com/items?page=0&size=10')
  .lastLink('http://api.example.com/items?page=9&size=10')
  
  .build();
```

## Bonnes pratiques

1. **Utilisez les types génériques** pour garantir la sécurité de type des propriétés de vos ressources.
2. **Préférez la méthode `withLinks`** plutôt que `links` pour bénéficier de la validation et de l'API fluide du `HalLinksBuilder`.
3. **Validez toujours les objets construits** en vérifiant que les propriétés requises sont présentes.
4. **Utilisez les sous-builders** pour les structures imbriquées complexes.
5. **Gérez les erreurs** qui peuvent être levées lorsque des propriétés requises sont manquantes.
