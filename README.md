# 📚 LibraryDesk

A full-stack library management system built with **Spring Boot**, **Core Java**, and a **HTML/CSS/JavaScript** frontend. LibraryDesk lets you manage books and students, issue and return books, and automatically calculates late fees — all backed by JSON file persistence.

---

## Features

- **Book management** — add books, prevent duplicate ISBNs, view all books
- **Student management** — add students, prevent duplicate student IDs
- **Issue books** — tracks issue date, due date, and blocks issuing when no copies are available
- **Return books** — automatically calculates late fees based on days overdue
- **Search** — filter books by title/author/ISBN and students by ID/name
- **Data persistence** — all data is saved to `library.json` and survives application restarts
- **Input validation** — both frontend and backend validate required fields, negative values, and duplicates
- **Clean error handling** — a global exception handler returns readable JSON error messages instead of raw stack traces
- **Automated tests** — 15 JUnit 5 tests covering core logic, validation, and persistence

---

## Technologies Used

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Build tool | Maven |
| Backend framework | Spring Boot |
| Persistence | Jackson (JSON serialization) → `library.json` |
| Testing | JUnit 5 |
| Frontend | HTML, CSS, JavaScript (vanilla, no framework) |

---

## Project Structure

```
LibraryDesk
├── src
│   ├── main
│   │   ├── java/com/librarydesk
│   │   │   ├── model
│   │   │   │   ├── Book.java
│   │   │   │   ├── Student.java
│   │   │   │   └── IssueRecord.java
│   │   │   ├── Library.java
│   │   │   ├── LibraryService.java
│   │   │   ├── LibraryRepository.java
│   │   │   ├── LibraryController.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── LibraryDeskApplication.java
│   │   └── resources
│   │       └── static
│   │           ├── index.html
│   │           ├── style.css
│   │           └── script.js
│   └── test
│       └── java/com/librarydesk
│           └── LibraryTest.java
├── library.json          (created automatically on first run)
└── pom.xml
```

**Architecture flow:**

```
Frontend (HTML/CSS/JS)
        ↓
REST Controller  (LibraryController)
        ↓
Service Layer    (LibraryService)
        ↓
Repository       (LibraryRepository)
        ↓
library.json
```

---

## How to Run

**Prerequisites:** Java 25, Maven, an IDE (IntelliJ recommended)

1. Clone the repository
   ```bash
   git clone <your-repo-url>
   cd LibraryDesk
   ```

2. Run the application
   - In IntelliJ: open the project and run `LibraryDeskApplication`
   - Or via Maven:
     ```bash
     mvn spring-boot:run
     ```

3. Open your browser and go to:
   ```
   http://localhost:8080
   ```

The application will automatically create `library.json` on first run if it doesn't already exist.

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/books` | View all books |
| `POST` | `/api/books` | Add a new book |
| `GET` | `/api/students` | View all students |
| `POST` | `/api/students` | Add a new student |
| `GET` | `/api/issues` | View all active issue records |
| `POST` | `/api/issue?isbn={isbn}&studentId={id}` | Issue a book to a student |
| `POST` | `/api/return?isbn={isbn}&studentId={id}` | Return a book and calculate late fee |

**Example — Add a book:**
```http
POST /api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "ISBN003",
  "copiesAvailable": 2
}
```

**Example — Issue a book:**
```http
POST /api/issue?isbn=ISBN001&studentId=S001
```

**Example error response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "No copies available for this book."
}
```

---

## Running Tests

```bash
mvn test
```

Or right-click `LibraryTest.java` in IntelliJ and select **Run**.

Tests use a separate `test-library.json` file, so running tests never affects your real application data.

**Test coverage includes:**
- Adding books and students
- Duplicate ISBN and duplicate student ID prevention
- Issuing and returning books
- Blocking issues when no copies are available
- Late fee calculation
- Input validation (empty fields, negative values)
- JSON save/load persistence

---

## Screenshots

*(Add screenshots of the Books, Students, Issue Book, Return Book, and Issue Records pages here before submission.)*

---

## Author

Built as part of the LibraryDesk internship project.
