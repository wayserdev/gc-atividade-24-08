package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    public enum ButtonStyle {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY, GHOST, ACCENT
    }

    private ButtonStyle style = ButtonStyle.PRIMARY;
    private int cornerRadius = 10;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public RoundedButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        initButton();
    }

    public RoundedButton(Icon icon, ButtonStyle style) {
        super(icon);
        this.style = style;
        initButton();
    }

    public RoundedButton(String text, Icon icon, ButtonStyle style) {
        super(text, icon);
        this.style = style;
        initButton();
    }

    private void initButton() {
        setFont(AppTheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setButtonStyle(ButtonStyle style) {
        this.style = style;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color baseBg;
        Color textColor = Color.WHITE;
        Color borderColor = null;

        switch (style) {
            case SUCCESS:
                baseBg = ModernColors.SUCCESS;
                break;
            case DANGER:
                baseBg = ModernColors.DANGER;
                break;
            case WARNING:
                baseBg = ModernColors.WARNING;
                textColor = Color.BLACK;
                break;
            case SECONDARY:
                baseBg = AppTheme.getSurface();
                textColor = AppTheme.getTextPrimary();
                borderColor = AppTheme.getBorder();
                break;
            case GHOST:
                baseBg = new Color(0, 0, 0, 0);
                textColor = AppTheme.getTextPrimary();
                borderColor = AppTheme.getBorder();
                break;
            case ACCENT:
                baseBg = ModernColors.ACCENT_PURPLE;
                break;
            case PRIMARY:
            default:
                baseBg = ModernColors.PRIMARY;
                break;
        }

        if (!isEnabled()) {
            baseBg = AppTheme.isDarkMode() ? new Color(0x33, 0x41, 0x55, 120) : new Color(0xE2, 0xE8, 0xF0);
            textColor = AppTheme.getTextMuted();
        } else if (isPressed) {
            baseBg = adjustBrightness(baseBg, -0.2f);
        } else if (isHovered) {
            baseBg = style == ButtonStyle.GHOST ? (AppTheme.isDarkMode() ? new Color(0xFF, 0xFF, 0xFF, 25) : new Color(0x00, 0x00, 0x00, 15)) : adjustBrightness(baseBg, 0.12f);
        }

        // Fill background
        if (baseBg.getAlpha() > 0) {
            g2.setColor(baseBg);
            g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        // Draw border if needed
        if (borderColor != null || style == ButtonStyle.GHOST || style == ButtonStyle.SECONDARY) {
            g2.setColor(borderColor != null ? borderColor : AppTheme.getBorder());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        setForeground(textColor);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color adjustBrightness(Color c, float factor) {
        if (c.getAlpha() == 0) return c;
        int r = Math.min(255, Math.max(0, (int) (c.getRed() + 255 * factor)));
        int g = Math.min(255, Math.max(0, (int) (c.getGreen() + 255 * factor)));
        int b = Math.min(255, Math.max(0, (int) (c.getBlue() + 255 * factor)));
        return new Color(r, g, b, c.getAlpha());
    }
}
