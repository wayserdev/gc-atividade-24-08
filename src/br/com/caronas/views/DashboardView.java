package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.service.MockApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class DashboardView extends JPanel {
    private final ApiService apiService;
    private final Consumer<String> navigateTo;

    private JLabel lblWelcome;
    private JLabel lblSubWelcome;
    private StatCard cardAtivas;
    private StatCard cardMinhasCaronas;
    private StatCard cardMinhasReservas;
    private StatCard cardEconomia;
    private JPanel feedPanel;

    public DashboardView(ApiService apiService, Consumer<String> navigateTo) {
        this.apiService = apiService;
        this.navigateTo = navigateTo;

        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Top Banner with Greeting & Quick Action Buttons
        RoundedPanel banner = new RoundedPanel(16);
        banner.setLayout(new BorderLayout(20, 0));
        banner.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel welcomeInfo = new JPanel();
        welcomeInfo.setLayout(new BoxLayout(welcomeInfo, BoxLayout.Y_AXIS));
        welcomeInfo.setOpaque(false);

        lblWelcome = new JLabel("Ola, Estudante!");
        lblWelcome.setFont(AppTheme.FONT_TITLE);
        lblWelcome.setForeground(AppTheme.getTextPrimary());

        lblSubWelcome = new JLabel("Economize combustivel, evite atrasos e conecte-se com colegas da sua faculdade.");
        lblSubWelcome.setFont(AppTheme.FONT_BODY);
        lblSubWelcome.setForeground(AppTheme.getTextSecondary());

        welcomeInfo.add(lblWelcome);
        welcomeInfo.add(Box.createVerticalStrut(4));
        welcomeInfo.add(lblSubWelcome);

        // Quick Actions
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        quickActions.setOpaque(false);

        RoundedButton btnBuscar = new RoundedButton("Buscar Caronas", RoundedButton.ButtonStyle.PRIMARY);
        btnBuscar.addActionListener(e -> navigateTo.accept("BUSCAR"));

        RoundedButton btnOferecer = new RoundedButton("+ Oferecer Carona", RoundedButton.ButtonStyle.ACCENT);
        btnOferecer.addActionListener(e -> navigateTo.accept("OFERECER"));

        RoundedButton btnVeiculos = new RoundedButton("Meus Veiculos", RoundedButton.ButtonStyle.SECONDARY);
        btnVeiculos.addActionListener(e -> navigateTo.accept("VEICULOS"));

        quickActions.add(btnBuscar);
        quickActions.add(btnOferecer);
        quickActions.add(btnVeiculos);

        banner.add(welcomeInfo, BorderLayout.CENTER);
        banner.add(quickActions, BorderLayout.EAST);

        // Stats Cards Row (4 Columns)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statsGrid.setOpaque(false);

        cardAtivas = new StatCard("[ATIVAS]", "0", "Caronas Ativas", "Disponiveis hoje", ModernColors.PRIMARY);
        cardMinhasCaronas = new StatCard("[OFERTADAS]", "0", "Minhas Caronas", "Como motorista", ModernColors.ACCENT_CYAN);
        cardMinhasReservas = new StatCard("[RESERVAS]", "0", "Minhas Reservas", "Como passageiro", ModernColors.ACCENT_PURPLE);
        cardEconomia = new StatCard("[ECONOMIA]", "R$ 0,00", "Economia Total", "Rateio estimado", ModernColors.SUCCESS);

        statsGrid.add(cardAtivas);
        statsGrid.add(cardMinhasCaronas);
        statsGrid.add(cardMinhasReservas);
        statsGrid.add(cardEconomia);

        // Center Area: Top Section + Upcoming Rides Feed
        JPanel centerSection = new JPanel(new BorderLayout(0, 14));
        centerSection.setOpaque(false);

        JPanel headerFeed = new JPanel(new BorderLayout());
        headerFeed.setOpaque(false);

        JLabel lblFeedTitle = new JLabel("Proximas Caronas Disponiveis");
        lblFeedTitle.setFont(AppTheme.FONT_SUBTITLE);
        lblFeedTitle.setForeground(AppTheme.getTextPrimary());

        JPanel feedActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        feedActions.setOpaque(false);

        RoundedButton btnResetTest = new RoundedButton("Restaurar Caronas de Teste", RoundedButton.ButtonStyle.GHOST);
        btnResetTest.setFont(AppTheme.FONT_SMALL_BOLD);
        btnResetTest.addActionListener(e -> {
            if (apiService instanceof MockApiService) {
                ((MockApiService) apiService).initDefaultData();
                ToastNotification.show(this, "Dados Restaurados", "Caronas e dados de teste foram redefinidos.", ToastNotification.ToastType.INFO);
                refreshData();
            }
        });

        RoundedButton btnVerTodas = new RoundedButton("Ver Todas as Caronas ->", RoundedButton.ButtonStyle.PRIMARY);
        btnVerTodas.setFont(AppTheme.FONT_SMALL_BOLD);
        btnVerTodas.addActionListener(e -> navigateTo.accept("BUSCAR"));

        feedActions.add(btnResetTest);
        feedActions.add(btnVerTodas);

        headerFeed.add(lblFeedTitle, BorderLayout.WEST);
        headerFeed.add(feedActions, BorderLayout.EAST);

        feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedPanel.setOpaque(false);

        JScrollPane scrollFeed = new JScrollPane(feedPanel);
        scrollFeed.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));
        scrollFeed.setOpaque(false);
        scrollFeed.getViewport().setOpaque(false);
        scrollFeed.getVerticalScrollBar().setUnitIncrement(16);

        centerSection.add(statsGrid, BorderLayout.NORTH);
        centerSection.add(scrollFeed, BorderLayout.CENTER);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 14));
        mainLayout.setOpaque(false);
        mainLayout.add(banner, BorderLayout.NORTH);
        mainLayout.add(centerSection, BorderLayout.CENTER);

        add(mainLayout, BorderLayout.CENTER);
    }

    public void refreshData() {
        Usuario u = apiService.getUsuarioLogado();
        if (u != null) {
            lblWelcome.setText("Ola, " + u.getNome_completo() + "!");
            lblSubWelcome.setText(u.getCurso() + " - " + (u.getInstituicao_nome() != null ? u.getInstituicao_nome() : "Universidade"));
        } else {
            lblWelcome.setText("Ola, Visitante!");
            lblSubWelcome.setText("Faca login para acessar todas as funcionalidades.");
        }

        DashboardStats stats = apiService.getDashboardStats();
        cardAtivas.updateValues(String.valueOf(stats.getTotalCaronasAtivasHoje()), "Caronas ativas no campus");
        cardMinhasCaronas.updateValues(String.valueOf(stats.getTotalMinhasCaronas()), "Como motorista");
        cardMinhasReservas.updateValues(String.valueOf(stats.getTotalMinhasReservas()), "Como passageiro");
        cardEconomia.updateValues(String.format("R$ %.2f", stats.getEconomiaEstimadaReais()), String.format("%.1f kg CO2 poupados", stats.getKgCo2Evitados()));

        feedPanel.removeAll();
        ApiResponse<List<Carona>> resp = apiService.buscarCaronas(null, null, null, null, "TODAS_ATIVAS");
        List<Carona> lista = resp.getData();

        if (lista == null || lista.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(40, 20, 40, 20));

            JLabel lblEmpty = new JLabel("Nenhuma carona cadastrada no momento.", SwingConstants.CENTER);
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundedButton btnSeed = new RoundedButton("Carregar Caronas de Teste", RoundedButton.ButtonStyle.PRIMARY);
            btnSeed.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSeed.addActionListener(e -> {
                if (apiService instanceof MockApiService) {
                    ((MockApiService) apiService).initDefaultData();
                    refreshData();
                }
            });

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(10));
            empty.add(btnSeed);
            feedPanel.add(empty);
        } else {
            for (Carona c : lista) {
                CaronaCard card = new CaronaCard(
                        c,
                        apiService.getUsuarioLogado(),
                        this::openSolicitarDialog,
                        this::openGerenciarDialog
                );
                feedPanel.add(card);
                feedPanel.add(Box.createVerticalStrut(10));
            }
        }

        feedPanel.revalidate();
        feedPanel.repaint();
    }

    private void openSolicitarDialog(Carona c) {
        if (!apiService.isAuthenticated()) {
            ToastNotification.show(this, "Login Necessario", "Faca login para solicitar vaga.", ToastNotification.ToastType.WARNING);
            navigateTo.accept("LOGIN");
            return;
        }
        SolicitarCaronaDialog dlg = new SolicitarCaronaDialog(SwingUtilities.getWindowAncestor(this), apiService, c, this::refreshData);
        dlg.setVisible(true);
    }

    private void openGerenciarDialog(Carona c) {
        if (!apiService.isAuthenticated()) {
            navigateTo.accept("LOGIN");
            return;
        }
        ReservasCaronaDialog dlg = new ReservasCaronaDialog(SwingUtilities.getWindowAncestor(this), apiService, c, this::refreshData);
        dlg.setVisible(true);
    }
}
