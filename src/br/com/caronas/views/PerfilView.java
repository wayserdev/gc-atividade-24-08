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
import java.awt.datatransfer.StringSelection;

public class PerfilView extends JPanel {
    private final ApiService apiService;
    private final Runnable onUserUpdated;

    private AvatarPanel avatarPanel;
    private JLabel lblNomeHeader;
    private JLabel lblEmailHeader;
    private BadgeLabel badgeRole;
    private JLabel lblRating;
    private JLabel lblInstituicao;
    private JLabel lblCriadoEm;
    private JLabel lblUserId;

    // Form inputs
    private ModernTextField txtNome;
    private ModernTextField txtTelefone;
    private ModernTextField txtCurso;
    private ModernTextField txtFotoUrl;

    // Readonly / Locked fields
    private ModernTextField txtEmailReadOnly;
    private ModernTextField txtNivelReadOnly;
    private ModernTextField txtRatingReadOnly;
    private ModernTextField txtIdReadOnly;

    private JLabel lblStatus;

    public PerfilView(ApiService apiService, Runnable onUserUpdated) {
        this.apiService = apiService;
        this.onUserUpdated = onUserUpdated;

        initUI();
        refreshUserData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // TOP HEADER CARD
        RoundedPanel headerCard = new RoundedPanel(16);
        headerCard.setLayout(new BorderLayout(20, 0));
        headerCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        avatarPanel = new AvatarPanel(80);

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);

        JPanel nameBadgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameBadgeRow.setOpaque(false);

        lblNomeHeader = new JLabel("Carregando...");
        lblNomeHeader.setFont(AppTheme.FONT_TITLE);
        lblNomeHeader.setForeground(AppTheme.getTextPrimary());

        badgeRole = BadgeLabel.forRole("ALUNO");

        nameBadgeRow.add(lblNomeHeader);
        nameBadgeRow.add(badgeRole);

        lblEmailHeader = new JLabel("email@exemplo.com");
        lblEmailHeader.setFont(AppTheme.FONT_BODY);
        lblEmailHeader.setForeground(AppTheme.getTextSecondary());

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        metaRow.setOpaque(false);
        metaRow.setBorder(new EmptyBorder(6, 0, 0, 0));

        lblRating = new JLabel("⭐ 0.00 / 5.00");
        lblRating.setFont(AppTheme.FONT_BODY_BOLD);
        lblRating.setForeground(ModernColors.GOLD);

        lblInstituicao = new JLabel("🏛️ Universidade");
        lblInstituicao.setFont(AppTheme.FONT_BODY);
        lblInstituicao.setForeground(AppTheme.getTextSecondary());

        lblCriadoEm = new JLabel("📅 Criado em: 2026-08-27");
        lblCriadoEm.setFont(AppTheme.FONT_SMALL);
        lblCriadoEm.setForeground(AppTheme.getTextMuted());

        metaRow.add(lblRating);
        metaRow.add(lblInstituicao);
        metaRow.add(lblCriadoEm);

        headerInfo.add(nameBadgeRow);
        headerInfo.add(Box.createVerticalStrut(4));
        headerInfo.add(lblEmailHeader);
        headerInfo.add(metaRow);

        headerCard.add(avatarPanel, BorderLayout.WEST);
        headerCard.add(headerInfo, BorderLayout.CENTER);

