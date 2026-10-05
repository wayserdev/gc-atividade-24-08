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
        card.setPreferredSize(new Dimension(880, 540));

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
        heroPanel.setPreferredSize(new Dimension(380, 540));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(36, 32, 36, 32));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel lblHeroTag = new JLabel("UniRide");
        lblHeroTag.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblHeroTag.setForeground(Color.WHITE);

        JLabel lblHeroTitle = new JLabel("<html>Caronas<br>Universitarias</html>");
        lblHeroTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeroTitle.setForeground(new Color(230, 240, 255));

        JLabel lblHeroDesc = new JLabel("<html>Plataforma academica de mobilidade com <b>Autenticacao JWT</b>, gestao de caronas, reservas de vagas e <b>Controle RBAC</b>.</html>");
        lblHeroDesc.setFont(AppTheme.FONT_BODY);
        lblHeroDesc.setForeground(new Color(240, 240, 255));
        lblHeroDesc.setBorder(new EmptyBorder(14, 0, 14, 0));

        JPanel featureList = new JPanel(new GridLayout(4, 1, 0, 8));
        featureList.setOpaque(false);
        featureList.add(createFeatureItem("[*] Tokens JWT com seguranca"));
        featureList.add(createFeatureItem("[*] Gestao de Caronas e Vagas"));
        featureList.add(createFeatureItem("[*] Avaliacoes entre Alunos"));
        featureList.add(createFeatureItem("[*] Perfis ALUNO e ADMIN"));

        heroContent.add(lblHeroTag);
        heroContent.add(Box.createVerticalStrut(4));
        heroContent.add(lblHeroTitle);
        heroContent.add(Box.createVerticalStrut(6));
        heroContent.add(lblHeroDesc);
        heroContent.add(Box.createVerticalStrut(8));
        heroContent.add(featureList);

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // RIGHT PANEL: Login Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(32, 38, 32, 38));

        JLabel lblWelcome = new JLabel("Acessar Plataforma");
        lblWelcome.setFont(AppTheme.FONT_TITLE);
        lblWelcome.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Digite suas credenciais ou selecione um usuario de teste:");
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        // Inputs
        JLabel lblEmail = new JLabel("E-MAIL ACADEMICO:");
        lblEmail.setFont(AppTheme.FONT_SMALL_BOLD);
        lblEmail.setForeground(AppTheme.getTextSecondary());
        txtEmail = new ModernTextField("carlos.edu@gmail.com");

        JLabel lblSenha = new JLabel("SENHA:");
        lblSenha.setFont(AppTheme.FONT_SMALL_BOLD);
        lblSenha.setForeground(AppTheme.getTextSecondary());
        txtSenha = new ModernPasswordField("senhaSegura123");

        btnLogin = new RoundedButton("Entrar no Sistema", RoundedButton.ButtonStyle.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.addActionListener(e -> executeLogin());

        // Quick Demo Logins (2x2 Grid)
        JPanel demoPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        demoPanel.setOpaque(false);

        RoundedButton btnCarlos = new RoundedButton("[Aluno] Carlos", RoundedButton.ButtonStyle.SECONDARY);
        btnCarlos.setFont(AppTheme.FONT_SMALL_BOLD);
        btnCarlos.addActionListener(e -> {
            txtEmail.setText("carlos.edu@gmail.com");
            txtSenha.setText("senhaSegura123");
            executeLogin();
        });

        RoundedButton btnMariana = new RoundedButton("[Aluna] Mariana", RoundedButton.ButtonStyle.SECONDARY);
        btnMariana.setFont(AppTheme.FONT_SMALL_BOLD);
        btnMariana.addActionListener(e -> {
            txtEmail.setText("mariana.oli@gmail.com");
            txtSenha.setText("senha123");
            executeLogin();
        });

        RoundedButton btnLucas = new RoundedButton("[Aluno] Lucas", RoundedButton.ButtonStyle.SECONDARY);
        btnLucas.setFont(AppTheme.FONT_SMALL_BOLD);
        btnLucas.addActionListener(e -> {
            txtEmail.setText("lucas.rib@gmail.com");
            txtSenha.setText("senha123");
            executeLogin();
        });

        RoundedButton btnAdmin = new RoundedButton("[Admin] Ana", RoundedButton.ButtonStyle.ACCENT);
        btnAdmin.setFont(AppTheme.FONT_SMALL_BOLD);
        btnAdmin.addActionListener(e -> {
            txtEmail.setText("ana.lima@faculdade.br");
            txtSenha.setText("admin123");
            executeLogin();
        });

        demoPanel.add(btnCarlos);
        demoPanel.add(btnMariana);
        demoPanel.add(btnLucas);
        demoPanel.add(btnAdmin);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Link to register
        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerLink.setOpaque(false);
        JLabel lblNoAccount = new JLabel("Nao tem uma conta?");
        lblNoAccount.setFont(AppTheme.FONT_BODY);
        lblNoAccount.setForeground(AppTheme.getTextSecondary());

        RoundedButton btnCadastre = new RoundedButton("Cadastre-se", RoundedButton.ButtonStyle.GHOST);
        btnCadastre.setFont(AppTheme.FONT_BODY_BOLD);
        btnCadastre.setForeground(ModernColors.PRIMARY);
        btnCadastre.addActionListener(e -> onNavigateToCadastro.run());

        footerLink.add(lblNoAccount);
        footerLink.add(btnCadastre);

        // Assemble form layout
        formPanel.add(lblWelcome);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(lblSub);
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(lblEmail);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtEmail);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(lblSenha);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(txtSenha);
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(12));

        JLabel lblDemoTitle = new JLabel("Atalhos Rapidos para Teste:");
        lblDemoTitle.setFont(AppTheme.FONT_SMALL_BOLD);
        lblDemoTitle.setForeground(AppTheme.getTextMuted());
        formPanel.add(lblDemoTitle);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(demoPanel);

        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblStatus);
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(footerLink);

        // Assemble card
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

    public void clearFields() {
        txtEmail.setText("");
        txtSenha.setText("");
        lblStatus.setText(" ");
    }

    private void executeLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (email.isEmpty() || senha.isEmpty()) {
            lblStatus.setText("Preencha o e-mail e a senha.");
            lblStatus.setForeground(ModernColors.WARNING);
            ToastNotification.show(this, "Atencao", "Preencha o e-mail e a senha.", ToastNotification.ToastType.WARNING);
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
            ToastNotification.show(this, "Erro de Autenticacao (" + resp.getStatusCode() + ")", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
