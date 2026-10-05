package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.AuthResponse;
import br.com.caronas.model.Instituicao;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CadastroView extends JPanel {
    private final ApiService apiService;
    private final Runnable onCadastroSuccess;
    private final Runnable onNavigateToLogin;

    private ModernTextField txtNome;
    private ModernTextField txtEmail;
    private ModernPasswordField txtSenha;
    private ModernTextField txtTelefone;
    private ModernTextField txtCurso;
    private JComboBox<Instituicao> cbInstituicao;
    private JLabel lblStatus;
    private RoundedButton btnCadastrar;

    public CadastroView(ApiService apiService, Runnable onCadastroSuccess, Runnable onNavigateToLogin) {
        this.apiService = apiService;
        this.onCadastroSuccess = onCadastroSuccess;
        this.onNavigateToLogin = onNavigateToLogin;

        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(20);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(920, 620));

        // LEFT PANEL: Brand Info
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, ModernColors.ACCENT_PURPLE,
                        getWidth(), getHeight(), ModernColors.PRIMARY
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.fillRect(getWidth() - 20, 0, 20, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setPreferredSize(new Dimension(360, 620));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(36, 32, 36, 32));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel lblTag = new JLabel("UniRide");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTag.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel("<html>Crie sua Conta<br>Universitaria</html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(230, 240, 255));

        JLabel lblDesc = new JLabel("<html>Junte-se a rede de mobilidade da sua faculdade. Compartilhe trajetos com estudantes verificados.</html>");
        lblDesc.setFont(AppTheme.FONT_BODY);
        lblDesc.setForeground(new Color(240, 240, 255));
        lblDesc.setBorder(new EmptyBorder(14, 0, 14, 0));

        JPanel featureList = new JPanel(new GridLayout(4, 1, 0, 8));
        featureList.setOpaque(false);
        featureList.add(createFeatureItem("[*] Validacao de e-mail academico"));
        featureList.add(createFeatureItem("[*] Integracao com sua faculdade"));
        featureList.add(createFeatureItem("[*] Perfil ALUNO configurado"));
        featureList.add(createFeatureItem("[*] Token JWT emitido"));

        heroContent.add(lblTag);
        heroContent.add(Box.createVerticalStrut(4));
        heroContent.add(lblTitle);
        heroContent.add(Box.createVerticalStrut(6));
        heroContent.add(lblDesc);
        heroContent.add(Box.createVerticalStrut(8));
        heroContent.add(featureList);

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // RIGHT PANEL: Registration Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel lblFormTitle = new JLabel("Novo Cadastro de Aluno");
        lblFormTitle.setFont(AppTheme.FONT_TITLE);
        lblFormTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblFormSub = new JLabel("Preencha seus dados para ingressar na plataforma");
        lblFormSub.setFont(AppTheme.FONT_BODY);
        lblFormSub.setForeground(AppTheme.getTextSecondary());

        // Fields
        txtNome = new ModernTextField("Carlos Eduardo");
        txtEmail = new ModernTextField("carlos.novo@aluno.ueg.br");
        txtSenha = new ModernPasswordField("senhaSegura123");
        txtTelefone = new ModernTextField("62999998888");
        txtCurso = new ModernTextField("Engenharia de Software");

        List<Instituicao> instList = apiService.getInstituicoes();
        cbInstituicao = new JComboBox<>(instList.toArray(new Instituicao[0]));
        cbInstituicao.setFont(AppTheme.FONT_BODY);

        btnCadastrar = new RoundedButton("Finalizar Cadastro (POST /auth)", RoundedButton.ButtonStyle.PRIMARY);
        btnCadastrar.setPreferredSize(new Dimension(0, 40));
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnCadastrar.addActionListener(e -> executeCadastro());

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerLink.setOpaque(false);
        JLabel lblHaveAccount = new JLabel("Ja possui uma conta?");
        lblHaveAccount.setFont(AppTheme.FONT_BODY);
        lblHaveAccount.setForeground(AppTheme.getTextSecondary());

        RoundedButton btnLogin = new RoundedButton("Fazer Login", RoundedButton.ButtonStyle.GHOST);
        btnLogin.setFont(AppTheme.FONT_BODY_BOLD);
        btnLogin.setForeground(ModernColors.PRIMARY);
        btnLogin.addActionListener(e -> onNavigateToLogin.run());

        footerLink.add(lblHaveAccount);
        footerLink.add(btnLogin);

        // Assemble Form
        formPanel.add(lblFormTitle);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(lblFormSub);
        formPanel.add(Box.createVerticalStrut(12));

        formPanel.add(createFieldRow("NOME COMPLETO:", txtNome));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(createFieldRow("E-MAIL ACADEMICO:", txtEmail));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(createFieldRow("SENHA (MINIMO 6 CARACTERES):", txtSenha));
        formPanel.add(Box.createVerticalStrut(6));

        JPanel splitRow = new JPanel(new GridLayout(1, 2, 10, 0));
        splitRow.setOpaque(false);
        splitRow.add(createFieldGroup("TELEFONE COM DDD:", txtTelefone));
        splitRow.add(createFieldGroup("CURSO:", txtCurso));
        formPanel.add(splitRow);
        formPanel.add(Box.createVerticalStrut(6));

        formPanel.add(createFieldGroup("INSTITUICAO DE ENSINO:", cbInstituicao));
        formPanel.add(Box.createVerticalStrut(12));

        formPanel.add(btnCadastrar);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(lblStatus);
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(footerLink);

        card.add(heroPanel, BorderLayout.WEST);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);
    }

    private JPanel createFeatureItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    private JPanel createFieldRow(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void executeCadastro() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        String telefone = txtTelefone.getText().trim();
        String curso = txtCurso.getText().trim();
        Instituicao inst = (Instituicao) cbInstituicao.getSelectedItem();
        String instId = inst != null ? inst.getId() : "";

        ApiResponse<AuthResponse> resp = apiService.cadastrar(nome, email, senha, telefone, curso, instId);
        if (resp.isSuccess()) {
            lblStatus.setText("Conta criada com sucesso!");
            lblStatus.setForeground(ModernColors.SUCCESS);
            ToastNotification.show(this, "Cadastro Realizado!", "Bem-vindo a comunidade UniRide!", ToastNotification.ToastType.SUCCESS);
            onCadastroSuccess.run();
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro no Cadastro", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
