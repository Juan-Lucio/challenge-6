# 🎨 System Architecture Diagrams - Collectibles Platform

## 📚 General Description

This set of Mermaid documents provides a complete visualization of the system architecture of the **Collectibles Platform**, a marketplace for collectible item auctions.

### 📂 Included Files

| File | Diagram Type | Description |
|------|------------|-------------|
| **ARCHITECTURE_OVERVIEW.md** | Summary | Overall view of entire architecture with index |
| **ARCHITECTURE_FLOWCHART.md** | Flowchart | Complete flow of HTTP and WebSocket requests |
| **ARCHITECTURE_CLASS_DIAGRAM.md** | Class Diagram | UML structure of classes and relationships |
| **ARCHITECTURE_MODULES.md** | Architecture Diagram | System modules and components in layers |
| **ARCHITECTURE_SEQUENCE_DIAGRAMS.md** | Sequence Diagrams | Sequence flows of main use cases |

---

## 🎯 Documented Use Cases

### 1. **View Home Page** (`GET /`)
- User accesses the system
- Items are loaded with optional price filters
- **File**: `ARCHITECTURE_SEQUENCE_DIAGRAMS.md` - Flow 1

### 2. **Create New Offer** (`POST /offer`)
- User places a bid
- Data validation
- Storage in database
- Real-time notification via WebSocket
- **File**: `ARCHITECTURE_SEQUENCE_DIAGRAMS.md` - Flow 2

### 3. **View Offer Rankings** (`GET /ranking`)
- User views offers sorted by item
- Complex SQL query with ranking
- Rendering of sorted table
- **File**: `ARCHITECTURE_SEQUENCE_DIAGRAMS.md` - Flow 3

### 4. **Real-time Updates** (`WebSocket`)
- Multiple browsers connected simultaneously
- Broadcasting of price updates
- **File**: `ARCHITECTURE_SEQUENCE_DIAGRAMS.md` - Flow 4

### 5. **Get Item Details** (`GET /item/:id`)
- View complete item information
- List all offers for that item
- Handle not found items (404)
- **File**: `ARCHITECTURE_SEQUENCE_DIAGRAMS.md` - Flow 5

---

## 🏗️ Layered Architecture

```
┌────────────────────────────────────────────────┐
│          CAPA DE PRESENTACIÓN                  │
│     Navegador (HTML5 + JavaScript)             │
└────────────────┬─────────────────────────────┘
                 │ HTTP / WebSocket
┌────────────────▼─────────────────────────────┐
│     CAPA DE ENRUTAMIENTO (Spark Router)       │
│  WebController | ItemController |             │
│  OfferController | UserController             │
└────────────────┬─────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────┐
│    CAPA DE LÓGICA DE NEGOCIO (Services)       │
│  ItemService | OfferService | UserService     │
└────────────────┬─────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────┐
│      CAPA DE PERSISTENCIA (DatabaseService)   │
│         JDBI → PostgreSQL                     │
└──────────────────────────────────────────────┘
```

---

## 🔧 Main Technologies

### Backend
- **Framework**: Spark Java 2.9.4
- **Base de Datos**: PostgreSQL 42.6.0
- **Mapper ORM**: JDBI 3.38.2
- **Template Engine**: Mustache 2.7.1
- **JSON**: GSON 2.10.1
- **Logging**: Logback 1.4.14
- **WebSocket**: Spark WebSocket Handler
- **Java Version**: JDK 17

### Frontend
- **HTML5 + CSS3**
- **JavaScript (Vanilla)**
- **WebSocket API**
- **Testing**: Jest con Coverage

### Build & Tools
- **Build Tool**: Maven 3.9.x
- **Testing**: JUnit 5.9.1, Mockito 4.11.0
- **Code Coverage**: JaCoCo 0.8.8

---

## 📊 Componentes Principales

### 🎮 Controladores
- **WebController**: Maneja rutas SSR (Server-Side Rendering)
- **ItemController**: API REST para items
- **OfferController**: API REST para ofertas
- **UserController**: API REST para usuarios

### ⚙️ Servicios
- **ItemService**: Lógica de items (CRUD, filtros)
- **OfferService**: Lógica de ofertas (validación, ranking)
- **UserService**: Gestión de usuarios (en memoria)

### 📦 Modelos
- **Item**: Artículo coleccionable
- **Offer**: Puja/oferta en un item
- **RankedOffer**: Oferta con ranking
- **User**: Usuario del sistema

### 💾 Persistencia
- **DatabaseService**: Inicialización y gestión de JDBI
- **PostgreSQL**: Base de datos relacional

