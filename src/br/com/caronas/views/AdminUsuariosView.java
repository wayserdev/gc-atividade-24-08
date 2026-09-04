package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.PageResult;
import br.com.caronas.model.Usuario;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUsuariosView extends JPanel {
    private final ApiService apiService;
    private final Runnable onRoleSwitchRequested;

    private ModernTextField txtBusca;
    private JComboBox<String> cbNivelFiltro;
    private JComboBox<Integer> cbLimite;
    private JLabel lblTotalRegistros;
    private JLabel lblPaginaInfo;

    private ModernTable table;
    private UsuariosTableModel tableModel;
    private List<Usuario> currentUsuarios = new ArrayList<>();

    private int currentPage = 1;
    private int totalPages = 1;
    private int totalRegistros = 0;

    private JPanel forbiddenPanel;
    private JPanel mainAdminPanel;
    private CardLayout cardLayout;

    public AdminUsuariosView(ApiService apiService, Runnable onRoleSwitchRequested) {
        this.apiService = apiService;
        this.onRoleSwitchRequested = onRoleSwitchRequested;

        initUI();
    }

    private void initUI() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setOpaque(false);

        buildAdminPanel();
        buildForbiddenPanel();

        add(mainAdminPanel, "ADMIN");
        add(forbiddenPanel, "FORBIDDEN");

        checkAccessAndLoad();
    }

    private void buildAdminPanel() {
        mainAdminPanel = new JPanel(new BorderLayout(0, 14));
        mainAdminPanel.setOpaque(false);
        mainAdminPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // TOP TOOLBAR CARD
        RoundedPanel toolbarCard = new RoundedPanel(14);
        toolbarCard.setLayout(new BorderLayout(14, 0));
        toolbarCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftToolbar.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍");
        txtBusca = new ModernTextField("Buscar por nome ou e-mail...", 16);
        txtBusca.setPreferredSize(new Dimension(240, 36));
        txtBusca.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });

        cbNivelFiltro = new JComboBox<>(new String[]{"TODOS", "ALUNO", "ADMIN"});
        cbNivelFiltro.setFont(AppTheme.FONT_BODY);
        cbNivelFiltro.setPreferredSize(new Dimension(100, 36));
        cbNivelFiltro.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });

        RoundedButton btnBuscar = new RoundedButton("Filtrar", RoundedButton.ButtonStyle.PRIMARY);
        btnBuscar.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });

        leftToolbar.add(lblSearch);
        leftToolbar.add(txtBusca);
        leftToolbar.add(new JLabel("Nível:"));
        leftToolbar.add(cbNivelFiltro);
        leftToolbar.add(btnBuscar);

        JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightToolbar.setOpaque(false);

        lblTotalRegistros = new JLabel("Total: 0 usuários");
        lblTotalRegistros.setFont(AppTheme.FONT_SMALL_BOLD);
        lblTotalRegistros.setForeground(ModernColors.PRIMARY_LIGHT);

        RoundedButton btnEdit = new RoundedButton("✏️ Editar/Promover", RoundedButton.ButtonStyle.SECONDARY);
        btnEdit.addActionListener(e -> onEditSelectedUser());

        RoundedButton btnDelete = new RoundedButton("🗑️ Excluir", RoundedButton.ButtonStyle.DANGER);
        btnDelete.addActionListener(e -> onDeleteSelectedUser());

        rightToolbar.add(lblTotalRegistros);
        rightToolbar.add(btnEdit);
        rightToolbar.add(btnDelete);

        toolbarCard.add(leftToolbar, BorderLayout.WEST);
        toolbarCard.add(rightToolbar, BorderLayout.EAST);

        // TABLE IN ROUNDED CONTAINER
        RoundedPanel tableCard = new RoundedPanel(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(8, 8, 8, 8));

        tableModel = new UsuariosTableModel();
        table = new ModernTable(tableModel);

        // Custom column renderers
        table.getColumnModel().getColumn(2).setCellRenderer(new RoleBadgeCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new RatingCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        // PAGINATION BAR
        RoundedPanel paginationCard = new RoundedPanel(10);
        paginationCard.setLayout(new BorderLayout());
        paginationCard.setBorder(new EmptyBorder(8, 16, 8, 16));

        JPanel leftPagination = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPagination.setOpaque(false);
        leftPagination.add(new JLabel("Itens por página:"));
        cbLimite = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        cbLimite.setSelectedItem(10);
        cbLimite.addActionListener(e -> {
            currentPage = 1;
            loadData();
        });
        leftPagination.add(cbLimite);

        JPanel rightPagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPagination.setOpaque(false);

        lblPaginaInfo = new JLabel("Página 1 de 1");
        lblPaginaInfo.setFont(AppTheme.FONT_SMALL_BOLD);

        RoundedButton btnPrev = new RoundedButton("◀ Anterior", RoundedButton.ButtonStyle.GHOST);
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadData();
            }
        });

        RoundedButton btnNext = new RoundedButton("Próxima ▶", RoundedButton.ButtonStyle.GHOST);
        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadData();
            }
        });

        rightPagination.add(lblPaginaInfo);
        rightPagination.add(btnPrev);
        rightPagination.add(btnNext);

        paginationCard.add(leftPagination, BorderLayout.WEST);
        paginationCard.add(rightPagination, BorderLayout.EAST);

        mainAdminPanel.add(toolbarCard, BorderLayout.NORTH);
        mainAdminPanel.add(tableCard, BorderLayout.CENTER);
        mainAdminPanel.add(paginationCard, BorderLayout.SOUTH);
    }

    private void buildForbiddenPanel() {
        forbiddenPanel = new JPanel(new GridBagLayout());
        forbiddenPanel.setOpaque(false);

        RoundedPanel card = new RoundedPanel(16);
        card.setPreferredSize(new Dimension(540, 360));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblLock = new JLabel("🚫 403 Forbidden", SwingConstants.CENTER);
        lblLock.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLock.setForeground(ModernColors.DANGER);
        lblLock.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Acesso restrito para administradores", SwingConstants.CENTER);
        lblSub.setFont(AppTheme.FONT_SUBTITLE);
        lblSub.setForeground(AppTheme.getTextPrimary());
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>Você está atualmente autenticado com o perfil <b>ALUNO</b>.<br>De acordo com o controle de acesso RBAC da especificação técnica, apenas usuários com perfil <b>ADMIN</b> têm permissão para acessar a rota <code>/admin/usuarios</code>.</center></html>", SwingConstants.CENTER);
        lblDesc.setFont(AppTheme.FONT_BODY);
        lblDesc.setForeground(AppTheme.getTextSecondary());
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setBorder(new EmptyBorder(16, 10, 20, 10));

        RoundedButton btnSwitchAdmin = new RoundedButton("👑 Entrar como Administrador (Ana)", RoundedButton.ButtonStyle.ACCENT);
        btnSwitchAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSwitchAdmin.addActionListener(e -> {
            if (onRoleSwitchRequested != null) {
                onRoleSwitchRequested.run();
            }
        });

        content.add(lblLock);
        content.add(Box.createVerticalStrut(10));
        content.add(lblSub);
        content.add(lblDesc);
        content.add(btnSwitchAdmin);

        card.add(content, BorderLayout.CENTER);
        forbiddenPanel.add(card);
    }

    public void checkAccessAndLoad() {
        if (apiService.isAdmin()) {
            cardLayout.show(this, "ADMIN");
            loadData();
        } else {
            cardLayout.show(this, "FORBIDDEN");
            // Dispara chamada para gerar evento 403 no log
            apiService.getUsuariosAdmin("", "TODOS", 1, 10);
        }
    }

    public void loadData() {
        if (!apiService.isAdmin()) {
            cardLayout.show(this, "FORBIDDEN");
            return;
        }

        String busca = txtBusca.getText().trim();
        String nivel = (String) cbNivelFiltro.getSelectedItem();
        int limite = cbLimite.getSelectedItem() != null ? (Integer) cbLimite.getSelectedItem() : 10;

        ApiResponse<PageResult<Usuario>> resp = apiService.getUsuariosAdmin(busca, nivel, currentPage, limite);
        if (resp.isSuccess()) {
            PageResult<Usuario> result = resp.getData();
            currentUsuarios = result.getUsuarios();
            totalRegistros = result.getTotal();
            totalPages = Math.max(1, result.getTotalPaginas());

            tableModel.fireTableDataChanged();
            lblTotalRegistros.setText(String.format("Total: %d usuário(s)", totalRegistros));
            lblPaginaInfo.setText(String.format("Página %d de %d (%d registros)", currentPage, totalPages, totalRegistros));
        } else {
            ToastNotification.show(this, "Erro ao listar (" + resp.getStatusCode() + ")", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }

    private void onEditSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentUsuarios.size()) {
            ToastNotification.show(this, "Seleção necessária", "Selecione um usuário na tabela para editar.", ToastNotification.ToastType.WARNING);
            return;
        }

        Usuario u = currentUsuarios.get(row);
        UsuarioEditDialog dialog = new UsuarioEditDialog(SwingUtilities.getWindowAncestor(this), apiService, u);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            ToastNotification.show(this, "Usuário Atualizado!", "Dados do usuário atualizados pelo administrador com sucesso.", ToastNotification.ToastType.SUCCESS);
            loadData();
        }
    }

    private void onDeleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentUsuarios.size()) {
            ToastNotification.show(this, "Seleção necessária", "Selecione um usuário na tabela para excluir.", ToastNotification.ToastType.WARNING);
            return;
        }

        Usuario u = currentUsuarios.get(row);

        // Regra de auto-bloqueio
        Usuario adminLogado = apiService.getUsuarioLogado();
        if (adminLogado != null && adminLogado.getId().equals(u.getId())) {
            ToastNotification.show(this, "Ação Não Permitida (400 Bad Request)",
                    "O Administrador não pode deletar a si mesmo (evitar auto-bloqueio).", ToastNotification.ToastType.ERROR);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o usuário " + u.getNome_completo() + " (" + u.getEmail() + ")?\n\nEsta ação cancelará automaticamente quaisquer caronas associadas.",
                "Confirmar Exclusão (DELETE /admin/usuarios/:id)",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {
            ApiResponse<String> resp = apiService.deleteUsuarioAdmin(u.getId());
            if (resp.isSuccess()) {
                ToastNotification.show(this, "Usuário Removido", resp.getData(), ToastNotification.ToastType.SUCCESS);
                loadData();
            } else {
                ToastNotification.show(this, "Erro ao excluir", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
            }
        }
    }

    private class UsuariosTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Nome Completo", "Nível (RBAC)", "E-mail", "Telefone", "Avaliação", "Curso"};

        @Override
        public int getRowCount() {
            return currentUsuarios.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= currentUsuarios.size()) return null;
            Usuario u = currentUsuarios.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return u.getId().length() > 8 ? u.getId().substring(0, 8) + "..." : u.getId();
                case 1:
                    return u.getNome_completo();
                case 2:
                    return u.getNivel();
                case 3:
                    return u.getEmail();
                case 4:
                    return u.getTelefone();
                case 5:
                    return u.getMedia_avaliacao();
                case 6:
                    return u.getCurso();
                default:
                    return "";
            }
        }
    }

    private static class RoleBadgeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String role = value != null ? value.toString() : "ALUNO";
            BadgeLabel badge = BadgeLabel.forRole(role);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            p.setOpaque(false);
            p.add(badge);
            return p;
        }
    }

    private static class RatingCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String val = value != null ? value.toString() : "0.00";
            setText("⭐ " + val);
            setForeground(ModernColors.GOLD);
            setFont(AppTheme.FONT_BODY_BOLD);
            return this;
        }
    }
}
