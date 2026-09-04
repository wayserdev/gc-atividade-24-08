package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToastNotification extends JWindow {
    public enum ToastType {
        SUCCESS, ERROR, WARNING, INFO
    }

    private static ToastNotification activeToast = null;

    public static void show(Component parent, String title, String message, ToastType type) {
        if (activeToast != null) {
            activeToast.dispose();
            activeToast = null;
        }

        Window owner = SwingUtilities.getWindowAncestor(parent);
        if (owner == null && parent instanceof Window) {
            owner = (Window) parent;
        }

        ToastNotification toast = new ToastNotification(owner, title, message, type);
        activeToast = toast;
        toast.display();
    }

    private Timer autoDismissTimer;

    private ToastNotification(Window owner, String title, String message, ToastType type) {
        super(owner);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        Color accentColor;
        String iconSymbol;
        switch (type) {
            case SUCCESS:
                accentColor = ModernColors.SUCCESS;
                iconSymbol = "✓";
                break;
            case ERROR:
                accentColor = ModernColors.DANGER;
                iconSymbol = "✕";
                break;
            case WARNING:
                accentColor = ModernColors.WARNING;
                iconSymbol = "⚠";
                break;
            case INFO:
            default:
                accentColor = ModernColors.INFO;
                iconSymbol = "ℹ";
                break;
        }

        RoundedPanel container = new RoundedPanel(12);
        container.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x1E, 0x29, 0x3B, 245) : new Color(0xFF, 0xFF, 0xFF, 245));
        container.setCustomBorderColor(accentColor);
        container.setLayout(new BorderLayout(12, 0));
        container.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Icon Pill
        JLabel lblIcon = new JLabel(iconSymbol, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setPreferredSize(new Dimension(28, 28));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Content
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        contentPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_BODY_BOLD);
        lblTitle.setForeground(accentColor);

        JLabel lblMsg = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        lblMsg.setFont(AppTheme.FONT_SMALL);
        lblMsg.setForeground(AppTheme.getTextPrimary());

        contentPanel.add(lblTitle);
        contentPanel.add(lblMsg);

        // Close Button
        JLabel btnClose = new JLabel("✕");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setForeground(AppTheme.getTextMuted());
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dismiss();
            }
        });

        container.add(lblIcon, BorderLayout.WEST);
        container.add(contentPanel, BorderLayout.CENTER);
        container.add(btnClose, BorderLayout.EAST);

        add(container);
        pack();
    }

    private void display() {
        Window owner = getOwner();
        if (owner != null && owner.isVisible()) {
            Point loc = owner.getLocationOnScreen();
            int x = loc.x + (owner.getWidth() - getWidth()) / 2;
            int y = loc.y + 45; // near top
            setLocation(x, y);
        } else {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screen.width - getWidth()) / 2, 50);
        }

        setAlwaysOnTop(true);
        setVisible(true);

        autoDismissTimer = new Timer(4000, e -> dismiss());
        autoDismissTimer.setRepeats(false);
        autoDismissTimer.start();
    }

    private void dismiss() {
        if (autoDismissTimer != null && autoDismissTimer.isRunning()) {
            autoDismissTimer.stop();
        }
        dispose();
        if (activeToast == this) {
            activeToast = null;
        }
    }
}
