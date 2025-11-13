# Diagrama de Arquitectura de Módulos - Collectibles Platform

Este diagrama muestra la arquitectura de componentes del sistema y cómo interactúan entre sí.

```mermaid
architecture-beta
    group client(internet)[Cliente Web]
        service browser(server)[Navegador]
        service websocket(internet)[WebSocket JS]
    
    group sparkapp(cloud)[Spark Java Application]
        group routing(server)[Capa de Enrutamiento]
            service webcontroller(server)[WebController]
            service itemcontroller(server)[ItemController]
            service offercontroller(server)[OfferController]
            service usercontroller(server)[UserController]
        
        group service_layer(server)[Capa de Servicios]
            service itemservice(server)[ItemService]
            service offerservice(server)[OfferService]
            service userservice(server)[UserService]
        
        group websocket_layer(server)[Capa WebSocket]
            service wshandler(server)[PriceUpdateWSHandler]
        
        group persistence(server)[Capa de Persistencia]
            service dbservice(database)[DatabaseService]
            service jdbi(database)[JDBI]
    
    group templates(cloud)[Templates y Assets]
        service mustache(server)[Mustache Templates]
        service static(disk)[Static Files]
    
    group external(internet)[Servicios Externos]
        service postgres(database)[PostgreSQL DB]
    
    group models(cloud)[Data Models]
        service item(server)[Item]
        service offer(server)[Offer]
        service user(server)[User]
        service rankedoffer(server)[RankedOffer]
    
    browser:R --> L:webcontroller
    browser:R --> L:itemcontroller
    browser:R --> L:offercontroller
    browser:R --> L:usercontroller
    
    websocket:R --> L:wshandler
    
    webcontroller:B --> T:itemservice
    webcontroller:B --> T:offerservice
    
    itemcontroller:B --> T:itemservice
    offercontroller:B --> T:offerservice
    usercontroller:B --> T:userservice
    
    wshandler:B --> T:offerservice
    
    itemservice:B --> T:dbservice
    offerservice:B --> T:dbservice
    userservice:B --> T:dbservice
    
    dbservice:B --> T:jdbi
    
    itemservice:R --> L:item
    offerservice:R --> L:offer
    offerservice:R --> L:rankedoffer
    userservice:R --> L:user
    
    webcontroller:L --> R:mustache
    mustache:B --> T:static
    
    jdbi:B --> T:postgres
    
    browser:L --> R:static
```

## Componentes Principales:

### 1. **Cliente Web**
- **Navegador**: Realiza solicitudes HTTP
- **WebSocket JS**: Mantiene conexión en tiempo real para actualizaciones de precios

### 2. **Spark Java Application**

#### Capa de Enrutamiento:
- **WebController**: Maneja rutas SSR para páginas HTML (/, /ranking, /item/:id)
- **ItemController**: API REST para items
- **OfferController**: API REST para ofertas
- **UserController**: API REST para usuarios

#### Capa de Servicios:
- **ItemService**: Lógica de items (filtros, búsqueda, CRUD)
- **OfferService**: Lógica de ofertas (validación, ranking, creación)
- **UserService**: Lógica de usuarios (gestión en memoria)

#### Capa WebSocket:
- **PriceUpdateWSHandler**: Maneja conexiones WebSocket para actualizaciones en tiempo real

#### Capa de Persistencia:
- **DatabaseService**: Inicializa y gestiona conexión a BD
- **JDBI**: Mapper de resultados SQL a objetos Java

### 3. **Modelos de Datos**
- **Item**: Artículo coleccionable
- **Offer**: Puja/Oferta en un artículo
- **User**: Usuario del sistema
- **RankedOffer**: Oferta con ranking y datos del item

### 4. **Templates & Assets**
- **Mustache Templates**: Plantillas HTML para renderizado en servidor
- **Static Files**: CSS, JavaScript y recursos HTML estáticos

### 5. **Servicios Externos**
- **PostgreSQL DB**: Base de datos relacional para persistencia

## Flujos de Comunicación:

1. **Flujo HTTP tradicional**:
   - Browser → WebController → ItemService → DatabaseService → JDBI → PostgreSQL
   - DatabaseService → ItemService → WebController → Mustache → Browser

2. **Flujo WebSocket**:
   - WebSocket JS (Browser) → PriceUpdateWSHandler
   - PriceUpdateWSHandler → Broadcast a todas las sesiones activas
   - Sessions activas → item-detail-app.js → Actualiza precios en tiempo real

3. **Flujo REST API**:
   - Browser → ItemController/OfferController → Services → Database
   - Database → Services → Controllers → JSON Response → Browser

## Tecnologías Utilizadas:

- **Framework Web**: Spark Java 2.9.4
- **Base de Datos**: PostgreSQL 42.6.0
- **Mapper SQL**: JDBI 3.38.2
- **Template Engine**: Mustache (Spark)
- **JSON**: GSON 2.10.1
- **Logging**: Logback 1.4.14
- **Build**: Maven 3.9.x
- **Java**: JDK 17