        // Action button on top right (Copy JWT Token)
        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        topActionPanel.setOpaque(false);
        RoundedButton btnCopyToken = new RoundedButton("🔑 Copiar Token JWT", RoundedButton.ButtonStyle.GHOST);
        btnCopyToken.setFont(AppTheme.FONT_SMALL_BOLD);
        btnCopyToken.addActionListener(e -> {
            String token = apiService.getToken();
            if (token != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(token), null);
                ToastNotification.show(this, "Token Copiado!", "Bearer JWT copiado para a área de transferência.", ToastNotification.ToastType.INFO);
            }
        });
        topActionPanel.add(btnCopyToken);
        headerCard.add(topActionPanel, BorderLayout.EAST);

        // MAIN CONTENT (Two Columns)
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        contentGrid.setOpaque(false);

        // LEFT CARD: Formulário Editável (PUT /usuarios/me)
        RoundedPanel editCard = new RoundedPanel(16);
        editCard.setLayout(new BorderLayout(0, 14));
        editCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblEditTitle = new JLabel("✏️ Editar Perfil do Usuário");
        lblEditTitle.setFont(AppTheme.FONT_SUBTITLE);
        lblEditTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblEditDesc = new JLabel("<html>Campos permitidos para atualização conforme rota <b>PUT /usuarios/me</b>:</html>");
        lblEditDesc.setFont(AppTheme.FONT_SMALL);
        lblEditDesc.setForeground(AppTheme.getTextSecondary());

        JPanel editHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        editHeader.setOpaque(false);
        editHeader.add(lblEditTitle);
        editHeader.add(lblEditDesc);

        JPanel editForm = new JPanel(new GridLayout(4, 1, 0, 10));
        editForm.setOpaque(false);

        txtNome = new ModernTextField("Nome Completo");
        txtTelefone = new ModernTextField("Telefone com DDD");
        txtCurso = new ModernTextField("Curso");
        txtFotoUrl = new ModernTextField("URL da Foto de Perfil (opcional)");

        editForm.add(createFieldGroup("NOME COMPLETO:", txtNome));
        editForm.add(createFieldGroup("TELEFONE:", txtTelefone));
        editForm.add(createFieldGroup("CURSO:", txtCurso));
        editForm.add(createFieldGroup("FOTO URL:", txtFotoUrl));

        JPanel editButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        editButtons.setOpaque(false);

        RoundedButton btnReset = new RoundedButton("Desfazer", RoundedButton.ButtonStyle.GHOST);
        btnReset.addActionListener(e -> refreshUserData());

        RoundedButton btnSave = new RoundedButton("Salvar Alterações (PUT)", RoundedButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> saveProfileChanges());

        editButtons.add(btnReset);
        editButtons.add(btnSave);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);

        JPanel editBottom = new JPanel(new BorderLayout(0, 8));
        editBottom.setOpaque(false);
        editBottom.add(lblStatus, BorderLayout.NORTH);
        editBottom.add(editButtons, BorderLayout.SOUTH);

        editCard.add(editHeader, BorderLayout.NORTH);
        editCard.add(editForm, BorderLayout.CENTER);
        editCard.add(editBottom, BorderLayout.SOUTH);

        // RIGHT CARD: Campos Protegidos e Detalhes do Sistema (Regra RBAC)
        RoundedPanel protectedCard = new RoundedPanel(16);
        protectedCard.setLayout(new BorderLayout(0, 14));
        protectedCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblProtTitle = new JLabel("🔒 Campos Protegidos (Regra de Negócio)");
        lblProtTitle.setFont(AppTheme.FONT_SUBTITLE);
        lblProtTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblProtDesc = new JLabel("<html>O aluno <b>NÃO</b> pode alterar diretamente estes campos na rota <code>/usuarios/me</code>:</html>");
        lblProtDesc.setFont(AppTheme.FONT_SMALL);
        lblProtDesc.setForeground(AppTheme.getTextSecondary());

        JPanel protHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        protHeader.setOpaque(false);
        protHeader.add(lblProtTitle);
        protHeader.add(lblProtDesc);

        JPanel protForm = new JPanel(new GridLayout(4, 1, 0, 10));
        protForm.setOpaque(false);

        txtEmailReadOnly = new ModernTextField();
        txtEmailReadOnly.setEnabled(false);

        txtNivelReadOnly = new ModernTextField();
        txtNivelReadOnly.setEnabled(false);

        txtRatingReadOnly = new ModernTextField();
        txtRatingReadOnly.setEnabled(false);

        txtIdReadOnly = new ModernTextField();
        txtIdReadOnly.setEnabled(false);

        protForm.add(createFieldGroup("E-MAIL (IMUTÁVEL):", txtEmailReadOnly));
        protForm.add(createFieldGroup("NÍVEL RBAC (ADMIN ONLY):", txtNivelReadOnly));
        protForm.add(createFieldGroup("MÉDIA DE AVALIAÇÃO:", txtRatingReadOnly));
        protForm.add(createFieldGroup("UUID DO USUÁRIO:", txtIdReadOnly));

        // Info box inside right card
        RoundedPanel infoBox = new RoundedPanel(10);
        infoBox.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xEE, 0xF2, 0xFF));
        infoBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        infoBox.setLayout(new BorderLayout());
        JLabel lblSecurityNote = new JLabel("<html>ℹ️ <b>Controle de Acesso:</b> Alterações de nível de acesso (ALUNO/ADMIN) e e-mail só podem ser efetuadas no Painel Administrativo.</html>");
        lblSecurityNote.setFont(AppTheme.FONT_SMALL);
        lblSecurityNote.setForeground(ModernColors.PRIMARY_LIGHT);
        infoBox.add(lblSecurityNote, BorderLayout.CENTER);

        protectedCard.add(protHeader, BorderLayout.NORTH);
        protectedCard.add(protForm, BorderLayout.CENTER);
        protectedCard.add(infoBox, BorderLayout.SOUTH);

        contentGrid.add(editCard);
        contentGrid.add(protectedCard);

        add(headerCard, BorderLayout.NORTH);
        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createFieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(8, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        l.setPreferredSize(new Dimension(170, 26));
        p.add(l, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void refreshUserData() {
        Usuario u = apiService.getUsuarioLogado();
        if (u == null) {
            lblNomeHeader.setText("Não Autenticado");
            lblEmailHeader.setText("Faça login para consultar o perfil");
            avatarPanel.setUser("V", null);
            badgeRole.setVisible(false);
            lblRating.setText("⭐ -- / 5.00");
            lblInstituicao.setText("🏛️ --");
            lblCriadoEm.setText("📅 --");
            txtNome.setText("");
            txtTelefone.setText("");
            txtCurso.setText("");
            txtFotoUrl.setText("");
            txtEmailReadOnly.setText("");
            txtNivelReadOnly.setText("");
            txtRatingReadOnly.setText("");
            txtIdReadOnly.setText("");
            lblStatus.setText("Faça login para editar seu perfil.");
            lblStatus.setForeground(AppTheme.getTextSecondary());
            repaint();
            return;
        }

        // Header info
        lblNomeHeader.setText(u.getNome_completo());
        lblEmailHeader.setText(u.getEmail());
        avatarPanel.setUser(u.getNome_completo(), u.getFoto_url());
        badgeRole.setText("ADMIN".equalsIgnoreCase(u.getNivel()) ? "👑 ADMIN" : "🎓 ALUNO");
        badgeRole.setVisible(true);
        badgeRole.setBadgeColors("ADMIN".equalsIgnoreCase(u.getNivel()) ? ModernColors.BADGE_ADMIN_BG : ModernColors.BADGE_ALUNO_BG, Color.WHITE);
        lblRating.setText("⭐ " + (u.getMedia_avaliacao() != null ? u.getMedia_avaliacao() : "0.00") + " / 5.00");
        lblInstituicao.setText("🏛️ " + (u.getInstituicao_nome() != null ? u.getInstituicao_nome() : "Instituição de Ensino"));
        lblCriadoEm.setText("📅 " + (u.getCriado_em() != null ? u.getCriado_em().split("T")[0] : "Recente"));

        // Editable fields
        txtNome.setText(u.getNome_completo());
        txtTelefone.setText(u.getTelefone() != null ? u.getTelefone() : "");
        txtCurso.setText(u.getCurso() != null ? u.getCurso() : "");
        txtFotoUrl.setText(u.getFoto_url() != null ? u.getFoto_url() : "");

        // Readonly fields
        txtEmailReadOnly.setText(u.getEmail());
        txtNivelReadOnly.setText(u.getNivel());
        txtRatingReadOnly.setText(u.getMedia_avaliacao() != null ? u.getMedia_avaliacao() : "0.00");
        txtIdReadOnly.setText(u.getId());

        lblStatus.setText(" ");
        repaint();
    }

    private void saveProfileChanges() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String curso = txtCurso.getText().trim();
        String fotoUrl = txtFotoUrl.getText().trim();

        if (nome.isEmpty()) {
            lblStatus.setText("Nome completo não pode ficar vazio.");
            lblStatus.setForeground(ModernColors.DANGER);
            return;
        }

        ApiResponse<Usuario> resp = apiService.updateMe(nome, telefone, curso, fotoUrl.isEmpty() ? null : fotoUrl);
        if (resp.isSuccess()) {
            lblStatus.setText("Perfil atualizado com sucesso (HTTP 200 OK)!");
            lblStatus.setForeground(ModernColors.SUCCESS);
            ToastNotification.show(this, "Perfil Atualizado!", "Seus dados foram salvos com sucesso.", ToastNotification.ToastType.SUCCESS);
            refreshUserData();
            if (onUserUpdated != null) {
                onUserUpdated.run();
            }
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro ao atualizar perfil", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
