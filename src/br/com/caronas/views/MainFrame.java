package br.com.caronas.views;

import br.com.caronas.components.*;
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
    private DashboardView dashboardView;
    private BuscarCaronasView buscarCaronasView;
    private OferecerCaronaView oferecerCaronaView;
    private MinhasCaronasView minhasCaronasView;
    private MinhasReservasView minhasReservasView;
    private MeusVeiculosView meusVeiculosView;
    private AvaliacoesView avaliacoesView;
    private PerfilView perfilView;
    private AdminUsuariosView adminUsuariosView;
    private ApiConsoleView apiConsoleView;
    private LoginView loginView;
    private CadastroView cadastroView;

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
    private String activeNav = "DASHBOARD";

    public MainFrame() {
        this.apiService = new MockApiService();
        initUI();
    }

    private void initUI() {
        setTitle("UniRide — Sistema de Caronas Universitárias (App Completo)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 860);
        setMinimumSize(new Dimension(1100, 720));
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

        dashboardView = new DashboardView(apiService, this::navigateTo);
        buscarCaronasView = new BuscarCaronasView(apiService);
        oferecerCaronaView = new OferecerCaronaView(apiService, this::navigateTo);
        minhasCaronasView = new MinhasCaronasView(apiService, this::navigateTo);
        minhasReservasView = new MinhasReservasView(apiService, this::navigateTo);
        meusVeiculosView = new MeusVeiculosView(apiService);
        avaliacoesView = new AvaliacoesView(apiService);
        perfilView = new PerfilView(apiService, this::updateHeaderUserInfo);
        adminUsuariosView = new AdminUsuariosView(apiService, this::switchDemoRole);
        apiConsoleView = new ApiConsoleView(apiService);

        loginView = new LoginView(apiService, this::onLoginSuccess, () -> navigateTo("CADASTRO"));
        cadastroView = new CadastroView(apiService, this::onCadastroSuccess, () -> navigateTo("LOGIN"));

        contentContainer.add(dashboardView, "DASHBOARD");
        contentContainer.add(buscarCaronasView, "BUSCAR");
        contentContainer.add(oferecerCaronaView, "OFERECER");
        contentContainer.add(minhasCaronasView, "MINHAS_CARONAS");
        contentContainer.add(minhasReservasView, "MINHAS_RESERVAS");
        contentContainer.add(meusVeiculosView, "VEICULOS");
        contentContainer.add(avaliacoesView, "AVALIACOES");
        contentContainer.add(perfilView, "PERFIL");
        contentContainer.add(adminUsuariosView, "ADMIN");
        contentContainer.add(apiConsoleView, "CONSOLE");
        contentContainer.add(loginView, "LOGIN");
        contentContainer.add(cadastroView, "CADASTRO");

        centerPanel.add(contentContainer, BorderLayout.CENTER);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Initial state
        updateHeaderUserInfo();
        if (apiService.isAuthenticated()) {
            navigateTo("DASHBOARD");
        } else {
            navigateTo("LOGIN");
        }

        AppTheme.addThemeChangeListener(() -> {
            rootPanel.setBackground(AppTheme.getBackground());
            btnHeaderThemeToggle.setText(AppTheme.isDarkMode() ? "Dark" : "Light");
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

        JLabel lblLogo = new JLabel("[UR]");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(ModernColors.PRIMARY);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);

        JLabel lblTitle = new JLabel("UNIRIDE • CARONAS UNIVERSITÁRIAS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(ModernColors.PRIMARY_LIGHT);

        JLabel lblSub = new JLabel("Plataforma Acadêmica de Mobilidade e Caronas Compartilhadas");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        brandText.add(lblTitle);
        brandText.add(lblSub);

        brandPanel.add(lblLogo);
        brandPanel.add(brandText);

        // Right User Info & Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
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
        btnHeaderQuickSwitch = new RoundedButton("Alternar Perfil Demo", RoundedButton.ButtonStyle.GHOST);
        btnHeaderQuickSwitch.setFont(AppTheme.FONT_SMALL_BOLD);
        btnHeaderQuickSwitch.addActionListener(e -> switchDemoRole());

        // Theme toggle button
        btnHeaderThemeToggle = new RoundedButton("Dark", RoundedButton.ButtonStyle.GHOST);
        btnHeaderThemeToggle.setFont(AppTheme.FONT_SMALL_BOLD);
        btnHeaderThemeToggle.addActionListener(e -> AppTheme.toggleTheme(this));

        // Logout
        btnHeaderLogout = new RoundedButton("Sair", RoundedButton.ButtonStyle.DANGER);
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
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 12));

        JPanel navList = new JPanel(new GridLayout(10, 1, 0, 6));
        navList.setOpaque(false);

        RoundedButton btnDashboard = createNavButton("Início (Painel)", "DASHBOARD");
        RoundedButton btnBuscar = createNavButton("Buscar Caronas", "BUSCAR");
        RoundedButton btnOferecer = createNavButton("Oferecer Carona", "OFERECER");
        RoundedButton btnMinhasCaronas = createNavButton("Minhas Caronas", "MINHAS_CARONAS");
        RoundedButton btnMinhasReservas = createNavButton("Minhas Reservas", "MINHAS_RESERVAS");
        RoundedButton btnVeiculos = createNavButton("Meus Veículos", "VEICULOS");
        RoundedButton btnAvaliacoes = createNavButton("Avaliações", "AVALIACOES");
        RoundedButton btnPerfil = createNavButton("Meu Perfil", "PERFIL");
        RoundedButton btnAdmin = createNavButton("Painel Admin (RBAC)", "ADMIN");
        RoundedButton btnConsole = createNavButton("Console API & Logs", "CONSOLE");

        navList.add(btnDashboard);
        navList.add(btnBuscar);
        navList.add(btnOferecer);
        navList.add(btnMinhasCaronas);
        navList.add(btnMinhasReservas);
        navList.add(btnVeiculos);
        navList.add(btnAvaliacoes);
        navList.add(btnPerfil);
        navList.add(btnAdmin);
        navList.add(btnConsole);

        // Bottom Spec Badge
        RoundedPanel specInfo = new RoundedPanel(10);
        specInfo.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xEE, 0xF2, 0xFF));
        specInfo.setLayout(new BorderLayout());
        specInfo.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblSpec = new JLabel("<html><b>UniRide v2.0</b> • Sistema Completo<br><font color='#818cf8'>Caronas • Reservas • Avaliações</font></html>");
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
        btn.setPreferredSize(new Dimension(0, 38));
        btn.addActionListener(e -> navigateTo(targetView));
        navButtons.put(targetView, btn);
        return btn;
    }

    public void navigateTo(String viewName) {
        // Enforce authentication for private views
        boolean isPublicView = "LOGIN".equals(viewName) || "CADASTRO".equals(viewName) || "BUSCAR".equals(viewName) || "CONSOLE".equals(viewName);
        if (!apiService.isAuthenticated() && !isPublicView) {
            ToastNotification.show(this, "Acesso Restrito", "Faça login para acessar esta funcionalidade.", ToastNotification.ToastType.WARNING);
            viewName = "LOGIN";
        }

        this.activeNav = viewName;
        cardLayout.show(contentContainer, viewName);

        if ("DASHBOARD".equals(viewName)) {
            dashboardView.refreshData();
        } else if ("BUSCAR".equals(viewName)) {
            buscarCaronasView.executeSearch();
        } else if ("OFERECER".equals(viewName)) {
            oferecerCaronaView.refreshVeiculos();
        } else if ("MINHAS_CARONAS".equals(viewName)) {
            minhasCaronasView.loadCaronas();
        } else if ("MINHAS_RESERVAS".equals(viewName)) {
            minhasReservasView.loadReservas();
        } else if ("VEICULOS".equals(viewName)) {
            meusVeiculosView.loadVeiculos();
        } else if ("AVALIACOES".equals(viewName)) {
            avaliacoesView.loadAvaliacoes();
        } else if ("PERFIL".equals(viewName)) {
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
        Usuario u = apiService.getUsuarioLogado();
        if (u != null) {
            lblHeaderNome.setText(u.getNome_completo());
            lblHeaderEmail.setText(u.getEmail());
            headerAvatar.setUser(u.getNome_completo(), u.getFoto_url());
            headerBadgeRole.setText("ADMIN".equalsIgnoreCase(u.getNivel()) ? "[ADMIN]" : "[ALUNO]");
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
            // Switch to Admin Ana
            apiService.login("ana.lima@faculdade.br", "admin123");
        } else {
            // Switch to Aluno Carlos
            apiService.login("carlos.edu@gmail.com", "senhaSegura123");
        }
        updateHeaderUserInfo();
        if ("LOGIN".equals(activeNav) || "CADASTRO".equals(activeNav)) {
            navigateTo("DASHBOARD");
        } else {
            navigateTo(activeNav);
        }
    }

    private void onLoginSuccess() {
        updateHeaderUserInfo();
        navigateTo("DASHBOARD");
    }

    private void onCadastroSuccess() {
        updateHeaderUserInfo();
        navigateTo("DASHBOARD");
    }

    private void onLogout() {
        apiService.logout();
        updateHeaderUserInfo();
        navigateTo("LOGIN");
    }
}
