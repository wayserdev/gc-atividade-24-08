package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernPasswordField extends JPasswordField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 10;
    private boolean showPassword = false;
    private char defaultEchoChar;

    public ModernPasswordField() {
        this("");
    }

    public ModernPasswordField(String placeholder) {
        super();
        this.placeholder = placeholder;
        this.defaultEchoChar = getEchoChar();
        init();
    }

    private void init() {
        setFont(AppTheme.FONT_BODY);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 14, 10, 36));
        setCaretColor(ModernColors.PRIMARY_LIGHT);

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Click on eye icon region at the right
                int iconX = getWidth() - 32;
                if (e.getX() >= iconX && e.getX() <= getWidth() - 8) {
                    toggleShowPassword();
                }
            }
        });
    }

    public void toggleShowPassword() {
        showPassword = !showPassword;
        if (showPassword) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar != 0 ? defaultEchoChar : '•');
        }
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background
        Color bg = !isEnabled() ? AppTheme.getSurface() : (AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : Color.WHITE);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border / Focus Glow
        if (isFocused && isEnabled()) {
            g2.setColor(ModernColors.PRIMARY);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);
        } else {
            g2.setColor(AppTheme.getBorder());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        setForeground(isEnabled() ? AppTheme.getTextPrimary() : AppTheme.getTextMuted());
        super.paintComponent(g);

        // Placeholder
        if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(AppTheme.getTextMuted());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        }

        // Draw Eye icon
        int iconX = width - 26;
        int iconY = (height - 12) / 2;
        g2.setColor(showPassword ? ModernColors.PRIMARY : AppTheme.getTextMuted());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(iconX, iconY, 14, 10);
        if (!showPassword) {
            g2.fillOval(iconX + 4, iconY + 2, 6, 6);
        } else {
            g2.drawLine(iconX - 2, iconY + 12, iconX + 16, iconY - 2);
        }

        g2.dispose();
    }
}
