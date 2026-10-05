package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BadgeLabel extends JLabel {
    private Color badgeBackground;
    private Color badgeForeground;

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
        setBorder(new EmptyBorder(4, 10, 4, 10));
    }

    public static BadgeLabel forRole(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new BadgeLabel("ADMIN", ModernColors.BADGE_ADMIN_BG, Color.WHITE);
        } else {
            return new BadgeLabel("ALUNO", ModernColors.BADGE_ALUNO_BG, Color.WHITE);
        }
    }

    public static BadgeLabel forCaronaStatus(String status) {
        if (status == null) status = "AGENDADA";
        switch (status.toUpperCase()) {
            case "AGENDADA":
                return new BadgeLabel("AGENDADA", new Color(0x06, 0x5F, 0x46), new Color(0xA7, 0xF3, 0xD0));
            case "EM_ANDAMENTO":
                return new BadgeLabel("EM ANDAMENTO", new Color(0x78, 0x35, 0x0F), new Color(0xFE, 0xF0, 0x8A));
            case "FINALIZADA":
                return new BadgeLabel("FINALIZADA", new Color(0x1E, 0x29, 0x3B), new Color(0x94, 0xA3, 0xB8));
            case "CANCELADA":
                return new BadgeLabel("CANCELADA", new Color(0x7F, 0x1D, 0x1D), new Color(0xFE, 0xCA, 0xCA));
            default:
                return new BadgeLabel(status, AppTheme.getSurface(), AppTheme.getTextPrimary());
        }
    }

    public static BadgeLabel forReservaStatus(String status) {
        if (status == null) status = "PENDENTE";
        switch (status.toUpperCase()) {
            case "ACEITA":
                return new BadgeLabel("ACEITA", new Color(0x06, 0x5F, 0x46), new Color(0xA7, 0xF3, 0xD0));
            case "PENDENTE":
                return new BadgeLabel("PENDENTE", new Color(0x78, 0x35, 0x0F), new Color(0xFE, 0xF0, 0x8A));
            case "RECUSADA":
                return new BadgeLabel("RECUSADA", new Color(0x7F, 0x1D, 0x1D), new Color(0xFE, 0xCA, 0xCA));
            case "CANCELADA":
                return new BadgeLabel("CANCELADA", new Color(0x33, 0x41, 0x55), new Color(0x94, 0xA3, 0xB8));
            default:
                return new BadgeLabel(status, AppTheme.getSurface(), AppTheme.getTextPrimary());
        }
    }

    public static BadgeLabel forDirecao(String direcao) {
        if ("CAMPUS_PARA_BAIRRO".equalsIgnoreCase(direcao)) {
            return new BadgeLabel("Campus -> Bairro", new Color(0x31, 0x2E, 0x81), new Color(0xC7, 0xD2, 0xFE));
        } else {
            return new BadgeLabel("Bairro -> Campus", new Color(0x0C, 0x4A, 0x6E), new Color(0xBA, 0xE6, 0xFD));
        }
    }

    public static BadgeLabel forVagas(int disponiveis, int totais) {
        if (disponiveis > 0) {
            return new BadgeLabel(disponiveis + "/" + totais + " vagas livres", new Color(0x06, 0x4E, 0x3B), new Color(0x6E, 0xE7, 0xB7));
        } else {
            return new BadgeLabel("Lotado (0 vagas)", new Color(0x45, 0x1A, 0x03), new Color(0xFD, 0xBA, 0x74));
        }
    }

    public static BadgeLabel forMethod(String method) {
        if (method == null) method = "GET";
        switch (method.toUpperCase()) {
            case "POST":
                return new BadgeLabel("POST", ModernColors.SUCCESS, Color.WHITE);
            case "PUT":
                return new BadgeLabel("PUT", ModernColors.WARNING, Color.BLACK);
            case "PATCH":
                return new BadgeLabel("PATCH", ModernColors.ACCENT_PURPLE, Color.WHITE);
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
