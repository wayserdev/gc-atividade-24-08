package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.AuthResponse;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JPanel {
    private final ApiService apiService;
    private final Runnable onLoginSuccess;
    private final Runnable onNavigateToCadastro;

    private ModernTextField txtEmail;
    private ModernPasswordField txtSenha;
    private JLabel lblStatus;
    private RoundedButton btnLogin;

    public LoginView(ApiService apiService, Runnable onLoginSuccess, Runnable onNavigateToCadastro) {
        this.apiService = apiService;
        this.onLoginSuccess = onLoginSuccess;
        this.onNavigateToCadastro = onNavigateToCadastro;

        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Main Card Container
        RoundedPanel card = new RoundedPanel(20);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(860, 520));

        // LEFT PANEL: Hero Banner
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, ModernColors.PRIMARY,
                        getWidth(), getHeight(), ModernColors.ACCENT_PURPLE
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Cover right corners so card looks cohesive
                g2.fillRect(getWidth() - 20, 0, 20, getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillOval(-40, -40, 180, 180);
                g2.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setPreferredSize(new Dimension(380, 520));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(40, 36, 40, 36));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel lblHeroIcon = new JLabel("🚗💨");
        lblHeroIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));

        JLabel lblHeroTitle = new JLabel("<html>Caronas<br>Universitárias</html>");
        lblHeroTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeroTitle.setForeground(Color.WHITE);

        JLabel lblHeroDesc = new JLabel("<html>Plataforma acadêmica com <b>Autenticação JWT</b>, gestão de perfis e <b>Controle de Acesso RBAC</b> conforme a Especificação Técnica FT01/FT02/FT03.</html>");
        lblHeroDesc.setFont(AppTheme.FONT_BODY);
        lblHeroDesc.setForeground(new Color(240, 240, 255));
        lblHeroDesc.setBorder(new EmptyBorder(16, 0, 16, 0));

        JPanel featureList = new JPanel(new GridLayout(3, 1, 0, 10));
        featureList.setOpaque(false);
        featureList.add(createFeatureItem("🔒 Tokens JWT com assinatura segura"));
        featureList.add(createFeatureItem("👥 Perfis ALUNO e ADMIN"));
        featureList.add(createFeatureItem("🛡️ Regras e Auditoria RBAC"));

        heroContent.add(lblHeroIcon);
        heroContent.add(Box.createVerticalStrut(16));
        heroContent.add(lblHeroTitle);
        heroContent.add(Box.createVerticalStrut(8));
        heroContent.add(lblHeroDesc);
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(featureList);

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // RIGHT PANEL: Login Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(40, 44, 40, 44));

        JLabel lblWelcome = new JLabel("Bem-vindo de volta! 👋");
        lblWelcome.setFont(AppTheme.FONT_TITLE);
        lblWelcome.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Digite suas credenciais ou use o login rápido");
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        // Inputs
        JLabel lblEmail = new JLabel("E-MAIL");
        lblEmail.setFont(AppTheme.FONT_SMALL_BOLD);
        lblEmail.setForeground(AppTheme.getTextSecondary());
        txtEmail = new ModernTextField("carlos.edu@gmail.com");

        JLabel lblSenha = new JLabel("SENHA");
        lblSenha.setFont(AppTheme.FONT_SMALL_BOLD);
        lblSenha.setForeground(AppTheme.getTextSecondary());
        txtSenha = new ModernPasswordField("senhaSegura123");

        btnLogin = new RoundedButton("Entrar na Plataforma", RoundedButton.ButtonStyle.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> executeLogin());

        // Quick Demo Logins
        JPanel demoPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        demoPanel.setOpaque(false);

        RoundedButton btnDemoAluno = new RoundedButton("🎓 Carlos (Aluno)", RoundedButton.ButtonStyle.SECONDARY);
        btnDemoAluno.setFont(AppTheme.FONT_SMALL_BOLD);
        btnDemoAluno.addActionListener(e -> {
            txtEmail.setText("carlos.edu@gmail.com");
            txtSenha.setText("senhaSegura123");
            executeLogin();
        });

        RoundedButton btnDemoAdmin = new RoundedButton("👑 Ana (Admin)", RoundedButton.ButtonStyle.ACCENT);
        btnDemoAdmin.setFont(AppTheme.FONT_SMALL_BOLD);
        btnDemoAdmin.addActionListener(e -> {
            txtEmail.setText("ana.lima@faculdade.br");
            txtSenha.setText("admin123");
            executeLogin();
        });

        demoPanel.add(btnDemoAluno);
        demoPanel.add(btnDemoAdmin);

        // Status / Error message
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Link to register
        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerLink.setOpaque(false);
        JLabel lblNoAccount = new JLabel("Não tem uma conta?");
        lblNoAccount.setFont(AppTheme.FONT_BODY);
        lblNoAccount.setForeground(AppTheme.getTextSecondary());

        RoundedButton btnCadastre = new RoundedButton("Cadastre-se", RoundedButton.ButtonStyle.GHOST);
        btnCadastre.setFont(AppTheme.FONT_BODY_BOLD);
        btnCadastre.setForeground(ModernColors.PRIMARY);
        btnCadastre.addActionListener(e -> onNavigateToCadastro.run());

        footerLink.add(lblNoAccount);
        footerLink.add(btnCadastre);

        // Build form layout
        formPanel.add(lblWelcome);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(lblSub);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(lblEmail);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtEmail);
        formPanel.add(Box.createVerticalStrut(12));

        formPanel.add(lblSenha);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtSenha);
        formPanel.add(Box.createVerticalStrut(16));

        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(12));

        JLabel lblDemoTitle = new JLabel("⚡ Atalhos para Teste / Avaliação:");
        lblDemoTitle.setFont(AppTheme.FONT_SMALL_BOLD);
        lblDemoTitle.setForeground(AppTheme.getTextMuted());
        formPanel.add(lblDemoTitle);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(demoPanel);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblStatus);
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(footerLink);

        // Assemble card
        card.add(heroPanel, BorderLayout.WEST);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);
    }

    private JPanel createFeatureItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    private void executeLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (email.isEmpty() || senha.isEmpty()) {
            lblStatus.setText("Preencha o e-mail e a senha.");
            lblStatus.setForeground(ModernColors.WARNING);
            ToastNotification.show(this, "Atenção", "Preencha o e-mail e a senha.", ToastNotification.ToastType.WARNING);
            return;
        }

        ApiResponse<AuthResponse> resp = apiService.login(email, senha);
        if (resp.isSuccess()) {
            lblStatus.setText("Login efetuado com sucesso!");
            lblStatus.setForeground(ModernColors.SUCCESS);
            ToastNotification.show(this, "Autenticado com Sucesso!", "Bem-vindo(a), " + resp.getData().getUsuario().getNome_completo(), ToastNotification.ToastType.SUCCESS);
            onLoginSuccess.run();
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro de Autenticação (" + resp.getStatusCode() + ")", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
