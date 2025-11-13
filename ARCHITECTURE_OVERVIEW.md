# 📐 System Architecture - Collectibles Platform

This document provides a comprehensive view of the system architecture of the Collectibles Platform using Mermaid diagrams.

## 📋 Table of Contents

1. [System Flow Diagram](#system-flow-diagram)
2. [Class Diagram](#class-diagram)
3. [Modules and Components Diagram](#modules-and-components-diagram)
4. [Technology Summary](#technology-summary)
5. [Data Flows](#data-flows)

---

## System Flow Diagram

See detailed file: [`ARCHITECTURE_FLOWCHART.md`](./ARCHITECTURE_FLOWCHART.md)

**Description**: This diagram illustrates the complete flow of HTTP and WebSocket requests in the application, from the client to the database and template rendering.

**Main Components**:
- 🌐 **Spark Router**: Distributes requests to appropriate controllers
- 🎮 **Controllers**: WebController, ItemController, OfferController, UserController
- ⚙️ **Services**: ItemService, OfferService, UserService
- 🗄️ **Database**: PostgreSQL with JDBI for persistence
- 📄 **Templates**: Mustache for SSR rendering
- 📡 **WebSocket**: Real-time price updates

---

## Class Diagram

See detailed file: [`ARCHITECTURE_CLASS_DIAGRAM.md`](./ARCHITECTURE_CLASS_DIAGRAM.md)

**Description**: Shows the UML structure of all main classes, their attributes, methods, and relationships.

### Organization by Namespaces:

#### 🏷️ **Models** (Data Models)
- `Item`: Collectible item
- `Offer`: Bid/Offer
- `RankedOffer`: Offer with ranking
- `User`: System user

#### ⚙️ **Services** (Business Logic Layer)
- `ItemService`: Item CRUD and search
- `OfferService`: Offer management and validation
- `UserService`: User management (in-memory)

#### 🎮 **Controllers** (Presentation Layer)
- `WebController`: SSR routes for HTML
- `ItemController`: REST API for items
- `OfferController`: REST API for offers
- `UserController`: REST API for users

#### 💾 **Database** (Persistence)
- `DatabaseService`: JDBI initialization and management

#### 🌐 **WebSocket**
- `PriceUpdateWebSocketHandler`: Real-time connection handling

#### 🛠️ **Utils** (Utilities)
- `JsonUtil`: JSON serialization/deserialization

#### ⚠️ **Exceptions** (Exceptions)
- `InvalidOfferException`: Offer validation
- `NotFoundException`: Resource not found

---

## Modules and Components Diagram

See detailed file: [`ARCHITECTURE_MODULES.md`](./ARCHITECTURE_MODULES.md)

**Description**: Layered architecture of the complete system showing component interactions.

### Layer Structure:

```
┌─────────────────────────────────────────────────────────────┐
│                     Web Client                              │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │   Browser        │         │  WebSocket JS    │         │
│  └──────────────────┘         └──────────────────┘         │
└─────────────────────────────────────────────────────────────┘
                          ▼ HTTP / WS
┌─────────────────────────────────────────────────────────────┐
│           Spark Java Application                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       Routing Layer                                  │  │
│  │  WebController | ItemController | OfferController   │  │
│  │  UserController                                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       Service Layer                                  │  │
│  │  ItemService | OfferService | UserService            │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       Persistence Layer                              │  │
│  │  DatabaseService --> JDBI --> PostgreSQL             │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       WebSocket Layer                                │  │
│  │  PriceUpdateWebSocketHandler                         │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
         ▼ SQL                              ▼ Assets
┌──────────────────────┐        ┌──────────────────────┐
│  PostgreSQL DB       │        │ Mustache Templates   │
│  (Persistence)       │        │ Static Files (CSS/JS)│
└──────────────────────┘        └──────────────────────┘
```

---

## Technology Summary

### Backend
- **Web Framework**: Spark Java 2.9.4
  - Lightweight and fast microframework
  - Simplified routes with functional syntax
  
- **Database**: PostgreSQL 42.6.0
  - Powerful and reliable RDBMS
  - JDBI 3.38.2 as result mapper
  - H2 2.2.224 for tests
  
- **Template Engine**: Spark Mustache 2.7.1
  - SSR (Server-Side Rendering)
  - Simple and efficient templates

- **JSON**: GSON 2.10.1
  - Fast serialization/deserialization
  - Easy REST API integration

- **Logging**: Logback 1.4.14
  - Flexible logging system
  - XML configuration

### Frontend
- **Language**: JavaScript/HTML5
- **WebSocket**: Bidirectional communication
- **Testing**: Jest (Coverage & Tests)

### Build & Testing
- **Build Tool**: Maven 3.9.x
- **Java Version**: JDK 17
- **Testing Framework**: JUnit 5.9.1
- **Mocking**: Mockito 4.11.0
- **Code Coverage**: JaCoCo 0.8.8

---

## Data Flows

### 1️⃣ Traditional HTTP Flow (SSR)

```
HTTP Client Request
    ↓
WebController.get('/')
    ↓
ItemService.getAllItems(minPrice, maxPrice)
    ↓
DatabaseService.query() → JDBI → PostgreSQL
    ↓
List<Item>
    ↓
MustacheTemplateEngine.render(index.mustache)
    ↓
HTML Response → Client
```

### 2️⃣ WebSocket Flow (Real-time)

```
Browser (item-detail-app.js)
    ↓ WebSocket Connection
PriceUpdateWebSocketHandler
    ↓
onConnect() → Add session to pool
    ↓
onMessage() → Process price update
    ↓
broadcast() → Send to all active sessions
    ↓
item-detail-app.js → Update DOM
```

### 3️⃣ REST API Flow (POST Offer)

```
HTTP Client POST /offer
    ↓
OfferController.registerRoutes()
    ↓
OfferService.validateOffer() [Validate data]
    ↓
if (valid) → createOffer() 
    else → throw InvalidOfferException
    ↓
DatabaseService.insert() → JDBI → PostgreSQL
    ↓
JSON Response (201 Created or 400 Bad Request)
    ↓
Client
```

### 4️⃣ Offer Ranking Flow

```
Client GET /ranking
    ↓
WebController.getRanking()
    ↓
OfferService.getRankedOffers()
    ↓
DatabaseService.query() → Complex SQL with JOIN and ranking
    ↓
List<RankedOffer>
    ↓
ranking.mustache → Render ordered table
    ↓
HTML Response → Client
```

## Key Architecture Points

### ✅ Advantages
1. **Separation of Concerns**: Controllers, Services, and Persistence
2. **Scalability**: Services can be extended without modifying controllers
3. **Testing**: Services and controllers can be tested independently
4. **Real-time**: WebSocket for instant updates
5. **Efficiency**: JDBI avoids heavy ORMs
6. **SSR + API**: Supports both rendered HTML and JSON APIs

### 🔄 Main Interactions
- Controllers orchestrate logic using Services
- Services implement business rules
- DatabaseService abstracts persistence
- WebSocket maintains active connections
- Templates render dynamic views

### 📊 Data Volume
- Items: Loaded from `items.json`
- Offers: Stored in PostgreSQL
- Users: Stored in memory (per "Big Bang" specification)
- WebSocket Sessions: Maintained in memory

---

## Detailed Diagram Files

For more details, consult:
- 📄 [`ARCHITECTURE_FLOWCHART.md`](./ARCHITECTURE_FLOWCHART.md) - Complete system flow diagram
- 🎨 [`ARCHITECTURE_CLASS_DIAGRAM.md`](./ARCHITECTURE_CLASS_DIAGRAM.md) - UML class diagram
- 🏗️ [`ARCHITECTURE_MODULES.md`](./ARCHITECTURE_MODULES.md) - Components and modules diagram
- 📊 [`ARCHITECTURE_SEQUENCE_DIAGRAMS.md`](./ARCHITECTURE_SEQUENCE_DIAGRAMS.md) - Detailed use case flows

---

**Last Updated**: November 2025
**System Version**: 3.0-DB_MIGRATION
