

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Manages the Light and Dark theme switching and provides standard colors/fonts.
 */
public class ThemeManager {
    public static boolean isDarkMode = false;

    // Light Theme Colors
    private static final Color LIGHT_BG = Color.decode("#F8F9FA");
    private static final Color LIGHT_CARD_BG = Color.decode("#FFFFFF");
    private static final Color LIGHT_PRIMARY = Color.decode("#2E86AB"); // Teal Blue
    private static final Color LIGHT_ACCENT = Color.decode("#F18F01"); // Warm Orange
    private static final Color LIGHT_TEXT = Color.decode("#1A1A2E");
    private static final Color LIGHT_SUBTEXT = Color.decode("#6C757D");
    private static final Color LIGHT_BORDER = Color.decode("#DEE2E6");
    private static final Color LIGHT_SUCCESS = Color.decode("#28A745");
    private static final Color LIGHT_DANGER = Color.decode("#DC3545");
    private static final Color LIGHT_HEADER_BG = Color.decode("#2E86AB");

    // Dark Theme Colors
    private static final Color DARK_BG = Color.decode("#0D1117");
    private static final Color DARK_CARD_BG = Color.decode("#161B22");
    private static final Color DARK_PRIMARY = Color.decode("#58A6FF");
    private static final Color DARK_ACCENT = Color.decode("#F0883E");
    private static final Color DARK_TEXT = Color.decode("#E6EDF3");
    private static final Color DARK_SUBTEXT = Color.decode("#8B949E");
    private static final Color DARK_BORDER = Color.decode("#30363D");
    private static final Color DARK_SUCCESS = Color.decode("#3FB950");
    private static final Color DARK_DANGER = Color.decode("#F85149");
    private static final Color DARK_HEADER_BG = Color.decode("#161B22");

    public static void toggleTheme(JFrame frame) {
        isDarkMode = !isDarkMode;
        // The GUI class handles updating component colors and calls SwingUtilities.updateComponentTreeUI
    }

    public static Color getBg() { return isDarkMode ? DARK_BG : LIGHT_BG; }
    public static Color getCardBg() { return isDarkMode ? DARK_CARD_BG : LIGHT_CARD_BG; }
    public static Color getPrimary() { return isDarkMode ? DARK_PRIMARY : LIGHT_PRIMARY; }
    public static Color getAccent() { return isDarkMode ? DARK_ACCENT : LIGHT_ACCENT; }
    public static Color getText() { return isDarkMode ? DARK_TEXT : LIGHT_TEXT; }
    public static Color getSubtext() { return isDarkMode ? DARK_SUBTEXT : LIGHT_SUBTEXT; }
    public static Color getBorder() { return isDarkMode ? DARK_BORDER : LIGHT_BORDER; }
    public static Color getSuccess() { return isDarkMode ? DARK_SUCCESS : LIGHT_SUCCESS; }
    public static Color getDanger() { return isDarkMode ? DARK_DANGER : LIGHT_DANGER; }
    public static Color getHeaderBg() { return isDarkMode ? DARK_HEADER_BG : LIGHT_HEADER_BG; }

    public static Font getTitleFont() { return new Font("Segoe UI", Font.BOLD, 22); }
    public static Font getBodyFont() { return new Font("Segoe UI", Font.PLAIN, 14); }
    public static Font getSmallFont() { return new Font("Segoe UI", Font.PLAIN, 12); }
    public static Font getPriceFont() { return new Font("Segoe UI", Font.BOLD, 16); }
    public static Font getEmojiFont() { 
        // Fallback handles platforms without Segoe UI Emoji
        return new Font("Segoe UI Emoji", Font.PLAIN, 36); 
    }
}
