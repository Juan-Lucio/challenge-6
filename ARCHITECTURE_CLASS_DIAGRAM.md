# Diagrama de Clases - Collectibles Platform

Este diagrama muestra la estructura de clases principales y sus relaciones.

```mermaid
classDiagram
    namespace Models {
        class Item {
            -String id
            -String name
            -String description
            -double price
            -String imageUrl
            +getId() String
            +getName() String
            +getDescription() String
            +getPrice() double
            +getImageUrl() String
            +setId(String)
            +setName(String)
            +setDescription(String)
            +setPrice(double)
            +setImageUrl(String)
        }

        class Offer {
            -int offer_id
            -String item_id
            -String name
            -String email
            -double amount
            -Timestamp created_at
            +getOffer_id() int
            +getItem_id() String
            +getName() String
            +getEmail() String
            +getAmount() double
            +getCreated_at() Timestamp
            +getFormattedAmount() String
            +setOffer_id(int)
            +setItem_id(String)
            +setName(String)
            +setEmail(String)
            +setAmount(double)
            +setCreated_at(Timestamp)
        }

        class RankedOffer {
            -int offer_id
            -String item_id
            -String item_name
            -String offer_name
            -String email
            -double amount
            -Timestamp created_at
            -int rank
            +getters()
            +setters()
        }

        class User {
            -String id
            -String username
            -String email
            +getId() String
            +getUsername() String
            +getEmail() String
            +setId(String)
            +setUsername(String)
            +setEmail(String)
        }
    }

    namespace Services {
        class ItemService {
            -Jdbi jdbi
            +ItemService(Jdbi)
            +getAllItems(minPrice, maxPrice) List~Item~
            +getItemById(id) Item
            +createItem(item) void
            +updateItem(item) void
            +deleteItem(id) void
        }

        class OfferService {
            -Jdbi jdbi
            +OfferService(Jdbi)
            +createOffer(offer) void
            +getOffersByItemId(itemId) List~Offer~
            +getRankedOffers() List~RankedOffer~
            +updateOffer(offer) void
            +validateOffer(offer) boolean
        }

        class UserService {
            -Map~String, User~ users
            +addUser(user) void
            +getUserById(id) User
            +getAllUsers() List~User~
            +updateUser(user) void
            +deleteUser(id) void
        }
    }

    namespace Controllers {
        class WebController {
            -ItemService itemService
            -OfferService offerService
            -TemplateEngine templateEngine
            +WebController(ItemService, OfferService, TemplateEngine)
            +registerRoutes() void
            +getHome() ModelAndView
            +getRanking() ModelAndView
            +getItemDetail() ModelAndView
        }

        class ItemController {
            -ItemService itemService
            +ItemController(ItemService)
            +registerRoutes() void
            +getAllItems() String
            +getItemById(id) String
            +createItem(item) String
        }

        class OfferController {
            -OfferService offerService
            +OfferController(OfferService)
            +registerRoutes() void
            +createOffer(offer) String
            +getOffersByItem(itemId) String
        }

        class UserController {
            -UserService userService
            +UserController(UserService)
            +registerRoutes() void
            +getUser(id) String
            +createUser(user) String
        }
    }

    namespace Database {
        class DatabaseService {
            -Jdbi jdbi
            +DatabaseService()
            +getJdbi() Jdbi
            +initializeDatabase() void
            +executeQuery(sql) ResultSet
            +executeUpdate(sql) int
        }
    }

    namespace WebSocket {
        class PriceUpdateWebSocketHandler {
            -static Set~Session~ sessions
            +onConnect(session) void
            +onMessage(session, message) void
            +onClose(session) void
            +onError(session, error) void
            +broadcast(message) void
        }
    }

    namespace Exceptions {
        class InvalidOfferException {
            +InvalidOfferException(message)
        }

        class NotFoundException {
            +NotFoundException(message)
        }
    }

    namespace Utils {
        class JsonUtil {
            +toJson(object) String
            +fromJson(json, clazz) T
            +prettyPrint(json) String
        }
    }

    %% Relaciones entre Services y Models
    ItemService --> Item: maneja
    OfferService --> Offer: maneja
    OfferService --> RankedOffer: transforma
    UserService --> User: maneja

    %% Relaciones entre Controllers y Services
    WebController --> ItemService: utiliza
    WebController --> OfferService: utiliza
    ItemController --> ItemService: utiliza
    OfferController --> OfferService: utiliza
    UserController --> UserService: utiliza

    %% Relaciones con Database
    ItemService --> DatabaseService: consulta
    OfferService --> DatabaseService: consulta
    UserService --> DatabaseService: consulta

    %% Relaciones con Exceptions
    OfferService --> InvalidOfferException: lanza
    ItemService --> NotFoundException: lanza
    OfferService --> NotFoundException: lanza

    %% Relaciones con Utils
    Controllers --> JsonUtil: utiliza
    ItemController --> JsonUtil: utiliza
    OfferController --> JsonUtil: utiliza

    %% WebSocket
    WebController --> PriceUpdateWebSocketHandler: registra

    %% Template Engine
    WebController --> "MustacheTemplateEngine": utiliza

    style Item fill:#E1F5E1
    style Offer fill:#E1F5E1
    style User fill:#E1F5E1
    style RankedOffer fill:#E1F5E1
    style ItemService fill:#E1E5F5
    style OfferService fill:#E1E5F5
    style UserService fill:#E1E5F5
    style WebController fill:#F5E1E1
    style ItemController fill:#F5E1E1
    style OfferController fill:#F5E1E1
    style UserController fill:#F5E1E1
    style DatabaseService fill:#F5F5E1
    style PriceUpdateWebSocketHandler fill:#E1F5F5
    style InvalidOfferException fill:#FFE1E1
    style NotFoundException fill:#FFE1E1
```

## Descripción de las Clases:

### Modelos (Models):
- **Item**: Representa un artículo coleccionable con propiedades como nombre, descripción, precio e imagen
- **Offer**: Representa una oferta/puja realizada por un usuario en un artículo
- **RankedOffer**: Versión extendida de Offer que incluye información del item y ranking
- **User**: Representa un usuario del sistema

### Servicios (Services):
- **ItemService**: Gestiona lógica de negocios relacionada con items (CRUD)
- **OfferService**: Gestiona lógica de negocios relacionada con ofertas (validación, ranking)
- **UserService**: Gestiona lógica de negocios relacionada con usuarios

### Controladores (Controllers):
- **WebController**: Maneja rutas SSR (Server-Side Rendering) para páginas HTML
- **ItemController**: API REST para operaciones de items
- **OfferController**: API REST para operaciones de ofertas
- **UserController**: API REST para operaciones de usuarios

### Base de Datos:
- **DatabaseService**: Gestiona la conexión a PostgreSQL mediante JDBI

### WebSocket:
- **PriceUpdateWebSocketHandler**: Maneja conexiones WebSocket para actualizaciones en tiempo real

### Excepciones:
- **InvalidOfferException**: Lanzada cuando una oferta no cumple con las validaciones
- **NotFoundException**: Lanzada cuando un recurso no se encuentra

### Utilidades:
- **JsonUtil**: Funciones auxiliares para serialización/deserialización JSON
