package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BadgeLabel extends JLabel {
    private Color badgeBackground;
    private Color badgeForeground;
    private int cornerRadius = 12;

    public BadgeLabel(String text, Color bg, Color fg) {
        super(text, SwingConstants.CENTER);
        this.badgeBackground = bg;
        this.badgeForeground = fg;
        init();
    }

    private void init() {
        setFont(AppTheme.FONT_SMALL_BOLD);
        setForeground(badgeForeground);
        setOpaque(false);
        setBorder(new EmptyBorder(3, 10, 3, 10));
    }

    public static BadgeLabel forRole(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new BadgeLabel("👑 ADMIN", ModernColors.BADGE_ADMIN_BG, Color.WHITE);
        } else {
            return new BadgeLabel("🎓 ALUNO", ModernColors.BADGE_ALUNO_BG, Color.WHITE);
        }
    }

    public static BadgeLabel forMethod(String method) {
        if (method == null) method = "GET";
        switch (method.toUpperCase()) {
            case "POST":
                return new BadgeLabel("POST", ModernColors.SUCCESS, Color.WHITE);
            case "PUT":
                return new BadgeLabel("PUT", ModernColors.WARNING, Color.BLACK);
            case "DELETE":
                return new BadgeLabel("DELETE", ModernColors.DANGER, Color.WHITE);
            case "GET":
            default:
                return new BadgeLabel("GET", ModernColors.INFO, Color.WHITE);
        }
    }

    public static BadgeLabel forStatus(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return new BadgeLabel(statusCode + " OK", ModernColors.SUCCESS, Color.WHITE);
        } else if (statusCode == 400 || statusCode == 409) {
            return new BadgeLabel(statusCode + " " + (statusCode == 409 ? "Conflict" : "Bad Request"), ModernColors.WARNING, Color.BLACK);
        } else if (statusCode == 401 || statusCode == 403) {
            return new BadgeLabel(statusCode + " " + (statusCode == 403 ? "Forbidden" : "Unauthorized"), ModernColors.DANGER, Color.WHITE);
        } else if (statusCode == 404) {
            return new BadgeLabel("404 Not Found", ModernColors.DANGER, Color.WHITE);
        } else {
            return new BadgeLabel(String.valueOf(statusCode), AppTheme.getSurface(), AppTheme.getTextPrimary());
        }
    }

    public void setBadgeColors(Color bg, Color fg) {
        this.badgeBackground = bg;
        this.badgeForeground = fg;
        setForeground(fg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(badgeBackground != null ? badgeBackground : ModernColors.PRIMARY);
        g2.fillRoundRect(0, 0, width - 1, height - 1, height, height);

        g2.dispose();
        super.paintComponent(g);
    }
}
