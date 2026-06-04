# Library Management System
### Multi-class Java OOP Project

A fully-featured CLI Library Management System demonstrating core OOP principles:
**Encapsulation, Composition, Aggregation, Constructor Overloading, and more.**

---

## Project Structure
```
LibrarySystem/
├── src/
│   ├── ConsoleColors.java   # ANSI colour constants + helpers
│   ├── Book.java            # Entity: book with copies & grade tracking
│   ├── IssueRecord.java     # Transaction record: issue date, due date, fines
│   ├── User.java            # Entity: user with borrow limit & history
│   ├── Library.java         # Core business logic: CRUD + issue/return rules
│   └── Main.java            # CLI entry point with 2-level menu
└── README.md
```

---

## OOP Concepts Used

| Concept | Where |
|---|---|
| **Encapsulation** | All fields private; controlled via getters/setters with validation |
| **Constructor Overloading** | `Book(id,title,author,genre)` and `Book(id,title,author,genre,copies)` |
| **Composition** | `User` has-a `List<IssueRecord>`; `Library` owns all records |
| **Aggregation** | `Library` aggregates `Book` and `User` objects |
| **Abstraction** | `Library.issueBook()` hides all transaction steps behind one call |
| **Static fields** | `IssueRecord.counter` auto-increments receipt numbers |
| **Enums / Constants** | `MAX_BOOKS_PER_USER`, `LOAN_PERIOD_DAYS`, `FINE_PER_DAY` |
| **Optional** | `Library.findBook()` / `findUser()` return `Optional<T>` (null-safe) |
| **Streams & Lambdas** | Filtering, sorting, searching throughout `Library.java` |

---

## How to Compile & Run

### Terminal
```bash
# Step 1 – Compile all 5 source files into /out
javac -d out src/*.java

# Step 2 – Run
java -cp out Main
```

### VS Code
1. Install **Extension Pack for Java** (Microsoft)
2. Open the `LibrarySystem/` folder
3. Open `Main.java` → click **▶ Run** above `main()`

### IntelliJ IDEA CE
1. **File → Open** → select `LibrarySystem/`
2. Right-click `Main.java` → **Run 'Main.main()'**

---

## Menu Structure
```
Main Menu
├── 1. Book Management
│   ├── Add Book
│   ├── View All Books
│   ├── Search (title / author / genre)
│   └── Remove Book
├── 2. User Management
│   ├── Register User
│   ├── View All Users
│   ├── Search Users
│   ├── Remove User
│   └── View User Profile (with borrow history)
├── 3. Issue Book   → issues a copy, creates IssueRecord with due date
├── 4. Return Book  → accepts return, calculates overdue fine (₹2/day)
├── 5. Transactions → All / Active / Overdue records
├── 6. Reports      → Stats + Top 5 most borrowed books
└── 0. Exit
```

---

## Business Rules
- Each user may borrow up to **3 books** at a time
- Loan period is **14 days**
- Overdue fine: **₹2 per day**
- A book cannot be removed while copies are issued
- A user cannot be removed while they have active borrows
- Duplicate Book IDs and User IDs are rejected

---

## Demo Data (loaded on startup)
**Books:** The Pragmatic Programmer, Clean Code, Intro to Algorithms,
Design Patterns, The Alchemist, Sapiens, Atomic Habits, Deep Work

**Users:** Arjun (Student), Priya (Student), Dr. Kumar (Faculty),
Sneha (Student), Ravi (Guest)
