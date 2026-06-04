/**
 * ConsoleColors.java
 * ANSI escape codes for coloured terminal output.
 * Works in VS Code terminal, IntelliJ, macOS Terminal, and most Linux terminals.
 */
public final class ConsoleColors {
    private ConsoleColors() {}  // Utility class – no instances

    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";

    // Foreground colours
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Background colours
    public static final String BG_RED    = "\u001B[41m";
    public static final String BG_GREEN  = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE   = "\u001B[44m";
    public static final String BG_CYAN   = "\u001B[46m";

    // Shortcuts
    public static String success(String msg) { return GREEN + "  ✓ " + msg + RESET; }
    public static String error(String msg)   { return RED   + "  ✗ " + msg + RESET; }
    public static String warn(String msg)    { return YELLOW + "  ⚠ " + msg + RESET; }
    public static String info(String msg)    { return CYAN  + "  ℹ " + msg + RESET; }
}
