package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ModernTextField extends JTextField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 10;

    public ModernTextField() {
        this("");
    }

    public ModernTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    public ModernTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setFont(AppTheme.FONT_BODY);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 14, 10, 14));
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
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(AppTheme.getTextMuted());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }
}
