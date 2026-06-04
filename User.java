import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User.java
 * Represents a registered library member.
 *
 * OOP concepts: Encapsulation, Composition (User has-a list of IssueRecords),
 *               Validation in setters
 */
public class User {

    private static final int MAX_BOOKS_PER_USER = 3;  // borrow limit

    private final String userId;
    private String name;
    private String email;
    private String phone;
    private final String membershipType;   // "STUDENT" | "FACULTY" | "GUEST"

    // Composition: a user keeps track of their own active borrows
    private final List<IssueRecord> activeRecords  = new ArrayList<>();
    private final List<IssueRecord> historyRecords = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────
    public User(String userId, String name, String email,
                String phone, String membershipType) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("User ID cannot be empty.");
        this.userId         = userId.toUpperCase().trim();
        this.membershipType = membershipType.toUpperCase().trim();
        setName(name);
        setEmail(email);
        setPhone(phone);
    }

    /** Convenience constructor – defaults to STUDENT membership */
    public User(String userId, String name, String email) {
        this(userId, name, email, "N/A", "STUDENT");
    }

    // ── Getters ───────────────────────────────────────────────────
    public String getUserId()        { return userId; }
    public String getName()          { return name; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }
    public String getMembershipType(){ return membershipType; }
    public int    getActiveBorrows() { return activeRecords.size(); }
    public boolean canBorrow()       { return activeRecords.size() < MAX_BOOKS_PER_USER; }

    public List<IssueRecord> getActiveRecords()  {
        return Collections.unmodifiableList(activeRecords);
    }
    public List<IssueRecord> getHistoryRecords() {
        return Collections.unmodifiableList(historyRecords);
    }

    // ── Setters with validation ───────────────────────────────────
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email address.");
        this.email = email.trim().toLowerCase();
    }

    public void setPhone(String phone) {
        this.phone = (phone == null || phone.isBlank()) ? "N/A" : phone.trim();
    }

    // ── Borrow / Return helpers ───────────────────────────────────

    /**
     * Adds a new IssueRecord to this user's active borrows.
     * @throws IllegalStateException if user has hit their borrow limit
     */
    public void addActiveBorrow(IssueRecord record) {
        if (!canBorrow())
            throw new IllegalStateException(
                name + " has reached the maximum borrow limit of " + MAX_BOOKS_PER_USER + " books.");
        activeRecords.add(record);
    }

    /**
     * Moves the IssueRecord for the given bookId from active → history.
     * @return the record that was closed, or null if not found
     */
    public IssueRecord closeRecord(String bookId) {
        IssueRecord found = activeRecords.stream()
                .filter(r -> r.getBookId().equalsIgnoreCase(bookId))
                .findFirst()
                .orElse(null);
        if (found != null) {
            activeRecords.remove(found);
            historyRecords.add(found);
        }
        return found;
    }

    /** True if the user currently has this book issued */
    public boolean hasBorrowed(String bookId) {
        return activeRecords.stream()
                .anyMatch(r -> r.getBookId().equalsIgnoreCase(bookId));
    }

    // ── Display ───────────────────────────────────────────────────
    public String toTableRow() {
        String borrowStatus = canBorrow()
                ? ConsoleColors.GREEN + activeRecords.size() + "/" + MAX_BOOKS_PER_USER + ConsoleColors.RESET
                : ConsoleColors.RED   + activeRecords.size() + "/" + MAX_BOOKS_PER_USER + " FULL" + ConsoleColors.RESET;
        return String.format("| %-6s | %-22s | %-26s | %-8s | %-12s |",
                userId, name, email, membershipType, borrowStatus);
    }

    @Override
    public String toString() {
        return String.format("%s[User]%s %s (ID: %s) | %s | Borrows: %d/%d",
                ConsoleColors.BLUE, ConsoleColors.RESET,
                name, userId, membershipType,
                activeRecords.size(), MAX_BOOKS_PER_USER);
    }
}
