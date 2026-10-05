package br.com.caronas.components;

import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

public class StarRatingPanel extends JPanel {
    private int rating = 5;
    private int hoveredRating = 0;
    private final boolean interactive;
    private final int starSize;

    public StarRatingPanel(int initialRating, boolean interactive) {
        this(initialRating, interactive, 22);
    }

    public StarRatingPanel(int initialRating, boolean interactive, int starSize) {
        this.rating = Math.max(1, Math.min(5, initialRating));
        this.interactive = interactive;
        this.starSize = starSize;

        setOpaque(false);
        setPreferredSize(new Dimension(starSize * 5 + 20, starSize + 8));

        if (interactive) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int clicked = getStarIndexAt(e.getX());
                    if (clicked >= 1 && clicked <= 5) {
                        setRating(clicked);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredRating = 0;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int hovered = getStarIndexAt(e.getX());
                    if (hovered != hoveredRating) {
                        hoveredRating = hovered;
                        repaint();
                    }
                }
            });
        }
    }

    private int getStarIndexAt(int x) {
        int idx = (x / (starSize + 4)) + 1;
        return Math.max(1, Math.min(5, idx));
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
        repaint();
    }

    public int getRating() {
        return rating;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int activeCount = hoveredRating > 0 ? hoveredRating : rating;

        for (int i = 0; i < 5; i++) {
            int cx = i * (starSize + 4) + starSize / 2 + 2;
            int cy = getHeight() / 2;
            boolean filled = (i + 1) <= activeCount;

            drawStar(g2, cx, cy, starSize / 2.0, starSize / 4.5, filled);
        }

        g2.dispose();
    }

    private void drawStar(Graphics2D g2, double centerX, double centerY, double outerRadius, double innerRadius, boolean filled) {
        Path2D path = new Path2D.Double();
        double angle = -Math.PI / 2.0;
        double step = Math.PI / 5.0;

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = centerX + r * Math.cos(angle);
            double y = centerY + r * Math.sin(angle);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            angle += step;
        }
        path.closePath();

        if (filled) {
            g2.setColor(ModernColors.GOLD);
            g2.fill(path);
            g2.setColor(new Color(0xD9, 0x77, 0x06));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(path);
        } else {
            g2.setColor(new Color(0x64, 0x74, 0x8B));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(path);
        }
    }
}
