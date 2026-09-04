package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius = 14;
    private Color customBackground = null;
    private Color customBorderColor = null;
    private boolean drawBorder = true;
    private boolean showShadow = false;

    public RoundedPanel() {
        this(14);
    }

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public void setCustomBackground(Color bg) {
        this.customBackground = bg;
        repaint();
    }

    public void setCustomBorderColor(Color border) {
        this.customBorderColor = border;
        repaint();
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        repaint();
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background
        Color bg = customBackground != null ? customBackground : AppTheme.getCardBackground();
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border
        if (drawBorder) {
            Color border = customBorderColor != null ? customBorderColor : AppTheme.getBorder();
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
