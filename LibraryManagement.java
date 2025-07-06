
import java.util.*;

class Book {
    String title, author, isbn;
    boolean borrowed;

    public Book(String title, String author, String isbn) {
        this.title = title; this.author = author; this.isbn = isbn;
        this.borrowed = false;
    }
}

class User {
    String name;
    List<Book> borrowedBooks;

    public User(String name) {
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }
}

public class LibraryManagement {
    static Scanner scanner = new Scanner(System.in);
    static List<Book> books = new ArrayList<>();
    static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        while(true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. List Books");
            System.out.println("3. Add User");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch(choice) {
                case 1: addBook(); break;
                case 2: listBooks(); break;
                case 3: addUser(); break;
                case 4: borrowBook(); break;
                case 5: returnBook(); break;
                case 6: System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void addBook() {
        System.out.print("Enter title: "); String title = scanner.nextLine();
        System.out.print("Enter author: "); String author = scanner.nextLine();
        System.out.print("Enter ISBN: "); String isbn = scanner.nextLine();
        books.add(new Book(title, author, isbn));
        System.out.println("Book added.");
    }

    static void listBooks() {
        System.out.println("\nBooks:");
        for (int i=0; i<books.size(); i++) {
            Book b = books.get(i);
            System.out.printf("%d. %s by %s (ISBN: %s) %s\n", i+1, b.title, b.author, b.isbn, b.borrowed ? "[Borrowed]" : "");
        }
    }

    static void addUser() {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        users.add(new User(name));
        System.out.println("User added.");
    }

    static void borrowBook() {
        listBooks();
        System.out.print("Select book number to borrow: ");
        int bnum = scanner.nextInt(); scanner.nextLine();
        if (bnum < 1 || bnum > books.size()) {
            System.out.println("Invalid book number.");
            return;
        }
        Book book = books.get(bnum-1);
        if (book.borrowed) {
            System.out.println("Book already borrowed.");
            return;
        }
        System.out.print("Enter user name: ");
        String uname = scanner.nextLine();
        User user = null;
        for (User u : users) {
            if (u.name.equalsIgnoreCase(uname)) { user = u; break; }
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        book.borrowed = true;
        user.borrowedBooks.add(book);
        System.out.println("Book borrowed.");
    }

    static void returnBook() {
        System.out.print("Enter user name: ");
        String uname = scanner.nextLine();
        User user = null;
        for (User u : users) {
            if (u.name.equalsIgnoreCase(uname)) { user = u; break; }
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (user.borrowedBooks.isEmpty()) {
            System.out.println("No borrowed books.");
            return;
        }
        System.out.println("Borrowed books:");
        for (int i=0; i<user.borrowedBooks.size(); i++) {
            Book b = user.borrowedBooks.get(i);
            System.out.printf("%d. %s by %s (ISBN: %s)\n", i+1, b.title, b.author, b.isbn);
        }
        System.out.print("Select book number to return: ");
        int bnum = scanner.nextInt(); scanner.nextLine();
        if (bnum < 1 || bnum > user.borrowedBooks.size()) {
            System.out.println("Invalid book number.");
            return;
        }
        Book book = user.borrowedBooks.remove(bnum-1);
        book.borrowed = false;
        System.out.println("Book returned.");
    }
}
