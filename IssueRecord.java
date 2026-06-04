import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * IssueRecord.java
 * Represents a single book-issue transaction.
 *
 * OOP concepts: Immutable fields (bookId, userId, issueDate), encapsulation
 */
public class IssueRecord {

    private static final int LOAN_PERIOD_DAYS = 14;   // standard 2-week loan
    private static final double FINE_PER_DAY  = 2.0;  // ₹2 per overdue day

    private static int counter = 1;                   // auto-increment receipt number

    private final int       receiptNo;
    private final String    bookId;
    private final String    userId;
    private final String    bookTitle;
    private final String    userName;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private       LocalDate returnDate;   // null until book is returned
    private       boolean   returned;

    // ── Constructor ───────────────────────────────────────────────
    public IssueRecord(String bookId, String bookTitle,
                       String userId, String userName) {
        this.receiptNo = counter++;
        this.bookId    = bookId;
        this.bookTitle = bookTitle;
        this.userId    = userId;
        this.userName  = userName;
        this.issueDate = LocalDate.now();
        this.dueDate   = issueDate.plusDays(LOAN_PERIOD_DAYS);
        this.returned  = false;
    }

    // ── Getters ───────────────────────────────────────────────────
    public int       getReceiptNo()  { return receiptNo; }
    public String    getBookId()     { return bookId; }
    public String    getUserId()     { return userId; }
    public String    getBookTitle()  { return bookTitle; }
    public String    getUserName()   { return userName; }
    public LocalDate getIssueDate()  { return issueDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean   isReturned()    { return returned; }

    public boolean isOverdue() {
        if (returned) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double calculateFine() {
        return getDaysOverdue() * FINE_PER_DAY;
    }

    // ── Return action ─────────────────────────────────────────────
    public double markReturned() {
        if (returned) throw new IllegalStateException("Book already returned.");
        this.returnDate = LocalDate.now();
        this.returned   = true;
        return calculateFine();   // fine is calculated at moment of return
    }

    // ── Display ───────────────────────────────────────────────────
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public String toTableRow() {
        String status;
        if (returned) {
            status = ConsoleColors.GREEN + "Returned " + returnDate.format(FMT) + ConsoleColors.RESET;
        } else if (isOverdue()) {
            status = ConsoleColors.RED + "OVERDUE +" + getDaysOverdue() + "d" + ConsoleColors.RESET;
        } else {
            status = ConsoleColors.YELLOW + "Due " + dueDate.format(FMT) + ConsoleColors.RESET;
        }
        return String.format("| %-4d | %-6s | %-26s | %-10s | %-12s | %-28s |",
                receiptNo, bookId, cap(bookTitle, 26), userId,
                issueDate.format(FMT), status);
    }

    @Override
    public String toString() {
        return String.format("Receipt#%04d | Book: %s | User: %s | Issued: %s | Due: %s",
                receiptNo, bookId, userId,
                issueDate.format(FMT), dueDate.format(FMT));
    }

    private String cap(String s, int n) {
        return s.length() > n ? s.substring(0, n - 1) + "…" : s;
    }
}
