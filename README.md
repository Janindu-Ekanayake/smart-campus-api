# Smart Campus Sensor & Room Management API

## Overview

This project is a RESTful API developed for the **5COSC022W Client-Server Architectures coursework**. It models a simple smart campus environment in which:

- rooms can be created, listed, retrieved, and deleted
- sensors can be registered and linked to rooms
- sensor readings can be stored and retrieved
- sensors can be filtered by type
- errors are returned as structured JSON responses
- incoming requests and outgoing responses are logged

The system is implemented using **JAX-RS (Jersey 2.x)** and stores all data **in memory** using Java collections. No database is used — all data resets on server restart.

## Technology Stack

- Java 11+
- JAX-RS / Jersey **2.41** (javax.* namespace)
- Maven
- **Apache Tomcat 9.0.x** (Servlet 4.0 / javax.servlet)
- NetBeans IDE

> ⚠️ **Tomcat Version Note:** This project uses **Jersey 2.x** with the `javax.*` namespace.  
> It is compatible with **Tomcat 9.x** (Servlet 4.0).  
> Do **NOT** use Tomcat 10.x or higher — those require the `jakarta.*` namespace (Jersey 3.x).

## Base URL

```text
http://localhost:8080/smart-campus-api/api/v1/
```

> The context path `/smart-campus-api` is set in `src/main/webapp/META-INF/context.xml`.

## Project Architecture

The project uses a **gateway-domain-rest layered structure** split into four top-level packages:

- `boot/` — JAX-RS application bootstrap and entry point
- `domain/` — pure business logic: entities, custom exceptions (`fault/`), and exception mappers (`handler/`)
- `gateway/` — technical infrastructure: in-memory persistence (`store/`) and HTTP logging (`http/`)
- `rest/` — REST endpoint resource classes

## Project Structure

```text
smart-campus-api/
├── pom.xml
├── README.md
├── nb-configuration.xml
├── src/
│   └── main/
│       ├── java/com/janindu/smart/campus/
│       │   ├── boot/
│       │   │   └── RestConfig.java
│       │   ├── domain/
│       │   │   ├── entity/
│       │   │   │   ├── Room.java
│       │   │   │   ├── Sensor.java
│       │   │   │   └── SensorReading.java
│       │   │   ├── fault/
│       │   │   │   ├── LinkedNotFoundException.java
│       │   │   │   ├── RoomNotEmptyException.java
│       │   │   │   └── SensorUnavailableException.java
│       │   │   └── handler/
│       │   │       ├── GlobalSafetyNet.java
│       │   │       ├── LinkedNotFoundHandler.java
│       │   │       ├── RoomNotEmptyHandler.java
│       │   │       └── SensorUnavailableHandler.java
│       │   ├── gateway/
│       │   │   ├── store/
│       │   │   │   └── DataStore.java
│       │   │   └── http/
│       │   │       └── LoggingFilter.java
│       │   └── rest/
│       │       ├── DiscoveryResource.java
│       │       ├── RoomResource.java
│       │       ├── SensorResource.java
│       │       └── SensorReadingResource.java
│       └── webapp/
│           ├── index.html
│           ├── META-INF/
│           │   └── context.xml
│           └── WEB-INF/
│               ├── beans.xml
│               └── web.xml
```

## API Design Summary

### Base Path

```text
http://localhost:8080/smart-campus-api/api/v1/
```

### Resources

- `GET  /api/v1/` — discovery endpoint with version and HATEOAS links
- `GET  /api/v1/rooms` — list all rooms
- `POST /api/v1/rooms` — create a room
- `GET  /api/v1/rooms/{id}` — fetch one room
- `DELETE /api/v1/rooms/{id}` — delete a room if no sensors remain assigned
- `GET  /api/v1/sensors` — list all sensors
- `GET  /api/v1/sensors?type=CO2` — filter sensors by type
- `POST /api/v1/sensors` — create a new sensor linked to a room
- `GET  /api/v1/sensors/{sensorId}/readings` — get readings history for a sensor
- `POST /api/v1/sensors/{sensorId}/readings` — append a new reading and update sensor's current value

## How to Build and Run

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- **Apache Tomcat 9.0.x** (NOT Tomcat 10+)

### Option 1: NetBeans + Tomcat 9

1. Open the project in **NetBeans**.
2. Add **Tomcat 9.0.x** as the server in NetBeans (Tools → Servers → Add Server).
3. Right-click project → **Clean and Build**.
4. Right-click project → **Run** (deploys to Tomcat automatically).
5. Open browser: `http://localhost:8080/smart-campus-api/api/v1/`

### Option 2: Maven CLI + Manual Deploy

```bash
git clone https://github.com/Janindu-Ekanayake/smart-campus-api.git
cd smart-campus-api
mvn clean package
```

Copy the WAR to Tomcat's webapps folder:

```bash
copy target\smart-campus-api-1.0-SNAPSHOT.war C:\path\to\apache-tomcat-9.0.117\webapps\
```

Start Tomcat:

```bash
C:\path\to\apache-tomcat-9.0.117\bin\startup.bat
```

Then open: `http://localhost:8080/smart-campus-api/api/v1/`

## Sample curl Commands

### 1. Discovery Endpoint

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/
```

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"R1\",\"name\":\"Lab A\",\"capacity\":30}"
```

### 3. List All Rooms

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 4. Get a Room by ID

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms/R1
```

### 5. Create a Sensor

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"S1\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.5,\"roomId\":\"R1\"}"
```

### 6. Filter Sensors by Type

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature"
```

### 7. Add a Sensor Reading

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/S1/readings ^
  -H "Content-Type: application/json" ^
  -d "{\"value\":24.8}"
```

