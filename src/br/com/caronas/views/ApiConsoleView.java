package br.com.caronas.views;

import br.com.caronas.components.BadgeLabel;
import br.com.caronas.components.ModernTable;
import br.com.caronas.components.RoundedButton;
import br.com.caronas.components.RoundedPanel;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class ApiConsoleView extends JPanel implements ApiService.ApiCallListener {
    private final ApiService apiService;
    private final List<ApiResponse<?>> logs = new ArrayList<>();

    private ModernTable tableLogs;
    private LogsTableModel tableModel;

    private JTextArea txtRequestDetails;
    private JTextArea txtResponseDetails;
    private JLabel lblSelectedEndpoint;
    private BadgeLabel badgeSelectedStatus;
    private BadgeLabel badgeSelectedMethod;

    public ApiConsoleView(ApiService apiService) {
        this.apiService = apiService;
        this.apiService.addApiCallListener(this);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 16, 24));

        // TOP HEADER
        RoundedPanel headerPanel = new RoundedPanel(14);
        headerPanel.setLayout(new BorderLayout(16, 0));
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("📡 Console de Auditoria e Inspeção de Rotas HTTP / JWT");
        lblTitle.setFont(AppTheme.FONT_SUBTITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Monitoramento em tempo real dos contratos REST e respostas JSON da Especificação Técnica");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        RoundedButton btnClear = new RoundedButton("🗑️ Limpar Histórico", RoundedButton.ButtonStyle.GHOST);
        btnClear.addActionListener(e -> {
            logs.clear();
            tableModel.fireTableDataChanged();
            clearDetails();
        });

        RoundedButton btnCopyJson = new RoundedButton("📋 Copiar Resposta", RoundedButton.ButtonStyle.SECONDARY);
        btnCopyJson.addActionListener(e -> {
            String txt = txtResponseDetails.getText();
            if (!txt.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt), null);
            }
        });

        actionPanel.add(btnClear);
        actionPanel.add(btnCopyJson);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // SPLIT PANE (Top: Table, Bottom: JSON Details)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.4);

        // Top: Table of requests
        RoundedPanel tableCard = new RoundedPanel(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(8, 8, 8, 8));

        tableModel = new LogsTableModel();
        tableLogs = new ModernTable(tableModel);
        tableLogs.getColumnModel().getColumn(1).setCellRenderer(new MethodCellRenderer());
        tableLogs.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        tableLogs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableLogs.getSelectedRow();
                if (row >= 0 && row < logs.size()) {
                    showDetails(logs.get(row));
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableLogs);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.setOpaque(false);
        scrollTable.getViewport().setOpaque(false);
        tableCard.add(scrollTable, BorderLayout.CENTER);

        // Bottom: Request/Response JSON view
        RoundedPanel detailsCard = new RoundedPanel(14);
        detailsCard.setLayout(new BorderLayout(0, 10));
        detailsCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel detailHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailHeader.setOpaque(false);

        badgeSelectedMethod = BadgeLabel.forMethod("GET");
        badgeSelectedStatus = BadgeLabel.forStatus(200);
        lblSelectedEndpoint = new JLabel("Selecione uma requisição acima para inspecionar");
        lblSelectedEndpoint.setFont(AppTheme.FONT_BODY_BOLD);

        detailHeader.add(badgeSelectedMethod);
        detailHeader.add(badgeSelectedStatus);
        detailHeader.add(lblSelectedEndpoint);

        JPanel jsonGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        jsonGrid.setOpaque(false);

        // Left JSON: Request
        JPanel reqBox = new JPanel(new BorderLayout(0, 4));
        reqBox.setOpaque(false);
        JLabel lblReq = new JLabel("REQUEST (Headers & Payload):");
        lblReq.setFont(AppTheme.FONT_SMALL_BOLD);
        lblReq.setForeground(AppTheme.getTextSecondary());

        txtRequestDetails = new JTextArea();
        txtRequestDetails.setFont(AppTheme.FONT_CODE);
        txtRequestDetails.setEditable(false);
        txtRequestDetails.setOpaque(false);
        txtRequestDetails.setBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xF1, 0xF5, 0xF9));
        txtRequestDetails.setForeground(AppTheme.getTextPrimary());
        JScrollPane scrollReq = new JScrollPane(txtRequestDetails);
        reqBox.add(lblReq, BorderLayout.NORTH);
        reqBox.add(scrollReq, BorderLayout.CENTER);

        // Right JSON: Response
        JPanel respBox = new JPanel(new BorderLayout(0, 4));
        respBox.setOpaque(false);
        JLabel lblResp = new JLabel("RESPONSE BODY (JSON):");
        lblResp.setFont(AppTheme.FONT_SMALL_BOLD);
        lblResp.setForeground(AppTheme.getTextSecondary());

        txtResponseDetails = new JTextArea();
        txtResponseDetails.setFont(AppTheme.FONT_CODE);
        txtResponseDetails.setEditable(false);
        txtResponseDetails.setOpaque(false);
        txtResponseDetails.setBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xF1, 0xF5, 0xF9));
        txtResponseDetails.setForeground(AppTheme.getTextPrimary());
        JScrollPane scrollResp = new JScrollPane(txtResponseDetails);
        respBox.add(lblResp, BorderLayout.NORTH);
        respBox.add(scrollResp, BorderLayout.CENTER);

        jsonGrid.add(reqBox);
        jsonGrid.add(respBox);

        detailsCard.add(detailHeader, BorderLayout.NORTH);
        detailsCard.add(jsonGrid, BorderLayout.CENTER);

        splitPane.setTopComponent(tableCard);
        splitPane.setBottomComponent(detailsCard);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void showDetails(ApiResponse<?> resp) {
        badgeSelectedMethod.setText(resp.getMethod());
        badgeSelectedMethod.setBadgeColors(
                resp.getMethod().equals("POST") ? ModernColors.SUCCESS :
                        resp.getMethod().equals("PUT") ? ModernColors.WARNING :
                                resp.getMethod().equals("DELETE") ? ModernColors.DANGER : ModernColors.INFO,
                Color.WHITE
        );

        badgeSelectedStatus.setText(resp.getStatusCode() + " " + (resp.isSuccess() ? "OK" : (resp.getError() != null ? resp.getError().getErro() : "ERROR")));
        badgeSelectedStatus.setBadgeColors(
                resp.getStatusCode() < 300 ? ModernColors.SUCCESS :
                        (resp.getStatusCode() == 400 || resp.getStatusCode() == 409) ? ModernColors.WARNING : ModernColors.DANGER,
                Color.WHITE
        );

        lblSelectedEndpoint.setText(resp.getEndpoint());

        StringBuilder reqText = new StringBuilder();
        if (!resp.getRequestHeaders().isEmpty()) {
            reqText.append("// Headers:\n");
            resp.getRequestHeaders().forEach((k, v) -> reqText.append(k).append(": ").append(v).append("\n"));
            reqText.append("\n");
        }
        if (resp.getRequestBody() != null) {
            reqText.append("// Request Body:\n").append(resp.getRequestBody());
        } else {
            reqText.append("// No Body");
        }
        txtRequestDetails.setText(reqText.toString());
        txtRequestDetails.setCaretPosition(0);

        txtResponseDetails.setText(resp.getResponseBody() != null ? resp.getResponseBody() : "// No Response Body");
        txtResponseDetails.setCaretPosition(0);
    }

    private void clearDetails() {
        lblSelectedEndpoint.setText("Selecione uma requisição acima para inspecionar");
        txtRequestDetails.setText("");
        txtResponseDetails.setText("");
    }

    @Override
    public void onApiCall(ApiResponse<?> response) {
        SwingUtilities.invokeLater(() -> {
            logs.add(0, response);
            tableModel.fireTableDataChanged();
            tableLogs.setRowSelectionInterval(0, 0);
            showDetails(response);
        });
    }

    private class LogsTableModel extends AbstractTableModel {
        private final String[] cols = {"Hora", "Método", "Rota / Endpoint", "Status HTTP", "Resultado"};

        @Override
        public int getRowCount() {
            return logs.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= logs.size()) return null;
            ApiResponse<?> r = logs.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return r.getTimestamp();
                case 1:
                    return r.getMethod();
                case 2:
                    return r.getEndpoint();
                case 3:
                    return r.getStatusCode();
                case 4:
                    return r.isSuccess() ? "✓ Sucesso" : ("✕ " + (r.getError() != null ? r.getError().getErro() : "Erro"));
                default:
                    return "";
            }
        }
    }

    private static class MethodCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String m = value != null ? value.toString() : "GET";
            BadgeLabel badge = BadgeLabel.forMethod(m);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            p.setOpaque(false);
            p.add(badge);
            return p;
        }
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int code = value instanceof Integer ? (Integer) value : 200;
            BadgeLabel badge = BadgeLabel.forStatus(code);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            p.setOpaque(false);
            p.add(badge);
            return p;
        }
    }
}
