# LibraryDesk — Architecture

This document explains how LibraryDesk is structured internally: the layers, the responsibility of each one, and how a request flows through the system from the browser to the data file and back.

---

## High-Level Overview

LibraryDesk follows a classic **layered architecture**. Each layer has one job and only talks to the layer directly below it.

```
Frontend (HTML + CSS + JavaScript)
            ↓  HTTP requests (fetch)
REST Controller   (LibraryController)
            ↓  Java method calls
Service Layer     (LibraryService)
            ↓  Java method calls
Repository        (LibraryRepository)
            ↓  Jackson (ObjectMapper)
library.json
```

There is **no traditional database** (e.g. MySQL) in this project. Persistence is handled entirely through a JSON file (`library.json`), using Jackson to serialize and deserialize Java objects. This is intentional — the project's requirement is file-based JSON persistence, not a relational database.

---

## Layer-by-Layer Breakdown

### 1. Frontend — `index.html`, `style.css`, `script.js`

- Plain HTML/CSS/JavaScript, no frameworks
- Served directly by Spring Boot from `src/main/resources/static`, so the whole app runs from a single server at `http://localhost:8080`
- Communicates with the backend exclusively through `fetch()` calls to the REST API (`/api/...`)
- Responsible for:
  - Rendering data (books, students, issue records) received from the backend
  - Client-side input validation (empty fields, negative numbers) before sending requests
  - Displaying success/error messages returned by the backend

The frontend never touches `library.json` or any Java code directly — it only ever talks to the REST API.

### 2. REST Controller — `LibraryController.java`

- Annotated with `@RestController` and `@RequestMapping("/api")`
- Defines all HTTP endpoints (`GET`/`POST` for books, students, issue, return, issues)
- Responsible for:
  - Parsing incoming HTTP requests (`@RequestParam`, `@RequestBody`)
  - Looking up the relevant `Book`/`Student` objects
  - Delegating actual business logic to `LibraryService`
  - Returning HTTP responses (plain text or JSON)

The controller contains **no business logic** itself — it's a thin layer that translates HTTP requests into service method calls.

### 3. Global Exception Handler — `GlobalExceptionHandler.java`

- Annotated with `@RestControllerAdvice`
- Catches exceptions thrown anywhere in the service layer (`IllegalArgumentException`, `IllegalStateException`)
- Converts them into clean, structured JSON error responses with a proper HTTP status code (`400 Bad Request`) instead of a raw `500 Internal Server Error` stack trace

Example response:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "No copies available for this book."
}
```

### 4. Service Layer — `LibraryService.java`

This is the core of the application — all business rules live here.

Responsibilities:
- **Validation** — empty titles, empty names, negative copy counts, duplicate ISBNs, duplicate student IDs
- **Issuing books** — checks copy availability, decrements `copiesAvailable`, calculates the due date using `LocalDate` and the configured loan period, creates an `IssueRecord`
- **Returning books** — finds the matching issue record, increments `copiesAvailable`, calculates late fees using `ChronoUnit.DAYS.between()` against the configured `lateFeePerDay`, removes the issue record
- **Persistence trigger** — after every state-changing operation (add book, add student, issue, return), it calls `saveLibrary()` so changes are immediately written to disk

`LibraryService` holds the in-memory `Library` object (the current state) and a reference to a `LibraryRepository` for saving/loading.

### 5. Repository — `LibraryRepository.java`

- The only layer that knows about the actual file system
- Uses Jackson's `ObjectMapper` (with the `JavaTimeModule` registered, so `LocalDate` fields serialize correctly) to:
  - `save(Library library)` — write the entire `Library` object to a JSON file
  - `load()` — read a JSON file back into a `Library` object
- Accepts a configurable file path via its constructor. The real application uses the default `"library.json"`; JUnit tests use a separate `"test-library.json"` so test runs never overwrite real application data

### 6. Data Models — `com.librarydesk.model`

Plain Java objects (POJOs) representing the domain:

- **`Book`** — title, author, isbn, copiesAvailable
- **`Student`** — studentId, studentName
- **`IssueRecord`** — links a `Book` and a `Student`, plus `issueDate` and `dueDate` (both `LocalDate`)
- **`Library`** — the top-level container holding lists of all books, students, and issue records, plus configuration (`lateFeePerDay`, `loanPeriodDays`)

These are the objects that get serialized directly to and from `library.json`.

---

## Request Flow Example: Issuing a Book

To make the layering concrete, here's exactly what happens when a user clicks "Issue Book" in the browser:

1. **Frontend** (`script.js`) sends `POST /api/issue?isbn=ISBN001&studentId=S001`
2. **Controller** (`LibraryController`) receives the request, looks up the `Book` and `Student` objects from the current in-memory lists, and calls `libraryService.issueBook(book, student)`
3. **Service** (`LibraryService.issueBook`):
   - Checks `copiesAvailable > 0` (throws `IllegalStateException` if not — caught by the global exception handler and returned as a clean `400`)
   - Decrements `copiesAvailable`
   - Calculates `dueDate = issueDate.plusDays(loanPeriodDays)`
   - Creates a new `IssueRecord` and adds it to the library's issue record list
   - Calls `saveLibrary()`
4. **Repository** (`LibraryRepository.save`) serializes the entire `Library` object (books, students, issue records, settings) to `library.json` via Jackson
5. **Controller** returns `"Book issued successfully"` as the HTTP response
6. **Frontend** displays the success message and can re-fetch `/api/books` and `/api/issues` to show the updated state

The same flow applies to returning a book, adding a book, and adding a student — request in through the controller, business logic and validation in the service, persistence through the repository.

---

## Why This Architecture?

- **Separation of concerns** — each layer can be understood, tested, and changed independently. The frontend doesn't need to know how data is stored; the repository doesn't need to know any business rules.
- **Testability** — because `LibraryService` and `LibraryRepository` accept a configurable file path, the JUnit test suite can exercise the exact same code paths as the real application without ever touching real data.
- **Simplicity for the requirements** — the task calls for JSON file persistence rather than a relational database, so introducing Spring Data JPA or MySQL would add complexity without meeting the actual requirement.

---

## Known Limitations / Possible Future Improvements

- `library.json` is a single flat file — fine for a small dataset, but would not scale to a large number of records or concurrent writers
- No authentication/authorization — anyone with access to the API can add/issue/return
- No pagination on `GET /api/books` or `GET /api/students` — acceptable for the current dataset size
- Book/Student lookups in the controller use linear search (`stream().filter()`) — fine at this scale, would need indexing at larger scale
