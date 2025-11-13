# Sequence Diagrams - Main Flows

This document shows the most important sequence flows of the system.

## 1. Flow: User Views Home Page

```mermaid
sequenceDiagram
    participant Client as Browser/Client
    participant Router as Spark Router
    participant Controller as WebController
    participant Service as ItemService
    participant DB as DatabaseService
    participant PG as PostgreSQL
    participant Template as MustacheEngine
    participant UI as HTML Page

    Client->>+Router: GET / (with optional filters)
    Router->>+Controller: get('/', minPrice, maxPrice)
    Controller->>+Service: getAllItems(minPrice, maxPrice)
    Service->>+DB: SQL query
    DB->>+PG: SELECT * FROM items WHERE price BETWEEN ? AND ?
    PG-->>-DB: List<Item>
    DB-->>-Service: List<Item>
    Service-->>-Controller: List<Item>
    Controller->>+Template: render('index.mustache', items)
    Template->>Template: Generate HTML with items
    Template-->>-Controller: HTML String
    Controller-->>-Router: HTML Response (200 OK)
    Router-->>-Client: HTML Page
    Client->>UI: Display page with items
```

## 2. Flow: User Creates New Offer

```mermaid
sequenceDiagram
    participant Client as Browser/Client
    participant Router as Spark Router
    participant Controller as OfferController
    participant Service as OfferService
    participant Validator as Validator
    participant DB as DatabaseService
    participant PG as PostgreSQL
    participant WS as WebSocketHandler

    Client->>+Router: POST /offer (offerData)
    Router->>+Controller: post('/offer', data)
    Controller->>+Service: createOffer(offerData)
    
    Service->>+Validator: validateOffer(offerData)
    alt Validation fails
        Validator-->>Service: InvalidOfferException
        Service-->>Controller: Exception
        Controller-->>Router: 400 Bad Request
        Router-->>Client: Error JSON
    else Validation successful
        Validator-->>-Service: ✓ Valid
        Service->>+DB: insertOffer(offer)
        DB->>+PG: INSERT INTO offers (...)
        PG-->>-DB: offer_id
        DB-->>-Service: void
        Service-->>-Controller: ✓ Created
        
        Service->>+WS: notifyPriceUpdate(itemId)
        WS->>WS: broadcast() to all sessions
        WS-->>-Service: ✓ Sent
        
        Controller-->>Router: 201 Created
        Router-->>Client: JSON (offerId)
    end
```

## 3. Flow: View Offer Rankings

```mermaid
sequenceDiagram
    participant Client as Browser/Client
    participant Router as Spark Router
    participant Controller as WebController
    participant Service as OfferService
    participant DB as DatabaseService
    participant PG as PostgreSQL
    participant Template as MustacheEngine

    Client->>+Router: GET /ranking
    Router->>+Controller: get('/ranking')
    Controller->>+Service: getRankedOffers()
    Service->>+DB: execute complex query
    
    DB->>+PG: SELECT o.*, i.name, RANK() OVER (PARTITION BY item_id ORDER BY amount DESC) as rank FROM offers o JOIN items i ON o.item_id = i.id
    PG-->>-DB: List<RankedOffer>
    
    DB->>DB: map results to RankedOffer
    DB-->>-Service: List<RankedOffer>
    Service-->>-Controller: List<RankedOffer>
    
    Controller->>+Template: render('ranking.mustache', rankedOffers)
    Template->>Template: Generate ordered HTML table by rank
    Template-->>-Controller: HTML String
    
    Controller-->>-Router: HTML Response (200 OK)
    Router-->>-Client: Ranking page
    Client->>Client: Display offers sorted by item
```

## 4. Flow: Real-time Updates (WebSocket)

