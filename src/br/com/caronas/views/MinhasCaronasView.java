package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class MinhasCaronasView extends JPanel {
    private final ApiService apiService;
    private final Consumer<String> navigateTo;

    private JPanel caronasPanel;
    private JLabel lblTotal;
    private JComboBox<String> cbFiltroStatus;

    public MinhasCaronasView(ApiService apiService, Consumer<String> navigateTo) {
        this.apiService = apiService;
        this.navigateTo = navigateTo;

        initUI();
        loadCaronas();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Header Card
        RoundedPanel header = new RoundedPanel(16);
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);

        JLabel lblTitle = new JLabel("Minhas Caronas Ofertadas");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        lblTotal = new JLabel("Gerencie suas viagens, aceite passageiros e finalize trajetos.");
        lblTotal.setFont(AppTheme.FONT_BODY);
        lblTotal.setForeground(AppTheme.getTextSecondary());

        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(lblTotal);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        headerActions.setOpaque(false);

        cbFiltroStatus = new JComboBox<>(new String[]{"Todas as Caronas", "Apenas Agendadas", "Em Andamento", "Finalizadas / Historico"});
        cbFiltroStatus.setFont(AppTheme.FONT_BODY);
        cbFiltroStatus.addActionListener(e -> loadCaronas());

        RoundedButton btnNova = new RoundedButton("+ Nova Carona", RoundedButton.ButtonStyle.PRIMARY);
        btnNova.addActionListener(e -> {
            if (navigateTo != null) navigateTo.accept("OFERECER");
        });

        headerActions.add(cbFiltroStatus);
        headerActions.add(btnNova);

        header.add(headerText, BorderLayout.CENTER);
        header.add(headerActions, BorderLayout.EAST);

        // Center: List of Caronas
        caronasPanel = new JPanel();
        caronasPanel.setLayout(new BoxLayout(caronasPanel, BoxLayout.Y_AXIS));
        caronasPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(caronasPanel);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadCaronas() {
        caronasPanel.removeAll();

        ApiResponse<List<Carona>> resp = apiService.getMinhasCaronas();
        List<Carona> lista = resp.getData();

        int filterIdx = cbFiltroStatus.getSelectedIndex();

        int count = 0;
        if (lista != null) {
            for (Carona c : lista) {
                boolean show = true;
                if (filterIdx == 1) show = c.isAgendada();
                else if (filterIdx == 2) show = c.isEmAndamento();
                else if (filterIdx == 3) show = c.isFinalizada() || c.isCancelada();

                if (show) {
                    caronasPanel.add(createMyRideCard(c));
                    caronasPanel.add(Box.createVerticalStrut(12));
                    count++;
                }
            }
        }

        if (count == 0) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel lblEmpty = new JLabel("Voce ainda nao cadastrou caronas nesta categoria.");
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundedButton btnCriar = new RoundedButton("Oferecer Minha Primeira Carona", RoundedButton.ButtonStyle.PRIMARY);
            btnCriar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnCriar.addActionListener(e -> {
                if (navigateTo != null) navigateTo.accept("OFERECER");
            });

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(14));
            empty.add(btnCriar);
            caronasPanel.add(empty);
        }

        lblTotal.setText("Voce possui " + (lista != null ? lista.size() : 0) + " carona(s) cadastrada(s) como motorista.");

        caronasPanel.revalidate();
        caronasPanel.repaint();
    }

    private JPanel createMyRideCard(Carona c) {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Badges
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);

        BadgeLabel badgeDir = BadgeLabel.forDirecao(c.getDirecao());
        BadgeLabel badgeStatus = BadgeLabel.forCaronaStatus(c.getStatus());
        BadgeLabel badgeVagas = BadgeLabel.forVagas(c.getAssentos_disponiveis(), c.getAssentos_totais());

        leftBadges.add(badgeDir);
        leftBadges.add(badgeStatus);
        leftBadges.add(badgeVagas);

        JLabel lblValor = new JLabel(String.format("R$ %.2f / passageiro", c.getValor_contribuicao()));
        lblValor.setFont(AppTheme.FONT_BODY_BOLD);
        lblValor.setForeground(ModernColors.SUCCESS);

        top.add(leftBadges, BorderLayout.WEST);
        top.add(lblValor, BorderLayout.EAST);

        // Center Route
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel lblRoute = new JLabel("Origem: " + c.getOrigemFormatada() + "  ->  Destino: " + c.getDestinoFormatado());
        lblRoute.setFont(AppTheme.FONT_HEADER);
        lblRoute.setForeground(AppTheme.getTextPrimary());

        JLabel lblDetails = new JLabel("Horario: " + c.getHorarioSaida() + " | Veiculo: " + c.getVeiculo_modelo() + " (" + c.getVeiculo_placa() + ") | Ponto: " + c.getPonto_encontro());
        lblDetails.setFont(AppTheme.FONT_BODY);
        lblDetails.setForeground(AppTheme.getTextSecondary());

        center.add(lblRoute);
        center.add(Box.createVerticalStrut(4));
        center.add(lblDetails);

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);

        RoundedButton btnGerenciar = new RoundedButton("Ver Passageiros e Pedidos", RoundedButton.ButtonStyle.PRIMARY);
        btnGerenciar.setFont(AppTheme.FONT_SMALL_BOLD);
        btnGerenciar.addActionListener(e -> {
            ReservasCaronaDialog dlg = new ReservasCaronaDialog(SwingUtilities.getWindowAncestor(this), apiService, c, this::loadCaronas);
            dlg.setVisible(true);
        });

        bottom.add(btnGerenciar);

        if (c.isAgendada()) {
            RoundedButton btnIniciar = new RoundedButton("Iniciar Viagem", RoundedButton.ButtonStyle.ACCENT);
            btnIniciar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnIniciar.addActionListener(e -> {
                apiService.atualizarStatusCarona(c.getId(), "EM_ANDAMENTO");
                ToastNotification.show(this, "Viagem Iniciada!", "A carona foi marcada como em andamento.", ToastNotification.ToastType.INFO);
                loadCaronas();
            });

            RoundedButton btnCancelar = new RoundedButton("Cancelar Carona", RoundedButton.ButtonStyle.DANGER);
            btnCancelar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnCancelar.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar esta carona?", "Cancelar Carona", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    apiService.atualizarStatusCarona(c.getId(), "CANCELADA");
                    ToastNotification.show(this, "Carona Cancelada", "Os passageiros foram notificados.", ToastNotification.ToastType.WARNING);
                    loadCaronas();
                }
            });

            bottom.add(btnIniciar);
            bottom.add(btnCancelar);
        } else if (c.isEmAndamento()) {
            RoundedButton btnFinalizar = new RoundedButton("Finalizar Viagem", RoundedButton.ButtonStyle.SECONDARY);
            btnFinalizar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnFinalizar.addActionListener(e -> {
                apiService.atualizarStatusCarona(c.getId(), "FINALIZADA");
                ToastNotification.show(this, "Viagem Finalizada!", "Carona concluida com sucesso. Avalie seus passageiros!", ToastNotification.ToastType.SUCCESS);
                loadCaronas();
            });
            bottom.add(btnFinalizar);
        }

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }
}
