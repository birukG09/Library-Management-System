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



