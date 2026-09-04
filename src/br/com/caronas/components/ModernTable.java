package br.com.caronas.components;

import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        init();
    }

    private void init() {
        setRowHeight(42);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 1));
        setFont(AppTheme.FONT_BODY);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);

        JTableHeader header = getTableHeader();
        header.setFont(AppTheme.FONT_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new HeaderRenderer());

        setDefaultRenderer(Object.class, new ModernCellRenderer());
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xEA, 0xEE, 0xF4));
            setForeground(AppTheme.isDarkMode() ? ModernColors.PRIMARY_LIGHT : ModernColors.PRIMARY_HOVER);
            setFont(AppTheme.FONT_HEADER);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            return this;
        }
    }

    private static class ModernCellRenderer extends DefaultTableCellRenderer {
        public ModernCellRenderer() {
            setBorder(new EmptyBorder(6, 14, 6, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                setBackground(AppTheme.isDarkMode() ? new Color(0x3B, 0x47, 0x64) : new Color(0xEE, 0xF2, 0xFF));
                setForeground(AppTheme.isDarkMode() ? Color.WHITE : ModernColors.PRIMARY_HOVER);
            } else {
                if (row % 2 == 0) {
                    setBackground(AppTheme.isDarkMode() ? ModernColors.DARK_CARD : ModernColors.LIGHT_CARD);
                } else {
                    setBackground(AppTheme.isDarkMode() ? new Color(0x18, 0x22, 0x34) : new Color(0xF8, 0xFA, 0xFC));
                }
                setForeground(AppTheme.getTextPrimary());
            }

            setFont(AppTheme.FONT_BODY);
            return this;
        }
    }
}
