/**
 * Book.java
 * Represents a single book in the library.
 *
 * OOP concepts: Encapsulation, Constructor overloading, toString override
 */
public class Book {

    // ── Fields ────────────────────────────────────────────────────
    private final String bookId;      // unique identifier  e.g. "B001"
    private String title;
    private String author;
    private String genre;
    private int totalCopies;          // total copies owned by the library
    private int availableCopies;      // copies currently on the shelf
    private int issuedCount;          // cumulative times this book was issued

    // ── Constructor ───────────────────────────────────────────────
    public Book(String bookId, String title, String author, String genre, int totalCopies) {
        if (bookId == null || bookId.isBlank())
            throw new IllegalArgumentException("Book ID cannot be empty.");
        if (totalCopies < 1)
            throw new IllegalArgumentException("Total copies must be at least 1.");

        this.bookId          = bookId.toUpperCase().trim();
        this.title           = title.trim();
        this.author          = author.trim();
        this.genre           = genre.trim();
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
        this.issuedCount     = 0;
    }

    /** Convenience constructor – defaults to 1 copy */
    public Book(String bookId, String title, String author, String genre) {
        this(bookId, title, author, genre, 1);
    }

    // ── Getters ───────────────────────────────────────────────────
    public String getBookId()          { return bookId; }
    public String getTitle()           { return title; }
    public String getAuthor()          { return author; }
    public String getGenre()           { return genre; }
    public int    getTotalCopies()     { return totalCopies; }
    public int    getAvailableCopies() { return availableCopies; }
    public int    getIssuedCount()     { return issuedCount; }
    public boolean isAvailable()       { return availableCopies > 0; }

    // ── Setters ───────────────────────────────────────────────────
    public void setTitle(String title)   { this.title = title.trim(); }
    public void setAuthor(String author) { this.author = author.trim(); }
    public void setGenre(String genre)   { this.genre = genre.trim(); }

    // ── Issue / Return logic ──────────────────────────────────────

    /**
     * Decrements available copies when a book is issued.
     * @throws IllegalStateException if no copies available
     */
    public void issueOneCopy() {
        if (availableCopies == 0)
            throw new IllegalStateException("No available copies of \"" + title + "\".");
        availableCopies--;
        issuedCount++;
    }

    /**
     * Increments available copies when a book is returned.
     * @throws IllegalStateException if all copies are already in
     */
    public void returnOneCopy() {
        if (availableCopies == totalCopies)
            throw new IllegalStateException("All copies of \"" + title + "\" are already in.");
        availableCopies++;
    }

    // ── Display ───────────────────────────────────────────────────
    public String toTableRow() {
        String status = isAvailable()
                ? ConsoleColors.GREEN + "Available (" + availableCopies + ")" + ConsoleColors.RESET
                : ConsoleColors.RED   + "Issued Out"                          + ConsoleColors.RESET;
        return String.format("| %-6s | %-30s | %-20s | %-14s | %-18s |",
                bookId, cap(title, 30), cap(author, 20), genre, status);
    }

    @Override
    public String toString() {
        return String.format(
            "%s[Book]%s %s (ID: %s) by %s | Genre: %s | Copies: %d/%d available",
            ConsoleColors.CYAN, ConsoleColors.RESET,
            title, bookId, author, genre, availableCopies, totalCopies);
    }

    // ── Utility ───────────────────────────────────────────────────
    private String cap(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "…" : s;
    }
}
