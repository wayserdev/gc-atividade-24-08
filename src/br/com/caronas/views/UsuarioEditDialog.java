package br.com.caronas.views;

import br.com.caronas.components.*;
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
    private final Runnable onSuccess;

    private ModernTextField txtNome;
    private ModernTextField txtEmail;
    private JComboBox<String> cbNivel;
    private ModernTextField txtTelefone;
    private ModernTextField txtCurso;
    private JLabel lblFeedback;

    public UsuarioEditDialog(Window owner, ApiService apiService, Usuario usuario, Runnable onSuccess) {
        super(owner, "Editar Usuario (Admin) - " + usuario.getNome_completo(), ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.usuario = usuario;
        this.onSuccess = onSuccess;

        initUI();
    }

    private void initUI() {
        setSize(480, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(AppTheme.getBackground());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Editar Dados do Usuario");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Alteracoes realizadas pelo Administrador (RBAC)");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        formPanel.setOpaque(false);

        txtNome = new ModernTextField(usuario.getNome_completo());
        txtEmail = new ModernTextField(usuario.getEmail());

        cbNivel = new JComboBox<>(new String[]{"ALUNO", "ADMIN"});
        cbNivel.setFont(AppTheme.FONT_BODY);
        cbNivel.setSelectedItem(usuario.getNivel() != null ? usuario.getNivel().toUpperCase() : "ALUNO");

        txtTelefone = new ModernTextField(usuario.getTelefone() != null ? usuario.getTelefone() : "");
        txtCurso = new ModernTextField(usuario.getCurso() != null ? usuario.getCurso() : "");

        formPanel.add(createFieldGroup("Nome Completo:", txtNome));
        formPanel.add(createFieldGroup("E-mail:", txtEmail));
        formPanel.add(createFieldGroup("Nivel de Acesso (RBAC):", cbNivel));
        formPanel.add(createFieldGroup("Telefone:", txtTelefone));
        formPanel.add(createFieldGroup("Curso:", txtCurso));

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);

        lblFeedback = new JLabel(" ", SwingConstants.CENTER);
        lblFeedback.setFont(AppTheme.FONT_SMALL_BOLD);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancelar", RoundedButton.ButtonStyle.GHOST);
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton("Salvar Alteracoes", RoundedButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> executeSave());

        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        bottomPanel.add(lblFeedback, BorderLayout.NORTH);
        bottomPanel.add(buttonRow, BorderLayout.SOUTH);

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createFieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void executeSave() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String nivel = (String) cbNivel.getSelectedItem();
        String telefone = txtTelefone.getText().trim();
        String curso = txtCurso.getText().trim();

        if (nome.isEmpty() || email.isEmpty()) {
            lblFeedback.setText("Nome e E-mail sao obrigatorios.");
            lblFeedback.setForeground(ModernColors.DANGER);
            return;
        }

        ApiResponse<Usuario> resp = apiService.updateUsuarioAdmin(usuario.getId(), nome, email, nivel, telefone, curso);
        if (resp.isSuccess()) {
            ToastNotification.show(getOwner(), "Usuario Atualizado!", "Alteracoes gravadas com sucesso.", ToastNotification.ToastType.SUCCESS);
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();
        } else {
            lblFeedback.setText(resp.getError().getMensagemFormatada());
            lblFeedback.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro ao salvar", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
