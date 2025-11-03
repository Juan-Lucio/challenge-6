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

## 📁 Updated Project Structure (Sprint 3)

The project structure was expanded to include the new `websocket` module and key files were modified.