### 📡 WebSocket
- **PriceUpdateWebSocketHandler**: Manejo de conexiones en tiempo real

---

## 📈 Flujos de Datos Principales

### Flujo 1: HTTP Request → Response
```
Cliente → WebController → Service → DatabaseService → PostgreSQL
PostgreSQL → DatabaseService → Service → Template Engine → Cliente
```

### Flujo 2: WebSocket Broadcast
```
Navegador 1 → WebSocket Handler → Broadcast → Navegadores 2..N
```

### Flujo 3: Validación y Persistencia
```
Request → Validator → Insert to DB → Notify via WebSocket → Response
```

---

## 🎨 Cómo Usar los Diagramas

### En GitHub
Los diagramas Mermaid se renderizan automáticamente en:
- README.md
- Pull Requests
- Issues
- Documentación

### En VS Code
1. Instala la extensión "Markdown Preview Mermaid Support"
2. Abre el archivo `.md` y presiona `Ctrl+Shift+V`
3. Los diagramas se renderizarán automáticamente

### Online
Copia el código Mermaid en https://mermaid.live para visualizar

---

## 🔄 Relaciones entre Diagramas

```
ARCHITECTURE_OVERVIEW.md (ÍNDICE PRINCIPAL)
    ├── ARCHITECTURE_FLOWCHART.md (Flujo General)
    │   └── Describe el routing general del sistema
    │
    ├── ARCHITECTURE_CLASS_DIAGRAM.md (Estructura OOP)
    │   └── Define las clases y sus relaciones
    │
    ├── ARCHITECTURE_MODULES.md (Arquitectura en Capas)
    │   └── Muestra interacción entre módulos
    │
    └── ARCHITECTURE_SEQUENCE_DIAGRAMS.md (Casos de Uso)
        └── Detalla 5 flujos de secuencia principales
```

---

## 📝 Notas Importantes

### Decisiones de Arquitectura

1. **Separación de Responsabilidades**
   - Controllers: Enrutamiento y validación HTTP
   - Services: Lógica de negocio
   - Database: Persistencia
   - Resultados limpia arquitectura testeable

2. **WebSocket para Realtime**
   - Actualizaciones instantáneas de precios
   - Broadcasting a múltiples clientes
   - No requiere polling

3. **Mustache Templates**
   - Renderizado en servidor (SSR)
   - Plantillas simples y seguras
   - Compatible con Spark Java

4. **JDBI sobre Hibernate/JPA**
   - Mapeo SQL explícito
   - Mejor control y performance
   - Queries optimizadas

5. **Usuarios en Memoria (Por ahora)**
   - Según especificación "Big Bang"
   - Futuro: migrar a DatabaseService

---

## 🚀 Cómo Extender la Arquitectura

### Agregar Nuevo Endpoint
1. Crear metodo en Controller
2. Crear/extender Service con lógica
3. Agregar queries en DatabaseService si es necesario
4. Crear template si es SSR

### Agregar Nueva Entidad
1. Crear clase Model (POJO)
2. Crear Service correspondiente
3. Agregar SQL a DatabaseService
4. Crear Controller para exponer API

### Agregar WebSocket Handler
1. Crear clase que extends `WebSocketHandler`
2. Implementar onConnect, onMessage, onClose, onError
3. Registrar en App.main() con `webSocket(path, handler)`

---

## 📊 Estadísticas del Sistema

| Métrica | Valor |
|---------|-------|
| **Controllers** | 4 (Web, Item, Offer, User) |
| **Services** | 3 (Item, Offer, User) |
| **Models** | 4 (Item, Offer, RankedOffer, User) |
| **HTTP Routes** | 10+ endpoints |
| **WebSocket Endpoints** | 1 (/ws/price-updates) |
| **DB Tables** | 3 (items, offers, users) |
| **Exceptions** | 2 (InvalidOfferException, NotFoundException) |
| **Templates** | 4 (index, item, ranking, 404) |

---

## ✅ Checklist de Documentación

- ✅ Diagrama de flujo general
- ✅ Diagrama de clases UML
- ✅ Diagrama de arquitectura en capas
- ✅ Diagramas de secuencia (5 casos de uso)
- ✅ Resumen de tecnologías
- ✅ Documentación de componentes
- ✅ Flujos de datos
- ✅ Instrucciones de uso

---

## 📞 Contacto

Para preguntas sobre la arquitectura o los diagramas, consultar:
- Documentación en archivos `.md`
- Código fuente en `src/main/java`
- Tests en `src/test/java`

---

**Versión**: 3.0-DB_MIGRATION
**Última actualización**: Noviembre 2025
**Autor**: Architecture Documentation Team
