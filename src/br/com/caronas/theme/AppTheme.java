package br.com.caronas.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class AppTheme {
    private static boolean isDark = true;
    private static final List<Runnable> themeListeners = new ArrayList<>();

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 12);

    public static void initialize() {
        try {
            if (isDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            applyCustomUIDefaults();
        } catch (Exception ex) {
            System.err.println("Erro ao inicializar FlatLaf: " + ex.getMessage());
        }
    }

    public static void toggleTheme(JFrame frame) {
        setDarkTheme(!isDark, frame);
    }

    public static void setDarkTheme(boolean dark, JFrame frame) {
        isDark = dark;
        try {
            if (isDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            applyCustomUIDefaults();
            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
                frame.repaint();
            }
            for (Runnable listener : themeListeners) {
                listener.run();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addThemeChangeListener(Runnable listener) {
        themeListeners.add(listener);
    }

    private static void applyCustomUIDefaults() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.rowHeight", 38);
    }

    public static boolean isDarkMode() {
        return isDark;
    }

    public static Color getBackground() {
        return isDark ? ModernColors.DARK_BG : ModernColors.LIGHT_BG;
    }

    public static Color getCardBackground() {
        return isDark ? ModernColors.DARK_CARD : ModernColors.LIGHT_CARD;
    }

    public static Color getCardHover() {
        return isDark ? ModernColors.DARK_CARD_HOVER : ModernColors.LIGHT_CARD_HOVER;
    }

    public static Color getSurface() {
        return isDark ? ModernColors.DARK_SURFACE : ModernColors.LIGHT_SURFACE;
    }

    public static Color getBorder() {
        return isDark ? ModernColors.DARK_BORDER : ModernColors.LIGHT_BORDER;
    }

    public static Color getBorderLight() {
        return isDark ? ModernColors.DARK_BORDER_LIGHT : ModernColors.LIGHT_BORDER;
    }

    public static Color getTextPrimary() {
        return isDark ? ModernColors.DARK_TEXT_PRIMARY : ModernColors.LIGHT_TEXT_PRIMARY;
    }

    public static Color getTextSecondary() {
        return isDark ? ModernColors.DARK_TEXT_SECONDARY : ModernColors.LIGHT_TEXT_SECONDARY;
    }

    public static Color getTextMuted() {
        return isDark ? ModernColors.DARK_TEXT_MUTED : ModernColors.LIGHT_TEXT_MUTED;
    }
}
