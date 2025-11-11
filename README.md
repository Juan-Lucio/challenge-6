---

# Collectible Item Sales API - Sprint 1

This repository contains the source code for **Sprint 1** of the collectible item sales project, developed with Java and the Spark framework.

## 🎯 Sprint Objective

The objective of this sprint was to build the foundational base of the RESTful API. The project was structured following a clean architecture (Controller-Service-Model) to ensure scalability and sustainability. Endpoints for user management and item querying were implemented.

## 🛠️ Tech Stack

* **Java** (Version 17)
* **Maven** (Dependency Management)
* **Spark Framework** (Web Micro-framework)
* **Gson** (JSON Handling)
* **Logback** (Logging)

## 🚀 How to Run the Project

Follow these steps to run the server locally.

### Prerequisites

* Java JDK 17 (or higher)
* Apache Maven

### Steps

1.  Clone this repository.
2.  Open a terminal in the project's root folder (the `challenge-6` folder, where the `pom.xml` file is located).
3.  Clean, compile, and run the server using the following Maven command:
    ```bash
    mvn clean compile exec:java
    ```
4.  The server will be active and listening on `http://localhost:8080`.



Here you can see the terminal confirmation that the server has started successfully:

> ### ![alt text](image-2.png)
>
> ``

---

## 🗺️ API Endpoints (Sprint 1)

All routes are prefixed with `/api`.

### Items API

| Verb | Route | Description |
| :--- | :--- | :--- |
| `GET` | `/api/items` | Returns a list (ID, name, price) of all items from `items.json`. |
| `GET` | `/api/items/:id/description` | Returns only the description of a specific item by its ID. |

### Users API

| Verb | Route | Description |
| :--- | :--- | :--- |
| `GET` | `/api/users` | Returns the list of all users (currently test data). |
| `GET` | `/api/users/:id` | Returns a specific user by ID. |
| `POST` | `/api/users` | (Industry Standard) Creates a new user. Send `username` and `email` in the JSON body. |
| `PUT` | `/api/users/:id` | Updates an existing user. Send `username` and `email` in the JSON body. |
| `DELETE` | `/api/users/:id` | Deletes a user by ID. |
| `OPTIONS` | `/api/users/:id` | Checks if a user exists (returns 200 OK or 404 Not Found). |

---

## ✅ Functionality Tests

Below are screenshots of the tests for the main `GET` endpoints in a browser.

### `GET /api/items` Test

> ![alt text](image.png)
> ``

### `GET /api/users` Test

![alt text](image-1.png)
>
> ``

---

## 📁 Project Structure

The project follows a separation of concerns architecture to facilitate maintenance:

challenge-6/ ├── pom.xml └── src/ └── main/ ├── java/ │ └── com/ │ └── collectibles/ │ ├── App.java // (Main class, starts server and routes) │ │ │ ├── item/ // (Items Module) │ │ ├── Item.java // (Data Model) │ │ ├── ItemService.java // (Business Logic) │ │ └── ItemController.java // (Route Definitions) │ │ │ ├── user/ // (Users Module) │ │ ├── User.java // (Data Model) │ │ ├── UserService.java // (Business Logic) │ │ └── UserController.java // (Route Definitions) │ │ │ └── utils/ │ └── JsonUtil.java // (JSON Utility) │ └── resources/ ├── items.json └── logback.xml


## 📝 Key Decision Log (Req 2)

During the development of this sprint, the following key decisions were made to ensure quality and scalability:

1.  **Architecture:** A 3-layer (Controller-Service-Model) architecture was chosen to separate API logic (HTTP) from business logic (Services).
2.  **`POST /users` Route:** The `POST /users/:id` requirement was modified to the industry standard `POST /users` for new resource creation, where the server is responsible for generating the ID.
3.  **`/api` Prefix:** All routes were grouped under `path("/api", ...)` to facilitate future versioning and organization.
4.  **Data Loading:** The `items.json` data is loaded into memory (`ItemSe


# Collectible Item Sales API & Web - Sprint 2

This repository contains the source code for **Sprint 1 and 2** of the collectible item sales project, developed with Java and the Spark framework.

Sprint 1 built the core RESTful API. **Sprint 2 adds the functional, user-facing website**, featuring an elegant design with images, Mustache templates, exception handling, and a form system for making offers.

## 🛠️ Tech Stack

* **Java** (Version 17)
* **Maven** (Dependency Management)
* **Spark Framework** (Web Micro-framework)
* **Mustache** (Template Engine)
* **Gson** (JSON Handling)
* **Logback** (Logging)

## 🚀 How to Run the Project

Follow these steps to run the server locally.

### Prerequisites

* Java JDK 17 (or higher)
* Apache Maven

### Steps

1.  Clone this repository.
2.  Open a terminal in the project's root folder (the `challenge-6` folder, where the `pom.xml` file is located).
3.  Clean, compile, and run the server using the following Maven command:
    ```bash
    mvn clean compile exec:java
    ```
4.  The server will be active and listening on `http://localhost:8080`.
5.  **Test the Website (Sprint 2):** Open `http://localhost:8080/` in your browser.
6.  **Test the API (Sprint 1):** Open `http://localhost:8080/api/items` to verify the API still works.

### 🖥️ Screenshot: Server Started

Here you can see the terminal confirmation that the server has started successfully:

> ![alt text](image-8.png)
>
> ``

---

## ✅ Sprint 2 Functionality & Tests

Sprint 2 implemented the user-facing website, a new visual design, an offer management system, and exception handling.

### New Homepage (GET /)

The homepage (`index.mustache`) now renders a professional grid of all available items, pulling images and data from the `items.json` file.

> ![alt text](image-9.png)
>
> ``

### Item Detail & Offer Form (GET /:id)

The item detail page (`item.mustache`) features a large image, a detailed description, and a redesigned form for making offers.

> ![alt text](image-10.png)
>
> ``

### Offer Submission & Persistence (POST /:id/offer)

Submitting the form (now including an email) creates a new `Offer` object, which is stored in the `OfferService`. The server then logs the offer and redirects.

> ![alt text](image-11.png)
>
> ``

### Offer List Display

The item detail page now queries the `OfferService` and displays a list of all current offers for that specific item.

> ![alt text](image-12.png)
>
> ``

### Custom Exception Handling (404)

Accessing a non-existent item (e.g., `/fake-item`) throws a `NotFoundException` and renders the custom, styled `404.mustache` template.

> ![alt text](image-13.png)
>
> ``

---

## 🗺️ API Endpoints (Sprint 1)

All API routes are prefixed with `/api` and continue to function alongside the website.

### Items API

| Verb | Route | Description |
| :--- | :--- | :--- |
| `GET` | `/api/items` | Returns a list (ID, name, price) of all items. |
| `GET` | `/api/items/:id/description` | Returns only the description of a specific item. |

### Users API

| Verb | Route | Description |
| :--- | :--- | :--- |
| `GET` | `/api/users` | Returns the list of all users. |
| `GET` | `/api/users/:id` | Returns a specific user by ID. |
| `POST` | `/api/users` | (Industry Standard) Creates a new user. |
| `PUT` | `/api/users/:id` | Updates an existing user. |
| `DELETE` | `/api/users/:id` | Deletes a user by ID. |
| `OPTIONS` | `/api/users/:id` | Checks if a user exists. |

---

## 📁 Updated Project Structure (Sprint 2)

The project structure was expanded to include the web layer (`WebController`), a new `offer` module, exception handling, and resource folders for templates and static files.

# Sprint 3 Updates: Filters & Real-Time WebSockets

This document details the new features implemented during Sprint 3, building upon the foundation of Sprints 1 and 2.

## ✅ Sprint 3 Functionality: Filters & WebSockets

This sprint added dynamic filtering and real-time communication.

### Price Filters (Req 3.1)

The homepage (`index.mustache`) now includes a form to filter items by a price range (min and max). The `WebController` (Java) receives these query parameters (`?minPrice=...`) and filters the list using the `ItemService` before rendering the template.

> ![alt text](image-14.png)
>
> ``

### Real-Time Price Updates (Req 3.2)

Implemented WebSockets for a live "auction" experience. When a user submits a new offer, that amount becomes the new price for the item. The server (using `PriceUpdateWebSocketHandler`) then broadcasts this new price to **all** connected clients viewing that item, updating the price on their screens instantly without a page reload.

> ![alt text](image-15.png)
>![alt text](image-16.png)
> ``

---



# Sprint 1 Update: Backend Unit Testing (JUnit)

This document details the new testing and quality assurance capabilities implemented during **Sprint 1** of the new project plan. The focus of this sprint was to validate the existing Java backend logic from the "Collector's Vault" project.

## 🎯 Sprint 1 Objective

The primary goal was to create a robust suite of unit tests for the Java service layer, ensuring our business logic (filters, offers, price updates) is reliable and preventing future regressions.

* **Technology Added:** JUnit 5, Mockito, and JaCoCo (Maven Plugin).
* **Target Coverage:** 90% code coverage on all tested services.

## 🛠️ What Was Implemented

1.  **Dependency Configuration:** The `pom.xml` was updated to include all necessary dependencies for testing:
    * `junit-jupiter`: The core JUnit 5 testing framework.
    * `mockito-core`: For creating "mock" objects (though not heavily used yet, it's now part of the stack).
    * `jacoco-maven-plugin`: For generating code coverage reports.

2.  **Test Directory:** The standard Maven test directory `src/test/java` was created to mirror the main source code.

3.  **Unit Tests (JUnit):**
    * `OfferServiceTest.java`: Validates that adding new offers and retrieving them by `itemId` works correctly.
    * `ItemServiceTest.java`: This was the most critical test. It validates:
        * **Price Filtering:** Correctly filters items based on `minPrice`, `maxPrice`, and a combination of both.
        * **Edge Cases:** Handles `null` or empty filter parameters gracefully.
        * **Price Updates:** Ensures the `updateItemPrice` method (used by WebSockets) correctly modifies the price in memory.

---

## ✅ Deliverables & Test Results

### JUnit Test Execution

All unit tests for the service layer were run successfully using the `mvn test` command.

> ![alt text](image-17.png)
>
> ``

### Code Coverage Report (JaCoCo)

We successfully generated a code coverage report using `mvn jacoco:report` to verify that our tests meet the 90% coverage goal for the logic in `ItemService` and `OfferService`.

> ![alt text](image-18.png)
>![alt text](image-19.png)
> ``

### Issues & Resolutions Log

* **Issue Found:** `ItemServiceTest` initially failed when testing price filters.
* **Resolution:** The original `Item.java` model had `price` as a `String`. The tests forced us to refactor this (in Sprint 3 of the original plan) to a `double`, which was a critical prerequisite for filtering. The tests in this sprint *validated* that this refactor was successful.
* **Issue Found:** `ItemService`'s `getAllItems` method (which now uses `double`) needed to be adjusted to correctly handle `null` or empty string inputs for `minPrice` and `maxPrice`.
* **Resolution:** Added a helper method `parseDouble()` to the service to safely convert query parameters, preventing `NumberFormatExceptions`.

# Sprint 2 Update: Frontend Refactor & Jest Testing

This document details the work completed for **Sprint 2**, which involved a major architectural refactor (Option 3) and the implementation of a frontend testing suite (Jest).

## 🎯 Sprint 2 Objectives

1.  **Architectural Refactor (Opción 3):** Decouple the Item Detail page from the Java backend, converting it from Server-Side Rendered (SSR) to a Client-Side Rendered (CSR) JavaScript application.
2.  **Frontend Testing (Teresa's Task):** Implement a comprehensive unit test suite for the new JavaScript application using **Jest**.
3.  **Code Coverage:** Achieve at least **90%** test coverage for the new JavaScript module.

---

## 🛠️ What Was Implemented

### 1. Architectural Refactor: Decoupling the Frontend

We successfully refactored the application into a hybrid model:

* **Backend (Java):**
    * `WebController.java` was modified. The `GET /:id` route no longer renders a Mustache template. It now performs a simple **redirect** to the new static `item.html` file.
    * The `POST /:id/offer` route was modified to **return JSON** instead of redirecting, making it a true API endpoint for the JavaScript app.
    * A new `OfferController.java` was created to provide a `GET /api/offers/:itemId` endpoint, allowing the frontend to fetch offers on demand.
* **Frontend (JavaScript):**
    * `item.mustache` is no longer used for the detail page.
    * `item.html` was created in `src/main/resources/public/`. This acts as a static "shell" for the application.
    * `item-detail-app.js` was created. This is the new client-side "brain" that:
        1.  Reads the item ID from the URL.
        2.  Fetches all item and offer data from the `/api/...` endpoints.
        3.  Renders the complete page HTML (item info, form, offer list) into the DOM.
        4.  Handles the `submit` event for the offer form.
        5.  Connects to the WebSocket for real-time price updates.

### 2. Frontend Unit Testing (Jest)

To test our new `item-detail-app.js` module, a complete Jest testing environment was configured.

* **Configuration:**
    * `package.json` was created and configured with `npm install`.
    * `jest`, `jest-environment-jsdom`, `babel-jest`, `@babel/core`, and `@babel/preset-env` were installed as dev dependencies.
    * `babel.config.js` was created to handle modern `import/export` syntax.
* **Mocking (`jest.setup.js`):**
    * Created a global setup file to provide mocks for all browser-native APIs, including `fetch()` and `WebSocket()`.
* **Tests (`item-detail-app.test.js`):**
    * Wrote **17 unit tests** covering all functions in the module.
    * **Pure Functions:** Tested the `renderOfferList` function with empty, null, and populated data.
    * **DOM Rendering:** Tested `renderPage` to ensure all HTML elements are created correctly.
    * **Data Fetching:** Tested `loadItemData`, including mock `fetch` calls for success, 404 item failures, and 404 offer failures.
    * **Event Handling:** Tested `attachFormListener` to simulate form submissions for both success and failure cases.
    * **WebSockets:** Tested `connectWebSocket` to ensure it connects and correctly updates the price on a valid message, while ignoring messages for other items.

---

## ✅ Deliverables & Test Results

### Test Execution & Coverage

All **17 tests passed** successfully. After testing all functions (including `init`), we achieved **94.80% statement coverage** and **100% line coverage**, surpassing the 90% sprint goal.

> ![alt text](image-20.png)
>
> ``

### Technical Difficulties & Resolutions Log

1.  **Issue:** `SyntaxError: Cannot use import statement...`
    * **Difficulty:** Jest (running on Node.js) does not natively understand the `import/export` (ESM) syntax used in our JavaScript module.
    * **Resolution:** Installed `babel-jest` and configured a `babel.config.js` to transpile the code "on-the-fly" into a format Jest understands (CommonJS).

2.  **Issue:** `TypeError: Cannot set properties of null...`
    * **Difficulty:** Tests were failing because functions like `renderPage` tried to find DOM elements (`getElementById`) that didn't exist in Jest's test environment. The global `DOM` object in the app was being set to `null` before tests ran.
    * **Resolution:** Refactored `item-detail-app.js` to remove the global `DOM` object. Functions were updated to find elements (e.g., `document.getElementById`) *when they are called*, not when the script is imported.

3.  **Issue:** Asynchronous tests (like `attachFormListener`) were failing.
    * **Difficulty:** The test would check the result (`expect(...)`) *before* the asynchronous `fetch` call had finished.
    * **Resolution:** Used `await new Promise(resolve => setTimeout(resolve, 0))` after dispatching the event. This forces the test to "wait one tick" for the event loop, allowing the `async` function to complete before the assertions are checked.