/**
 * ConsoleUI.java
 * Utility class for handling rich console output (Colors, Tables, Animations)
 * Created by: Member 4 (UI & Presentation)
 */
public class ConsoleUI {
    // ANSI Color Constants
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE_BOLD = "\033[1;37m";

    /**
     * Prints a standardized success message in GREEN
     */
    public static void printSuccess(String msg) {
        System.out.println(GREEN + "✔ " + msg + RESET);
    }

    /**
     * Prints a standardized error message in RED
     */
    public static void printError(String msg) {
        System.out.println(RED + "✖ " + msg + RESET);
    }

    /**
     * Prints a standardized warning message in YELLOW
     */
    public static void printWarning(String msg) {
        System.out.println(YELLOW + "⚠ " + msg + RESET);
    }

    /**
     * Prints a fancy boxed header for menus
     */
    public static void printHeader(String title) {
        String border = repeat("═", 50);
        System.out.println(CYAN + "\n╔" + border + "╗");
        // Center the title manually
        int padding = (50 - title.length()) / 2;
        String padStr = repeat(" ", Math.max(0, padding));
        System.out.println("║" + padStr + title + padStr + (title.length() % 2 != 0 ? " " : "") + "║");
        System.out.println("╚" + border + "╝" + RESET);
    }

    /**
     * Prints a simple divider line
     */
    public static void printDivider() {
        System.out.println(BLUE + "────────────────────────────────────────────────────" + RESET);
    }

    /**
     * Fake loading animation to make algorithms feel "heavy"
     * Usage: ConsoleUI.showLoading("Calculating Route");
     */
    public static void showLoading(String message) {
        System.out.print(YELLOW + message + RESET);
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300); // Wait 300ms
                System.out.print(".");
            }
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(GREEN + " Done!" + RESET);
    }

    /**
     * Helper to print a table row with specific column widths
     * @param widths Array of column widths (e.g., {10, 20, 15})
     * @param data The strings to put in the columns
     */
    public static void printRow(int[] widths, String... data) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < widths.length && i < data.length; i++) {
            sb.append(" ");
            sb.append(padRight(data[i], widths[i]));
            sb.append(" |");
        }
        System.out.println(sb.toString());
    }

    /**
     * Prints the separator line for tables based on widths
     */
    public static void printTableSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int w : widths) {
            sb.append(repeat("-", w + 2));
            sb.append("+");
        }
        System.out.println(sb.toString());
    }

    /**
     * Prints an info message in BLUE
     */
    public static void printInfo(String msg) {
        System.out.println(BLUE + "ℹ " + msg + RESET);
    }

    /**
     * Prints colored text without newline
     */
    public static void printColored(String color, String msg) {
        System.out.print(color + msg + RESET);
    }

    /**
     * Prints colored text with newline
     */
    public static void printColoredLn(String color, String msg) {
        System.out.println(color + msg + RESET);
    }

    // ==========================================
    // HELPER METHODS (Manual String Operations)
    // ==========================================

    /**
     * Manual implementation of String.repeat() for older Java versions
     */
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Pads a string to the right with spaces
     */
    private static String padRight(String s, int width) {
        if (s == null) s = "";
        // Handle ANSI codes - they don't take up visual space
        int visibleLength = s.replaceAll("\u001B\\[[;\\d]*m", "").length();
        if (visibleLength >= width) {
            return s;
        }
        return s + repeat(" ", width - visibleLength);
    }
}