### 8. Get Reading History

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/S1/readings
```

### 9. Delete a Room (will fail if sensors exist)

```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/R1
```

## Data Storage Strategy

Thread-safe in-memory collections in `DataStore` under `gateway/store/`:

- `ConcurrentHashMap<String, Room> rooms`
- `ConcurrentHashMap<String, Sensor> sensors`
- `ConcurrentHashMap<String, List<SensorReading>> sensorReadings`

A singleton pattern ensures a single shared instance across all requests.

## Error Handling Strategy

Custom exception classes live in `domain/fault/` and their JAX-RS mappers live in `domain/handler/`.

| HTTP Status | Scenario |
|---|---|
| 409 Conflict | Deleting a room that still contains sensors |
| 422 Unprocessable Entity | Creating a sensor with a non-existent roomId |
| 403 Forbidden | Posting a reading to a sensor in MAINTENANCE |
| 500 Internal Server Error | Unhandled runtime failures (GlobalSafetyNet) |

### Example Error JSON

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Room R1 cannot be deleted because it still contains sensors."
}
```

## Logging

`gateway/http/LoggingFilter.java` captures:

- HTTP method and request URI for every incoming request
- Final HTTP status code for every outgoing response

## Limitations

- No persistence — all data resets on server restart.
- Plain `ConcurrentHashMap` is thread-safe but not transactional.

---

# Conceptual Report

## Q1 — What is the default lifecycle of a JAX-RS resource class, and how does it affect in-memory data management?

By default, JAX-RS creates a **new instance of the resource class for every request**. Each request gets a fresh object, which avoids shared state inside the class. Our data lives in the `DataStore` singleton under `gateway/store/`, which is shared across all requests. Using `ConcurrentHashMap` and `CopyOnWriteArrayList` prevents race conditions on concurrent reads and writes without explicit locking.

---

## Q2 — Why is Hypermedia (HATEOAS) considered important in RESTful design?

Hypermedia means the API response tells the client **what it can do next** by including links. Instead of the client hardcoding every URL from external docs, the server guides navigation. This makes the API self-describing and reduces tight coupling between client and server. Our `DiscoveryResource` at `GET /api/v1/` implements a basic version of this by returning `_links` with the main resource paths.

---

## Q3 — What are the implications of returning only IDs vs full room objects in a list response?

Returning **only IDs** keeps responses small but forces the client to make extra requests for details, increasing latency. Returning **full objects** is more convenient — everything arrives in one response — but the payload is larger. For this coursework the dataset is small so returning full room objects is the better choice. For large systems you would use pagination or sparse fieldsets.

---

## Q4 — Is the DELETE operation idempotent in this implementation?

Yes. The first successful `DELETE /rooms/R1` removes the room. A second identical request gets `404 Not Found` because the room is already gone. The server state does not keep changing after the first deletion, so it is idempotent. When the delete is blocked because sensors still exist, repeating the request gives the same `409 Conflict` every time — the state never changes, which is also idempotent behaviour.

---

## Q5 — What happens technically when a client sends data in the wrong format to a @Consumes(APPLICATION_JSON) endpoint?

JAX-RS checks the `Content-Type` header before calling the resource method. If it does not match `application/json`, the framework immediately rejects the request with **HTTP 415 Unsupported Media Type**. The method code never runs. If the JSON body itself is malformed, that gives a `400 Bad Request` during body parsing. So `@Consumes` acts as an early contract check at the framework level.

---

## Q6 — Why is @QueryParam better than a path segment for filtering sensors by type?

The path `/api/v1/sensors` represents the **sensors collection**. Adding `?type=CO2` is a filter on that same collection — it does not identify a different resource. Using `/sensors/type/CO2` implies "type" is a sub-resource, which is conceptually wrong. Query parameters are also composable: `?type=CO2&status=ACTIVE` works naturally. They keep the base URI clean and are the standard RESTful way to express optional search and filter criteria.

---

## Q7 — What are the architectural benefits of the Sub-Resource Locator pattern?

Instead of putting all nested endpoints into one large class, the sub-resource locator in `SensorResource` delegates `/sensors/{id}/readings` to a dedicated `SensorReadingResource` class in `rest/`. This gives **separation of concerns** — sensor logic and reading logic stay in different files. Each class is smaller and easier to maintain. Testing can be done in isolation. Adding deeper nesting later is also cleaner than extending a single monolithic resource class.

---

## Q8 — Why should a POST to a reading also update the parent sensor's currentValue?

Because the two values represent the same real-world fact from different angles. If `POST /sensors/S1/readings` adds a new measurement but `GET /sensors/S1` still shows the old `currentValue`, the API is **internally inconsistent**. A client using different endpoints would get contradictory data. Updating `currentValue` on every successful reading POST keeps the system consistent — the sensor always reflects its latest known state, and the readings history shows the full timeline.

---

## Q9 — Why is HTTP 422 more semantically accurate than 404 when a referenced roomId doesn't exist?

`404 Not Found` means the **requested URL does not exist**. But here `POST /sensors` is a perfectly valid URL — the problem is inside the JSON body where `roomId` references something that does not exist. The server understood the request and its format but cannot process it because of an invalid data reference. `422 Unprocessable Entity` means exactly that: the syntax is fine but the content is semantically wrong. Using 404 here would be misleading to the client.

---

## Q10 — What are the cybersecurity risks of exposing Java stack traces to API clients?

A raw stack trace leaks internal details: **class names, package structure, method names, line numbers, framework versions, and file paths**. An attacker can use this to fingerprint the tech stack, identify outdated libraries with known CVEs, and understand internal code flow to craft targeted exploits. The `GlobalSafetyNet` handler in `domain/handler/` intercepts all unhandled `Throwable` instances and returns only a generic `500 Internal Server Error` message, keeping the full trace server-side only.
