package br.com.caronas.components;

import br.com.caronas.theme.ModernColors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

public class AvatarPanel extends JComponent {
    private int size;
    private String name = "";
    private String imageUrl = null;
    private Image loadedImage = null;

    public AvatarPanel(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setOpaque(false);
    }

    public AvatarPanel(String name, int size) {
        this(size);
        this.name = name;
    }

    public void setUser(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.loadedImage = null;
        if (imageUrl != null && imageUrl.startsWith("http")) {
            loadImageAsync(imageUrl);
        }
        repaint();
    }

    private void loadImageAsync(String urlStr) {
        new SwingWorker<Image, Void>() {
            @Override
            protected Image doInBackground() {
                try {
                    URL url = URI.create(urlStr).toURL();
                    return ImageIO.read(url);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    loadedImage = get();
                    if (loadedImage != null) {
                        repaint();
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private String getInitials() {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int d = Math.min(getWidth(), getHeight());
        int x = (getWidth() - d) / 2;
        int y = (getHeight() - d) / 2;

        Shape circle = new Ellipse2D.Float(x, y, d, d);

        if (loadedImage != null) {
            g2.setClip(circle);
            g2.drawImage(loadedImage, x, y, d, d, null);
            g2.setClip(null);
        } else {
            // Gradient Background
            GradientPaint gradient = new GradientPaint(
                    x, y, ModernColors.PRIMARY,
                    x + d, y + d, ModernColors.ACCENT_PURPLE
            );
            g2.setPaint(gradient);
            g2.fill(circle);

            // Initials text
            g2.setColor(Color.WHITE);
            int fontSize = Math.max(12, d / 2 - 2);
            g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            String initials = getInitials();
            int tx = x + (d - fm.stringWidth(initials)) / 2;
            int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initials, tx, ty);
        }

        // Outer border
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(circle);

        g2.dispose();
    }
}