```mermaid
sequenceDiagram
    participant Browser1 as Browser 1 (item-detail-app.js)
    participant Browser2 as Browser 2 (item-detail-app.js)
    participant WS as PriceUpdateWSHandler
    participant SessionMgr as Session Manager
    participant Service as OfferService
    participant DB as DatabaseService

    Browser1->>+WS: WebSocket Connect (/ws/price-updates)
    WS->>SessionMgr: addSession(session1)
    WS-->>-Browser1: ✓ Connected
    
    Browser2->>+WS: WebSocket Connect (/ws/price-updates)
    WS->>SessionMgr: addSession(session2)
    WS-->>-Browser2: ✓ Connected
    
    Browser1->>+WS: Message: {"action": "watch", "itemId": "item123"}
    WS->>WS: Process command
    WS-->>-Browser1: ✓ Watching item123
    
    Note over Browser1,Browser2: User 1 makes new offer for item123
    
    Browser1->>+WS: POST /offer (offerData)
    WS->>+Service: createOffer(offer)
    Service->>+DB: insertOffer()
    DB-->>-Service: ✓ Success
    Service->>+WS: notifyUpdate(itemId)
    
    WS->>WS: broadcast(updated_price)
    WS->>Browser1: {"type": "price_update", "itemId": "item123", "newPrice": "$999"}
    WS->>Browser2: {"type": "price_update", "itemId": "item123", "newPrice": "$999"}
    
    Browser1->>Browser1: Update UI without refresh
    Browser2->>Browser2: Update UI without refresh
    
    Service-->>-WS: ✓ Notified
```

## 5. Flow: Get Item Detail

```mermaid
sequenceDiagram
    participant Client as HTTP Client
    participant Router as Spark Router
    participant WebCtrl as WebController
    participant ItemSvc as ItemService
    participant OfferSvc as OfferService
    participant DB as DatabaseService
    participant PG as PostgreSQL
    participant Template as MustacheEngine

    Client->>+Router: GET /item/item123
    Router->>+WebCtrl: get('/item/:id', 'item123')
    
    WebCtrl->>+ItemSvc: getItemById('item123')
    ItemSvc->>+DB: query("SELECT * FROM items WHERE id = ?", 'item123')
    DB->>+PG: SELECT * FROM items WHERE id = 'item123'
    PG-->>-DB: Item object
    DB-->>-ItemSvc: Item
    ItemSvc-->>-WebCtrl: Item
    
    alt Item found
        WebCtrl->>+OfferSvc: getOffersByItemId('item123')
        OfferSvc->>+DB: query("SELECT * FROM offers WHERE item_id = ? ORDER BY amount DESC", 'item123')
        DB->>+PG: SELECT * FROM offers WHERE item_id = 'item123' ORDER BY amount DESC
        PG-->>-DB: List<Offer>
        DB-->>-OfferSvc: List<Offer>
        OfferSvc-->>-WebCtrl: List<Offer>
        
        WebCtrl->>+Template: render('item.mustache', {item, offers})
        Template->>Template: Generate detailed HTML
        Template-->>-WebCtrl: HTML String
        WebCtrl-->>-Router: 200 OK + HTML
        
    else Item not found
        ItemSvc-->>WebCtrl: NotFoundException
        WebCtrl-->>Router: 404 Not Found
    end
    
    Router-->>-Client: HTML Response or 404 Error
```

## Legend

| Symbol | Meaning |
|--------|---------|
| `->` | Synchronous call |
| `-->` | Response |
| `+` | Actor activation |
| `-` | Activity end |
| `alt` | Alternative (if/else) |
| `Note` | Comment |
| `par` | Parallel operations |

## Technologies Involved in Flows

- **Spark Java**: Routing and request handling
- **JDBI**: SQL result mapping
- **PostgreSQL**: Persistent storage
- **Mustache**: Template rendering
- **WebSocket**: Real-time communication
- **GSON**: JSON serialization
- **Logback**: Operation logging

---

**Note**: These sequence flows illustrate the most common use cases of the system. Actual flows may vary depending on specific parameters and error conditions.
