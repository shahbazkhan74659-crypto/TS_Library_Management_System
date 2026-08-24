# TS Library

A server-rendered Library Management System built with Spring Boot, Thymeleaf, and MySQL. It lets library staff manage books and members, issue books to members, and process returns, with a dashboard summarizing key stats.

## Features

- **Dashboard** — total books, available books, issued books, and total members at a glance.
- **Book management** — add, list, and delete books, with per-title quantity and available-quantity tracking.
- **Member management** — add, list, and delete members.
- **Issue books** — issue an available book to a member with a due date; available quantity is decremented automatically.
- **Return books** — return an issued book, which records it in return history and restores available quantity.
- **About page** — static info page.

## Tech Stack

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Thymeleaf)
- MySQL (via `mysql-connector-j`)
- Lombok
- Maven (with Maven Wrapper)

## Project Structure

```
src/main/java/com/library/
├── LibrarymanagementApplication.java        # Spring Boot entry point
├── entity/                                  # JPA entities: Book, Member, IssuedBook, ReturnedBook
├── repository/                              # Spring Data JPA repositories
└── librarymanagement/controller/
    └── HomeController.java                  # All web routes (dashboard, books, members, issue/return)

src/main/resources/
├── application.properties                   # Datasource & JPA config
├── static/                                  # CSS, JS, fonts, images
└── templates/                               # Thymeleaf views (dashboard, books, members, etc.)
```

## Data Model

| Entity         | Table            | Key fields |
|----------------|------------------|------------|
| `Book`         | `books`          | title, author, category, publishedDate, quantity, availableQuantity |
| `Member`       | `members`        | name, email, mobile, address, joinedDate |
| `IssuedBook`   | `issued_books`   | bookId, bookName, memberId, memberName, issueDate, dueDate |
| `ReturnedBook` | `returned_books` | bookId, bookName, memberId, memberName, issueDate, returnDate |

`spring.jpa.hibernate.ddl-auto=update` is set, so tables are created/updated automatically on startup — no manual schema migration is required.

## Prerequisites

- JDK 21+
- MySQL Server (running locally or reachable)
- Maven (or use the bundled `mvnw` / `mvnw.cmd` wrapper — no local Maven install needed)

## Setup

1. **Create the database**

   ```sql
   CREATE DATABASE library_db;
   ```

2. **Configure credentials**

   The app reads DB credentials from environment variables (`DB_USERNAME`, `DB_PASSWORD`), referenced in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/library_db
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   ```

   Set them before running the app, e.g. on Windows (PowerShell):

   ```powershell
   $env:DB_USERNAME = "your_mysql_user"
   $env:DB_PASSWORD = "your_mysql_password"
   ```

   or on macOS/Linux:

   ```bash
   export DB_USERNAME=your_mysql_user
   export DB_PASSWORD=your_mysql_password
   ```

3. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

   (On Windows: `mvnw.cmd spring-boot:run`)

4. **Open the app**

   Visit [http://localhost:8080](http://localhost:8080) in your browser.

## Building

```bash
./mvnw clean package
java -jar target/librarymanagement-0.0.1-SNAPSHOT.jar
```

## Routes

| Method | Path                | Description                        |
|--------|---------------------|-------------------------------------|
| GET    | `/`                 | Dashboard with stats                |
| GET    | `/books`            | List all books                      |
| GET    | `/addbooks`         | Add-book form                       |
| POST   | `/addbooks`         | Save a new book                     |
| GET    | `/deletebook/{id}`  | Delete a book                       |
| GET    | `/members`          | List all members                    |
| GET    | `/addmembers`       | Add-member form                     |
| POST   | `/addmembers`       | Save a new member                   |
| GET    | `/deletemember/{id}`| Delete a member                     |
| GET    | `/issuebook/{id}`   | Issue-book form for a given book    |
| POST   | `/issuebook`        | Issue a book to a member            |
| GET    | `/returnbooks`      | List returned books                 |
| GET    | `/returnbook/{id}`  | Return an issued book               |
| GET    | `/about`            | About page                          |

## Testing

```bash
./mvnw test
```

## License

No license has been specified for this project.
