package br.com.caronas.views;

import br.com.caronas.components.AvatarPanel;
import br.com.caronas.components.BadgeLabel;
import br.com.caronas.components.RoundedButton;
import br.com.caronas.components.RoundedPanel;
import br.com.caronas.model.Usuario;
import br.com.caronas.service.ApiService;
import br.com.caronas.service.MockApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final ApiService apiService;

    private JPanel contentContainer;
    private CardLayout cardLayout;

    // View instances
    private LoginView loginView;
    private CadastroView cadastroView;
    private PerfilView perfilView;
    private AdminUsuariosView adminUsuariosView;
    private ApiConsoleView apiConsoleView;

    // Header components
    private AvatarPanel headerAvatar;
    private JLabel lblHeaderNome;
    private JLabel lblHeaderEmail;
    private BadgeLabel headerBadgeRole;
    private RoundedButton btnHeaderThemeToggle;
    private RoundedButton btnHeaderQuickSwitch;
    private RoundedButton btnHeaderLogout;

    // Sidebar navigation buttons
    private final Map<String, RoundedButton> navButtons = new HashMap<>();
    private String activeNav = "PERFIL";

    public MainFrame() {
        this.apiService = new MockApiService();
        initUI();
    }

    private void initUI() {
        setTitle("Sistema de Caronas — Autenticação JWT & RBAC (Feature 1)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 820);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // Root panel
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(AppTheme.getBackground());

        // 1. Top Header
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Split (Sidebar + Views)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel sidebar = createSidebarPanel();
        centerPanel.add(sidebar, BorderLayout.WEST);

        // Content Views Container
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setOpaque(false);

        loginView = new LoginView(apiService, this::onLoginSuccess, () -> navigateTo("CADASTRO"));
        cadastroView = new CadastroView(apiService, this::onCadastroSuccess, () -> navigateTo("LOGIN"));
        perfilView = new PerfilView(apiService, this::updateHeaderUserInfo);
        adminUsuariosView = new AdminUsuariosView(apiService, this::switchDemoRole);
        apiConsoleView = new ApiConsoleView(apiService);

        contentContainer.add(loginView, "LOGIN");
        contentContainer.add(cadastroView, "CADASTRO");
        contentContainer.add(perfilView, "PERFIL");
        contentContainer.add(adminUsuariosView, "ADMIN");
        contentContainer.add(apiConsoleView, "CONSOLE");

        centerPanel.add(contentContainer, BorderLayout.CENTER);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Initial navigation
        updateHeaderUserInfo();
        if (apiService.isAuthenticated()) {
            navigateTo("PERFIL");
        } else {
            navigateTo("LOGIN");
        }

        AppTheme.addThemeChangeListener(() -> {
            rootPanel.setBackground(AppTheme.getBackground());
            btnHeaderThemeToggle.setText(AppTheme.isDarkMode() ? "🌙 Dark" : "☀️ Light");
            updateNavButtonStyles();
        });
    }

    private JPanel createHeaderPanel() {
        RoundedPanel header = new RoundedPanel(0);
        header.setDrawBorder(true);
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(12, 24, 12, 24));
        header.setPreferredSize(new Dimension(0, 68));

        // Left Brand
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🚗");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel lblTitle = new JLabel("CARONAS UNIVERSITÁRIAS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(ModernColors.PRIMARY_LIGHT);

        JLabel lblSub = new JLabel("Feature 1: Autenticação JWT, Perfis e RBAC");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        brandText.add(lblTitle);
        brandText.add(lblSub);

        brandPanel.add(lblLogo);
        brandPanel.add(brandText);

        // Right User Info & Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        // Active user pill
        RoundedPanel userPill = new RoundedPanel(12);
        userPill.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x18, 0x22, 0x34) : new Color(0xEE, 0xF2, 0xFF));
        userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));
        userPill.setBorder(new EmptyBorder(4, 10, 4, 12));

        headerAvatar = new AvatarPanel(34);

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 0));
        userText.setOpaque(false);

        lblHeaderNome = new JLabel("Visitante");
        lblHeaderNome.setFont(AppTheme.FONT_BODY_BOLD);
        lblHeaderNome.setForeground(AppTheme.getTextPrimary());

        lblHeaderEmail = new JLabel("Não autenticado");
        lblHeaderEmail.setFont(AppTheme.FONT_SMALL);
        lblHeaderEmail.setForeground(AppTheme.getTextMuted());

        userText.add(lblHeaderNome);
        userText.add(lblHeaderEmail);

        headerBadgeRole = BadgeLabel.forRole("ALUNO");

        userPill.add(headerAvatar);
        userPill.add(userText);
        userPill.add(headerBadgeRole);

        // Quick role toggle button
        btnHeaderQuickSwitch = new RoundedButton("🔄 Alternar Perfil (demo)", RoundedButton.ButtonStyle.GHOST);
        btnHeaderQuickSwitch.setFont(AppTheme.FONT_SMALL_BOLD);
        btnHeaderQuickSwitch.addActionListener(e -> switchDemoRole());

        // Theme toggle button
        btnHeaderThemeToggle = new RoundedButton("🌙 Dark", RoundedButton.ButtonStyle.GHOST);
        btnHeaderThemeToggle.setFont(AppTheme.FONT_SMALL_BOLD);
        btnHeaderThemeToggle.addActionListener(e -> AppTheme.toggleTheme(this));

        // Logout
        btnHeaderLogout = new RoundedButton("🔒 Sair", RoundedButton.ButtonStyle.DANGER);
        btnHeaderLogout.setFont(AppTheme.FONT_SMALL_BOLD);
        btnHeaderLogout.addActionListener(e -> onLogout());

        rightPanel.add(userPill);
        rightPanel.add(btnHeaderQuickSwitch);
        rightPanel.add(btnHeaderThemeToggle);
        rightPanel.add(btnHeaderLogout);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebarPanel() {
        RoundedPanel sidebar = new RoundedPanel(0);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(20, 16, 20, 16));

        JPanel navList = new JPanel(new GridLayout(6, 1, 0, 10));
        navList.setOpaque(false);

        RoundedButton btnLogin = createNavButton("🔑  Login (/auth)", "LOGIN");
        RoundedButton btnCadastro = createNavButton("📝  Cadastrar Aluno", "CADASTRO");
        RoundedButton btnPerfil = createNavButton("👤  Meu Perfil (/me)", "PERFIL");
        RoundedButton btnAdmin = createNavButton("🛡️  Painel Admin (RBAC)", "ADMIN");
        RoundedButton btnConsole = createNavButton("📡  Console API & Logs", "CONSOLE");

        navList.add(btnPerfil);
        navList.add(btnAdmin);
        navList.add(btnConsole);
        navList.add(btnLogin);
        navList.add(btnCadastro);

        // Bottom Spec Badge
        RoundedPanel specInfo = new RoundedPanel(10);
        specInfo.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xEE, 0xF2, 0xFF));
        specInfo.setLayout(new BorderLayout());
        specInfo.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblSpec = new JLabel("<html><b>Dev 01</b> • FT01, FT02, FT03<br><font color='#818cf8'>JWT • Hash seguro • RBAC</font></html>");
        lblSpec.setFont(AppTheme.FONT_SMALL);
        specInfo.add(lblSpec, BorderLayout.CENTER);

        sidebar.add(navList, BorderLayout.NORTH);
        sidebar.add(specInfo, BorderLayout.SOUTH);

        return sidebar;
    }

    private RoundedButton createNavButton(String text, String targetView) {
        RoundedButton btn = new RoundedButton(text, RoundedButton.ButtonStyle.GHOST);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(AppTheme.FONT_BODY_BOLD);
        btn.setPreferredSize(new Dimension(0, 44));
        btn.addActionListener(e -> navigateTo(targetView));
        navButtons.put(targetView, btn);
        return btn;
    }

    public void navigateTo(String viewName) {
        this.activeNav = viewName;
        cardLayout.show(contentContainer, viewName);

        if ("PERFIL".equals(viewName)) {
            perfilView.refreshUserData();
        } else if ("ADMIN".equals(viewName)) {
            adminUsuariosView.checkAccessAndLoad();
        }

        updateNavButtonStyles();
    }

    private void updateNavButtonStyles() {
        for (Map.Entry<String, RoundedButton> entry : navButtons.entrySet()) {
            boolean isActive = entry.getKey().equals(activeNav);
            RoundedButton btn = entry.getValue();
            if (isActive) {
                btn.setButtonStyle(RoundedButton.ButtonStyle.PRIMARY);
            } else {
                btn.setButtonStyle(RoundedButton.ButtonStyle.GHOST);
            }
        }
    }

    private void updateHeaderUserInfo() {
        Usuario u = apiService.isAuthenticated() ? apiService.getUsuarioLogado() : null;
        if (u != null) {
            lblHeaderNome.setText(u.getNome_completo());
            lblHeaderEmail.setText(u.getEmail());
            headerAvatar.setUser(u.getNome_completo(), u.getFoto_url());
            headerBadgeRole.setText("ADMIN".equalsIgnoreCase(u.getNivel()) ? "👑 ADMIN" : "🎓 ALUNO");
            headerBadgeRole.setBadgeColors("ADMIN".equalsIgnoreCase(u.getNivel()) ? ModernColors.BADGE_ADMIN_BG : ModernColors.BADGE_ALUNO_BG, Color.WHITE);
            headerBadgeRole.setVisible(true);
            btnHeaderLogout.setVisible(true);
            btnHeaderQuickSwitch.setVisible(true);
        } else {
            lblHeaderNome.setText("Visitante");
            lblHeaderEmail.setText("Não autenticado");
            headerAvatar.setUser("V", null);
            headerBadgeRole.setVisible(false);
            btnHeaderLogout.setVisible(false);
            btnHeaderQuickSwitch.setVisible(true);
        }
    }

    private void switchDemoRole() {
        Usuario current = apiService.getUsuarioLogado();
        if (current == null || current.isAluno()) {
            // Switch to Admin
            apiService.login("ana.lima@faculdade.br", "admin123");
        } else {
            // Switch to Aluno
            apiService.login("carlos.edu@gmail.com", "senhaSegura123");
        }
        updateHeaderUserInfo();
        perfilView.refreshUserData();
        if ("ADMIN".equals(activeNav)) {
            adminUsuariosView.checkAccessAndLoad();
        }
    }

    private void onLoginSuccess() {
        updateHeaderUserInfo();
        navigateTo("PERFIL");
    }

    private void onCadastroSuccess() {
        updateHeaderUserInfo();
        navigateTo("PERFIL");
    }

    private void onLogout() {
        apiService.logout();
        updateHeaderUserInfo();
        navigateTo("LOGIN");
    }
}
