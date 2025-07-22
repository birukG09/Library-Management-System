# 📚 Java Library Management System (OOP + SQLite)

A feature-rich, modular, and Object-Oriented Library Management System built in Java with full SQLite database integration. This application helps manage books, members, and borrowing records in a simple and efficient way — ideal for schools, colleges, or personal libraries.

---

## ✨ Key Features

### 📗 Book Management
- Add, update, delete, and search books  
- Auto-generate unique book IDs  
- Track book availability  

### 👥 Member Management
- Register, update, and delete members  
- View member borrowing history  

### ⏳ Borrowing & Returning System
- Borrow and return books  
- Automatically sets borrow and return dates  
- Prevents borrowing of already borrowed books  

### 🔐 Secure & Persistent
- Uses SQLite for persistent local storage  
- Data is safely stored in `library.db`  

### 💻 Clean OOP Design
- Follows SOLID principles  
- Structured into model, DAO, service, and UI layers  

---

## 📁 Project Structure

LibraryManagementSystem/
│
├── src/
│ └── com/library/
│ ├── model/ # Book.java, Member.java, BorrowRecord.java
│ ├── dao/ # BookDAO.java, MemberDAO.java, BorrowDAO.java
│ ├── service/ # BookService.java, MemberService.java, BorrowService.java
│ ├── util/ # SQLiteConnection.java, ConsoleHelper.java
│ └── ui/ # LibraryApp.java, Menu.java
│
├── resources/
│ └── library.db # SQLite DB file (created automatically
│
├── README.md
└── LICENSE
---

## ⚙️ Technologies Used

| Component       | Technology                 |
|-----------------|----------------------------|
| Language        | Java (JDK 8 or higher)     |
| Database        | SQLite (file-based)        |
| DB Connectivity | JDBC                       |
| Build Tool      | Manual / IDE               |
| UI              | Console-based CLI          |
| Design Pattern  | OOP (SOLID principles)     |

---

## 🧠 System Architecture

User (Console)  
↓  
UI Layer (`LibraryApp.java`)  
↓  
Service Layer (business logic)  
↓  
DAO Layer (JDBC + SQLite)  
↓  
Database (`library.db`)

---

## 🧪 Getting Started

### ✅ Prerequisites

- Java JDK 8 or above  
- Any Java IDE (IntelliJ, Eclipse, etc.) or command-line tools

### 🔧 Setup Instructions

1. Clone the repository

```bash
git clone https://github.com/birukG09/Library-Management-System.git
cd Library-Management-System
javac -d bin src/com/library/**/*.java
Database Schema (SQLite)
The database tables will be created automatically if not present. Below are the example SQL definitions CREATE TABLE books (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  author TEXT NOT NULL,
  isbn TEXT UNIQUE,
  available INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE members (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT UNIQUE
);

CREATE TABLE borrow_records (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  member_id INTEGER,
  book_id INTEGER,
  borrow_date TEXT,
  return_date TEXT,
  FOREIGN KEY(member_id) REFERENCES members(id),
  FOREIGN KEY(book_id) REFERENCES books(id)
);
Example Code Snippets
java
Copy
Edit
Book book = new Book("Clean Code", "Robert Martin", "9780132350884");
bookService.addBook(book);

borrowService.returnBook(borrowRecordId); Future Improvements
JavaFX GUI interface for better user experience

Export reports as PDF or Excel

Search by genre, year, or category

Email/SMS reminders for due books

Role-based user login system

Web version using Spring Boot
