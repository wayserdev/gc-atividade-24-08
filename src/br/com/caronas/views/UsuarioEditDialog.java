package br.com.caronas.views;

import br.com.caronas.components.ModernTextField;
import br.com.caronas.components.RoundedButton;
import br.com.caronas.components.RoundedPanel;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.Usuario;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UsuarioEditDialog extends JDialog {
    private final ApiService apiService;
    private final Usuario usuario;
    private boolean saved = false;

    private ModernTextField txtNome;
    private ModernTextField txtEmail;
    private JComboBox<String> cbNivel;
    private ModernTextField txtTelefone;
    private ModernTextField txtCurso;
    private JLabel lblFeedback;

    public UsuarioEditDialog(Window owner, ApiService apiService, Usuario usuario) {
        super(owner, "Editar Usuário (Admin) — " + usuario.getNome_completo(), ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.usuario = usuario;

        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setResizable(false);

        RoundedPanel rootPanel = new RoundedPanel(16);
        rootPanel.setLayout(new BorderLayout(0, 16));
        rootPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Editar Dados do Usuário");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Rota PUT /admin/usuarios/:id (Apenas ADMIN)");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(ModernColors.PRIMARY_LIGHT);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        formPanel.setOpaque(false);

        txtNome = new ModernTextField("Nome Completo");
        txtNome.setText(usuario.getNome_completo());

        txtEmail = new ModernTextField("E-mail");
        txtEmail.setText(usuario.getEmail());

        // ComboBox de Nível (ALUNO / ADMIN)
        cbNivel = new JComboBox<>(new String[]{"ALUNO", "ADMIN"});
        cbNivel.setSelectedItem(usuario.getNivel() != null ? usuario.getNivel().toUpperCase() : "ALUNO");
        cbNivel.setFont(AppTheme.FONT_BODY);

        txtTelefone = new ModernTextField("Telefone com DDD");
        txtTelefone.setText(usuario.getTelefone() != null ? usuario.getTelefone() : "");

        txtCurso = new ModernTextField("Curso / Departamento");
        txtCurso.setText(usuario.getCurso() != null ? usuario.getCurso() : "");

        formPanel.add(createFieldGroup("Nome Completo:", txtNome));
        formPanel.add(createFieldGroup("E-mail:", txtEmail));
        formPanel.add(createFieldGroup("Nível de Acesso (RBAC):", cbNivel));
        formPanel.add(createFieldGroup("Telefone:", txtTelefone));
        formPanel.add(createFieldGroup("Curso / Departamento:", txtCurso));

        // Feedback label
        lblFeedback = new JLabel(" ", SwingConstants.CENTER);
        lblFeedback.setFont(AppTheme.FONT_SMALL_BOLD);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancelar", RoundedButton.ButtonStyle.GHOST);
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton("Salvar Alterações", RoundedButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> saveChanges());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.add(lblFeedback, BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(rootPanel, BorderLayout.CENTER);
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(8, 2));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AppTheme.FONT_SMALL_BOLD);
        lbl.setForeground(AppTheme.getTextSecondary());
        lbl.setPreferredSize(new Dimension(140, 26));
        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void saveChanges() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String nivel = (String) cbNivel.getSelectedItem();
        String telefone = txtTelefone.getText().trim();
        String curso = txtCurso.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            lblFeedback.setText("Nome e E-mail são obrigatórios.");
            lblFeedback.setForeground(ModernColors.DANGER);
            return;
        }

        ApiResponse<Usuario> resp = apiService.updateUsuarioAdmin(usuario.getId(), nome, email, nivel, telefone, curso);
        if (resp.isSuccess()) {
            saved = true;
            dispose();
        } else {
            lblFeedback.setText(resp.getError().getMensagemFormatada());
            lblFeedback.setForeground(ModernColors.DANGER);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
