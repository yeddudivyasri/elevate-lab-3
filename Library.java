import java.util.*;
import java.util.stream.Collectors;

/**
 * Library.java
 * Central class that orchestrates Books, Users, and IssueRecords.
 *
 * OOP concepts: Aggregation (Library has Books & Users),
 *               Composition (Library owns IssueRecords),
 *               Single Responsibility, encapsulation of business rules
 */
public class Library {

    private final String libraryName;

    // ── Catalogues ────────────────────────────────────────────────
    private final Map<String, Book>  bookCatalogue  = new LinkedHashMap<>();
    private final Map<String, User>  userRegistry   = new LinkedHashMap<>();
    private final List<IssueRecord>  allRecords     = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────
    public Library(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryName() { return libraryName; }

    // ══════════════════════════════════════════════════════════════
    //  BOOK MANAGEMENT
    // ══════════════════════════════════════════════════════════════

    /** Registers a new book. Returns false if ID already exists. */
    public boolean addBook(Book book) {
        if (bookCatalogue.containsKey(book.getBookId())) return false;
        bookCatalogue.put(book.getBookId(), book);
        return true;
    }

    /** Removes a book only if no copies are currently issued. */
    public boolean removeBook(String bookId) {
        Book book = bookCatalogue.get(bookId.toUpperCase());
        if (book == null) return false;
        if (book.getAvailableCopies() < book.getTotalCopies())
            throw new IllegalStateException(
                "Cannot remove \"" + book.getTitle() + "\": some copies are still issued.");
        bookCatalogue.remove(bookId.toUpperCase());
        return true;
    }

    public Optional<Book> findBook(String bookId) {
        return Optional.ofNullable(bookCatalogue.get(bookId.toUpperCase()));
    }

    public Collection<Book> getAllBooks() { return bookCatalogue.values(); }

    /** Full-text search across title and author (case-insensitive) */
    public List<Book> searchBooks(String query) {
        String q = query.toLowerCase();
        return bookCatalogue.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                          || b.getAuthor().toLowerCase().contains(q)
                          || b.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return bookCatalogue.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════
    //  USER MANAGEMENT
    // ══════════════════════════════════════════════════════════════

    /** Registers a new user. Returns false if ID already exists. */
    public boolean registerUser(User user) {
        if (userRegistry.containsKey(user.getUserId())) return false;
        userRegistry.put(user.getUserId(), user);
        return true;
    }

    /** Removes a user only if they have no active borrows. */
    public boolean removeUser(String userId) {
        User user = userRegistry.get(userId.toUpperCase());
        if (user == null) return false;
        if (user.getActiveBorrows() > 0)
            throw new IllegalStateException(
                user.getName() + " has " + user.getActiveBorrows() + " unreturned book(s).");
        userRegistry.remove(userId.toUpperCase());
        return true;
    }

    public Optional<User> findUser(String userId) {
        return Optional.ofNullable(userRegistry.get(userId.toUpperCase()));
    }

    public Collection<User> getAllUsers() { return userRegistry.values(); }

    public List<User> searchUsers(String query) {
        String q = query.toLowerCase();
        return userRegistry.values().stream()
                .filter(u -> u.getName().toLowerCase().contains(q)
                          || u.getUserId().toLowerCase().contains(q)
                          || u.getEmail().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════
    //  CORE TRANSACTIONS: ISSUE & RETURN
    // ══════════════════════════════════════════════════════════════

    /**
     * Issues a book to a user.
     * @return the created IssueRecord
     * @throws IllegalStateException if book unavailable or user limit reached
     * @throws NoSuchElementException if bookId or userId not found
     */
    public IssueRecord issueBook(String bookId, String userId) {
        Book book = findBook(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book ID not found: " + bookId));
        User user = findUser(userId)
                .orElseThrow(() -> new NoSuchElementException("User ID not found: " + userId));

        if (!book.isAvailable())
            throw new IllegalStateException(
                "No available copies of \"" + book.getTitle() + "\".");
        if (!user.canBorrow())
            throw new IllegalStateException(
                user.getName() + " has reached the maximum borrow limit.");
        if (user.hasBorrowed(bookId))
            throw new IllegalStateException(
                user.getName() + " already has \"" + book.getTitle() + "\" issued.");

        // All checks passed – perform the transaction
        IssueRecord record = new IssueRecord(
                book.getBookId(), book.getTitle(),
                user.getUserId(), user.getName());

        book.issueOneCopy();
        user.addActiveBorrow(record);
        allRecords.add(record);
        return record;
    }

    /**
     * Returns a book from a user.
     * @return fine amount (₹0 if on time)
     * @throws IllegalStateException if user doesn't have this book
     * @throws NoSuchElementException if IDs not found
     */
    public double returnBook(String bookId, String userId) {
        Book book = findBook(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book ID not found: " + bookId));
        User user = findUser(userId)
                .orElseThrow(() -> new NoSuchElementException("User ID not found: " + userId));

        if (!user.hasBorrowed(bookId))
            throw new IllegalStateException(
                user.getName() + " does not currently have \"" + book.getTitle() + "\" issued.");

        // Close the record and collect fine
        IssueRecord record = user.closeRecord(bookId);
        double fine = record.markReturned();
        book.returnOneCopy();
        return fine;
    }

    // ══════════════════════════════════════════════════════════════
    //  QUERIES & REPORTS
    // ══════════════════════════════════════════════════════════════

    public List<IssueRecord> getAllRecords()       { return Collections.unmodifiableList(allRecords); }

    public List<IssueRecord> getActiveRecords() {
        return allRecords.stream().filter(r -> !r.isReturned()).collect(Collectors.toList());
    }

    public List<IssueRecord> getOverdueRecords() {
        return allRecords.stream().filter(IssueRecord::isOverdue).collect(Collectors.toList());
    }

    public int getTotalBooks()     { return bookCatalogue.size(); }
    public int getTotalUsers()     { return userRegistry.size(); }
    public int getTotalIssued()    { return getActiveRecords().size(); }

    /** Most borrowed books (top N) */
    public List<Book> getTopBorrowedBooks(int n) {
        return bookCatalogue.values().stream()
                .sorted(Comparator.comparingInt(Book::getIssuedCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}
