import java.util.*;

/**
 * Main.java  –  Library Management System
 * CLI entry point with a two-level menu system.
 *
 * Main Menu
 * ├── 1. Book Management   → Add / View / Search / Remove books
 * ├── 2. User Management   → Register / View / Search / Remove users
 * ├── 3. Issue Book        → Lend a book to a user
 * ├── 4. Return Book       → Accept a book return (+ fine display)
 * ├── 5. View Transactions → All / Active / Overdue records
 * ├── 6. Reports           → Stats, top books, overdue summary
 * └── 0. Exit
 */
public class Main {

    private static final Library library = new Library("City Central Library");
    private static final Scanner sc      = new Scanner(System.in);

    // ═════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ═════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        printBanner();
        seedData();   // pre-load demo books and users

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> userMenu();
                case 3 -> issueBookFlow();
                case 4 -> returnBookFlow();
                case 5 -> transactionMenu();
                case 6 -> reportsMenu();
                case 0 -> {
                    println(ConsoleColors.GREEN, "\n  Thank you for using " + library.getLibraryName() + ". Goodbye!\n");
                    running = false;
                }
                default -> println(ConsoleColors.RED, "  ✗ Invalid option. Choose 0–6.");
            }
        }
        sc.close();
    }

    // ═════════════════════════════════════════════════════════════
    //  SUB-MENUS
    // ═════════════════════════════════════════════════════════════

    static void bookMenu() {
        while (true) {
            header("BOOK MANAGEMENT");
            System.out.println(ConsoleColors.BLUE + "  1. Add Book         3. Search Books" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  2. View All Books   4. Remove Book" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  0. Back to Main Menu" + ConsoleColors.RESET);
            int c = readInt("  Choice: ");
            switch (c) {
                case 1 -> addBookFlow();
                case 2 -> viewAllBooks();
                case 3 -> searchBooksFlow();
                case 4 -> removeBookFlow();
                case 0 -> { return; }
                default -> println(ConsoleColors.RED, "  ✗ Invalid option.");
            }
        }
    }

    static void userMenu() {
        while (true) {
            header("USER MANAGEMENT");
            System.out.println(ConsoleColors.BLUE + "  1. Register User     3. Search Users" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  2. View All Users    4. Remove User" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  5. View User Profile" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  0. Back to Main Menu" + ConsoleColors.RESET);
            int c = readInt("  Choice: ");
            switch (c) {
                case 1 -> registerUserFlow();
                case 2 -> viewAllUsers();
                case 3 -> searchUsersFlow();
                case 4 -> removeUserFlow();
                case 5 -> userProfileFlow();
                case 0 -> { return; }
                default -> println(ConsoleColors.RED, "  ✗ Invalid option.");
            }
        }
    }

    static void transactionMenu() {
        while (true) {
            header("TRANSACTION RECORDS");
            System.out.println(ConsoleColors.BLUE + "  1. All Transactions" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  2. Active Issues (not yet returned)" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  3. Overdue Issues" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "  0. Back to Main Menu" + ConsoleColors.RESET);
            int c = readInt("  Choice: ");
            switch (c) {
                case 1 -> printRecords(library.getAllRecords(),      "ALL TRANSACTIONS");
                case 2 -> printRecords(library.getActiveRecords(),   "ACTIVE ISSUES");
                case 3 -> printRecords(library.getOverdueRecords(),  "OVERDUE ISSUES");
                case 0 -> { return; }
                default -> println(ConsoleColors.RED, "  ✗ Invalid option.");
            }
        }
    }

    static void reportsMenu() {
        header("LIBRARY REPORTS");
        System.out.println();
        System.out.printf("  %-28s %s%d%s%n", "Total Books in Catalogue:", ConsoleColors.BOLD, library.getTotalBooks(), ConsoleColors.RESET);
        System.out.printf("  %-28s %s%d%s%n", "Total Registered Users:",   ConsoleColors.BOLD, library.getTotalUsers(), ConsoleColors.RESET);
        System.out.printf("  %-28s %s%d%s%n", "Currently Issued Copies:",  ConsoleColors.BOLD, library.getTotalIssued(), ConsoleColors.RESET);
        System.out.printf("  %-28s %s%d%s%n", "Overdue Borrows:",
                ConsoleColors.RED + ConsoleColors.BOLD, library.getOverdueRecords().size(), ConsoleColors.RESET);
        System.out.printf("  %-28s %s%d%s%n", "Total Transactions:",
                ConsoleColors.BOLD, library.getAllRecords().size(), ConsoleColors.RESET);

        System.out.println();
        System.out.println(ConsoleColors.BOLD + "  TOP 5 MOST BORROWED BOOKS" + ConsoleColors.RESET);
        System.out.println("  " + "─".repeat(50));
        List<Book> top = library.getTopBorrowedBooks(5);
        if (top.isEmpty()) {
            println(ConsoleColors.YELLOW, "  No borrow history yet.");
        } else {
            for (int i = 0; i < top.size(); i++) {
                Book b = top.get(i);
                System.out.printf("  %d. %-30s  (issued %dx)%n",
                        i + 1, b.getTitle(), b.getIssuedCount());
            }
        }
        pressEnter();
    }

    // ═════════════════════════════════════════════════════════════
    //  BOOK FLOWS
    // ═════════════════════════════════════════════════════════════

    static void addBookFlow() {
        header("ADD BOOK");
        String id     = readString("  Book ID (e.g. B010)  : ").toUpperCase();
        String title  = readString("  Title                : ");
        String author = readString("  Author               : ");
        String genre  = readString("  Genre                : ");
        int    copies = readPositiveInt("  Number of copies     : ");

        try {
            boolean ok = library.addBook(new Book(id, title, author, genre, copies));
            if (ok) System.out.println(ConsoleColors.success("Book added: " + title));
            else    System.out.println(ConsoleColors.error("Book ID " + id + " already exists."));
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    static void viewAllBooks() {
        header("ALL BOOKS (" + library.getTotalBooks() + ")");
        if (library.getAllBooks().isEmpty()) {
            println(ConsoleColors.YELLOW, "  No books in catalogue.");
            pressEnter(); return;
        }
        printBookTableHeader();
        library.getAllBooks().forEach(b -> System.out.println("  " + b.toTableRow()));
        printBookTableFooter();
        pressEnter();
    }

    static void searchBooksFlow() {
        header("SEARCH BOOKS");
        String q = readString("  Enter title / author / genre: ");
        List<Book> results = library.searchBooks(q);
        if (results.isEmpty()) {
            println(ConsoleColors.YELLOW, "  No books found matching \"" + q + "\".");
        } else {
            println(ConsoleColors.GREEN, "  Found " + results.size() + " result(s):");
            printBookTableHeader();
            results.forEach(b -> System.out.println("  " + b.toTableRow()));
            printBookTableFooter();
        }
        pressEnter();
    }

    static void removeBookFlow() {
        header("REMOVE BOOK");
        viewAllBooks();
        String id = readString("  Enter Book ID to remove: ").toUpperCase();
        try {
            boolean ok = library.removeBook(id);
            if (ok) System.out.println(ConsoleColors.success("Book " + id + " removed."));
            else    System.out.println(ConsoleColors.error("Book ID " + id + " not found."));
        } catch (IllegalStateException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    // ═════════════════════════════════════════════════════════════
    //  USER FLOWS
    // ═════════════════════════════════════════════════════════════

    static void registerUserFlow() {
        header("REGISTER USER");
        String id    = readString("  User ID (e.g. U005)   : ").toUpperCase();
        String name  = readString("  Full Name             : ");
        String email = readString("  Email                 : ");
        String phone = readString("  Phone (Enter to skip) : ");
        System.out.print("  Membership (STUDENT/FACULTY/GUEST) [STUDENT]: ");
        String type  = sc.nextLine().trim().toUpperCase();
        if (type.isEmpty()) type = "STUDENT";

        try {
            boolean ok = library.registerUser(new User(id, name, email, phone, type));
            if (ok) System.out.println(ConsoleColors.success("User registered: " + name));
            else    System.out.println(ConsoleColors.error("User ID " + id + " already exists."));
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    static void viewAllUsers() {
        header("ALL USERS (" + library.getTotalUsers() + ")");
        if (library.getAllUsers().isEmpty()) {
            println(ConsoleColors.YELLOW, "  No users registered.");
            pressEnter(); return;
        }
        printUserTableHeader();
        library.getAllUsers().forEach(u -> System.out.println("  " + u.toTableRow()));
        printUserTableFooter();
        pressEnter();
    }

    static void searchUsersFlow() {
        header("SEARCH USERS");
        String q = readString("  Enter name / user ID / email: ");
        List<User> results = library.searchUsers(q);
        if (results.isEmpty()) {
            println(ConsoleColors.YELLOW, "  No users found matching \"" + q + "\".");
        } else {
            println(ConsoleColors.GREEN, "  Found " + results.size() + " result(s):");
            printUserTableHeader();
            results.forEach(u -> System.out.println("  " + u.toTableRow()));
            printUserTableFooter();
        }
        pressEnter();
    }

    static void removeUserFlow() {
        header("REMOVE USER");
        String id = readString("  Enter User ID to remove: ").toUpperCase();
        try {
            boolean ok = library.removeUser(id);
            if (ok) System.out.println(ConsoleColors.success("User " + id + " removed."));
            else    System.out.println(ConsoleColors.error("User ID " + id + " not found."));
        } catch (IllegalStateException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    static void userProfileFlow() {
        header("USER PROFILE");
        String id = readString("  Enter User ID: ").toUpperCase();
        library.findUser(id).ifPresentOrElse(user -> {
            System.out.println();
            System.out.println(ConsoleColors.BOLD + "  " + user.getName() + ConsoleColors.RESET
                    + "  [" + user.getMembershipType() + "]  ID: " + user.getUserId());
            System.out.println("  Email : " + user.getEmail());
            System.out.println("  Phone : " + user.getPhone());
            System.out.println();

            System.out.println(ConsoleColors.YELLOW + ConsoleColors.BOLD
                    + "  CURRENTLY BORROWED (" + user.getActiveBorrows() + ")" + ConsoleColors.RESET);
            if (user.getActiveRecords().isEmpty()) {
                println(ConsoleColors.DIM, "  None");
            } else {
                printRecordTableHeader();
                user.getActiveRecords().forEach(r -> System.out.println("  " + r.toTableRow()));
                printRecordTableFooter();
            }

            System.out.println();
            System.out.println(ConsoleColors.CYAN + ConsoleColors.BOLD
                    + "  BORROW HISTORY (" + user.getHistoryRecords().size() + ")" + ConsoleColors.RESET);
            if (user.getHistoryRecords().isEmpty()) {
                println(ConsoleColors.DIM, "  None");
            } else {
                printRecordTableHeader();
                user.getHistoryRecords().forEach(r -> System.out.println("  " + r.toTableRow()));
                printRecordTableFooter();
            }
        }, () -> System.out.println(ConsoleColors.error("User ID " + id + " not found.")));
        pressEnter();
    }

    // ═════════════════════════════════════════════════════════════
    //  ISSUE / RETURN FLOWS
    // ═════════════════════════════════════════════════════════════

    static void issueBookFlow() {
        header("ISSUE BOOK");
        viewAllBooks();
        String bookId = readString("  Enter Book ID to issue: ").toUpperCase();
        viewAllUsers();
        String userId = readString("  Enter User ID         : ").toUpperCase();

        try {
            IssueRecord record = library.issueBook(bookId, userId);
            System.out.println();
            System.out.println(ConsoleColors.success("Book issued successfully!"));
            System.out.println(ConsoleColors.BOLD + "\n  ┌─── ISSUE RECEIPT ─────────────────────────────┐" + ConsoleColors.RESET);
            System.out.printf("  │  Receipt No : #%04d                            │%n", record.getReceiptNo());
            System.out.printf("  │  Book       : %-38s│%n", record.getBookTitle());
            System.out.printf("  │  Issued To  : %-38s│%n", record.getUserName());
            System.out.printf("  │  Issue Date : %-38s│%n", record.getIssueDate());
            System.out.printf("  │  Due Date   : %-38s│%n", record.getDueDate());
            System.out.println(ConsoleColors.BOLD + "  └────────────────────────────────────────────────┘" + ConsoleColors.RESET);
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    static void returnBookFlow() {
        header("RETURN BOOK");
        // Show active issues to help the user
        List<IssueRecord> active = library.getActiveRecords();
        if (active.isEmpty()) {
            println(ConsoleColors.YELLOW, "  No books are currently issued.");
            pressEnter(); return;
        }
        printRecordTableHeader();
        active.forEach(r -> System.out.println("  " + r.toTableRow()));
        printRecordTableFooter();

        String bookId = readString("  Enter Book ID to return: ").toUpperCase();
        String userId = readString("  Enter User ID          : ").toUpperCase();

        try {
            double fine = library.returnBook(bookId, userId);
            System.out.println();
            System.out.println(ConsoleColors.success("Book returned successfully!"));
            if (fine > 0) {
                System.out.println(ConsoleColors.warn(
                        String.format("Late return fine: ₹%.2f", fine)));
            } else {
                println(ConsoleColors.GREEN, "  No fine – returned on time.");
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println(ConsoleColors.error(e.getMessage()));
        }
        pressEnter();
    }

    // ═════════════════════════════════════════════════════════════
    //  TABLE PRINTERS
    // ═════════════════════════════════════════════════════════════

    static void printBookTableHeader() {
        System.out.println("  ┌────────┬────────────────────────────────┬──────────────────────┬────────────────┬────────────────────┐");
        System.out.println("  │ ID     │ Title                          │ Author               │ Genre          │ Status             │");
        System.out.println("  ├────────┼────────────────────────────────┼──────────────────────┼────────────────┼────────────────────┤");
    }
    static void printBookTableFooter() {
        System.out.println("  └────────┴────────────────────────────────┴──────────────────────┴────────────────┴────────────────────┘");
    }

    static void printUserTableHeader() {
        System.out.println("  ┌────────┬────────────────────────┬────────────────────────────┬──────────┬──────────────┐");
        System.out.println("  │ ID     │ Name                   │ Email                      │ Type     │ Borrows      │");
        System.out.println("  ├────────┼────────────────────────┼────────────────────────────┼──────────┼──────────────┤");
    }
    static void printUserTableFooter() {
        System.out.println("  └────────┴────────────────────────┴────────────────────────────┴──────────┴──────────────┘");
    }

    static void printRecordTableHeader() {
        System.out.println("  ┌──────┬────────┬────────────────────────────┬────────────┬──────────────┬──────────────────────────────┐");
        System.out.println("  │ #    │ BookID │ Title                      │ UserID     │ Issue Date   │ Status                       │");
        System.out.println("  ├──────┼────────┼────────────────────────────┼────────────┼──────────────┼──────────────────────────────┤");
    }
    static void printRecordTableFooter() {
        System.out.println("  └──────┴────────┴────────────────────────────┴────────────┴──────────────┴──────────────────────────────┘");
    }

    static void printRecords(List<IssueRecord> records, String title) {
        header(title);
        if (records.isEmpty()) {
            println(ConsoleColors.YELLOW, "  No records found.");
        } else {
            printRecordTableHeader();
            records.forEach(r -> System.out.println("  " + r.toTableRow()));
            printRecordTableFooter();
            println(ConsoleColors.CYAN, "  Total: " + records.size() + " record(s)");
        }
        pressEnter();
    }

    // ═════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ═════════════════════════════════════════════════════════════

    static void printBanner() {
        String C = ConsoleColors.CYAN + ConsoleColors.BOLD;
        String R = ConsoleColors.RESET;
        System.out.println(C);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║          LIBRARY MANAGEMENT SYSTEM                  ║");
        System.out.println("  ║              OOP Edition  •  Java                   ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝" + R);
    }

    static void printMainMenu() {
        System.out.println();
        String B = ConsoleColors.BLUE + ConsoleColors.BOLD;
        String b = ConsoleColors.BLUE;
        String R = ConsoleColors.RESET;
        System.out.println(B + "  ╔══════════════════════════════╗" + R);
        System.out.println(B + "  ║          MAIN MENU           ║" + R);
        System.out.println(B + "  ╠══════════════════════════════╣" + R);
        System.out.println(b + "  ║  1. 📚 Book Management       ║" + R);
        System.out.println(b + "  ║  2. 👤 User Management       ║" + R);
        System.out.println(b + "  ║  3. 📤 Issue Book            ║" + R);
        System.out.println(b + "  ║  4. 📥 Return Book           ║" + R);
        System.out.println(b + "  ║  5. 📋 View Transactions     ║" + R);
        System.out.println(b + "  ║  6. 📊 Reports & Stats       ║" + R);
        System.out.println(b + "  ║  0. 🚪 Exit                  ║" + R);
        System.out.println(B + "  ╚══════════════════════════════╝" + R);
    }

    static void header(String title) {
        System.out.println();
        System.out.println(ConsoleColors.BOLD + ConsoleColors.CYAN
                + "  ┌── " + title + " " + "─".repeat(Math.max(2, 44 - title.length())) + "┐"
                + ConsoleColors.RESET);
    }

    static void println(String color, String msg) {
        System.out.println(color + msg + ConsoleColors.RESET);
    }

    static void pressEnter() {
        System.out.print(ConsoleColors.DIM + "\n  Press Enter to continue..." + ConsoleColors.RESET);
        sc.nextLine();
    }

    // ═════════════════════════════════════════════════════════════
    //  INPUT HELPERS
    // ═════════════════════════════════════════════════════════════

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = sc.nextInt(); sc.nextLine(); return v;
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println(ConsoleColors.error("Enter a whole number."));
            }
        }
    }

    static int readPositiveInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v > 0) return v;
            System.out.println(ConsoleColors.error("Value must be greater than 0."));
        }
    }

    static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println(ConsoleColors.error("Input cannot be empty."));
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  DEMO / SEED DATA
    // ═════════════════════════════════════════════════════════════

    static void seedData() {
        // Books
        library.addBook(new Book("B001", "The Pragmatic Programmer",     "Andrew Hunt",       "Technology",  3));
        library.addBook(new Book("B002", "Clean Code",                   "Robert C. Martin",  "Technology",  2));
        library.addBook(new Book("B003", "Introduction to Algorithms",   "CLRS",              "CS Theory",   4));
        library.addBook(new Book("B004", "Design Patterns",              "Gang of Four",      "Technology",  2));
        library.addBook(new Book("B005", "The Alchemist",                "Paulo Coelho",      "Fiction",     5));
        library.addBook(new Book("B006", "Sapiens",                      "Yuval Noah Harari", "Non-Fiction", 3));
        library.addBook(new Book("B007", "Atomic Habits",                "James Clear",       "Self-Help",   4));
        library.addBook(new Book("B008", "Deep Work",                    "Cal Newport",       "Self-Help",   2));

        // Users
        library.registerUser(new User("U001", "Arjun Sharma",  "arjun@college.edu",  "9876543210", "STUDENT"));
        library.registerUser(new User("U002", "Priya Reddy",   "priya@college.edu",  "9876543211", "STUDENT"));
        library.registerUser(new User("U003", "Dr. Kumar Rao", "kumar@college.edu",  "9876543212", "FACULTY"));
        library.registerUser(new User("U004", "Sneha Patel",   "sneha@college.edu",  "9876543213", "STUDENT"));
        library.registerUser(new User("U005", "Ravi Verma",    "ravi@college.edu",   "9876543214", "GUEST"));

        // A couple of demo issues so transactions aren't empty
        try {
            library.issueBook("B001", "U001");
            library.issueBook("B005", "U002");
            library.issueBook("B007", "U003");
        } catch (Exception ignored) {}

        System.out.println(ConsoleColors.success(
            "Demo data loaded: 8 books, 5 users, 3 active issues."));
        System.out.println(ConsoleColors.info("Library: " + library.getLibraryName()));
    }
}
