package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends RoundedPanel {
    private JLabel lblTag;
    private JLabel lblValue;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private Color accentColor;

    public StatCard(String tagText, String value, String title, String subtitle, Color accentColor) {
        super(16);
        this.accentColor = accentColor != null ? accentColor : ModernColors.PRIMARY;
        initUI(tagText, value, title, subtitle);
    }

    private void initUI(String tagText, String value, String title, String subtitle) {
        setLayout(new BorderLayout(14, 0));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Tag Box
        RoundedPanel tagBox = new RoundedPanel(10);
        tagBox.setPreferredSize(new Dimension(80, 42));
        tagBox.setCustomBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
        tagBox.setLayout(new GridBagLayout());

        lblTag = new JLabel(tagText);
        lblTag.setFont(AppTheme.FONT_SMALL_BOLD);
        lblTag.setForeground(accentColor);
        tagBox.add(lblTag);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(AppTheme.getTextPrimary());

        lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_BODY_BOLD);
        lblTitle.setForeground(AppTheme.getTextSecondary());

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(AppTheme.FONT_SMALL);
        lblSubtitle.setForeground(AppTheme.getTextMuted());

        content.add(lblValue);
        content.add(Box.createVerticalStrut(2));
        content.add(lblTitle);
        if (subtitle != null && !subtitle.isEmpty()) {
            content.add(Box.createVerticalStrut(2));
            content.add(lblSubtitle);
        }

        add(tagBox, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    public void updateValues(String value, String subtitle) {
        lblValue.setText(value);
        if (subtitle != null) lblSubtitle.setText(subtitle);
        repaint();
    }
}
