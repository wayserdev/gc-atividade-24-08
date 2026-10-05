package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.*;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.function.Consumer;

public class MinhasReservasView extends JPanel {
    private final ApiService apiService;
    private final Consumer<String> navigateTo;

    private JPanel listPanel;
    private JLabel lblTotal;

    public MinhasReservasView(ApiService apiService, Consumer<String> navigateTo) {
        this.apiService = apiService;
        this.navigateTo = navigateTo;

        initUI();
        loadReservas();
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

        JLabel lblTitle = new JLabel("Minhas Reservas como Passageiro");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        lblTotal = new JLabel("Acompanhe o status das suas solicitacoes de carona.");
        lblTotal.setFont(AppTheme.FONT_BODY);
        lblTotal.setForeground(AppTheme.getTextSecondary());

        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(lblTotal);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        headerActions.setOpaque(false);

        RoundedButton btnBuscar = new RoundedButton("Encontrar Mais Caronas", RoundedButton.ButtonStyle.PRIMARY);
        btnBuscar.addActionListener(e -> {
            if (navigateTo != null) navigateTo.accept("BUSCAR");
        });

        headerActions.add(btnBuscar);

        header.add(headerText, BorderLayout.CENTER);
        header.add(headerActions, BorderLayout.EAST);

        // Center: List of Reservations
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadReservas() {
        listPanel.removeAll();

        ApiResponse<List<Reserva>> resp = apiService.getMinhasReservas();
        List<Reserva> lista = resp.getData();

        if (lista == null || lista.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel lblEmpty = new JLabel("Voce ainda nao solicitou nenhuma carona como passageiro.");
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundedButton btnBuscar = new RoundedButton("Buscar Caronas Disponiveis", RoundedButton.ButtonStyle.PRIMARY);
            btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnBuscar.addActionListener(e -> {
                if (navigateTo != null) navigateTo.accept("BUSCAR");
            });

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(14));
            empty.add(btnBuscar);
            listPanel.add(empty);
            lblTotal.setText("Nenhuma reserva ativa.");
        } else {
            lblTotal.setText("Voce possui " + lista.size() + " reserva(s) no seu historico.");
            for (Reserva r : lista) {
                listPanel.add(createReservationCard(r));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createReservationCard(Reserva r) {
        RoundedPanel card = new RoundedPanel(16);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        Carona c = r.getCarona();

        // Top Row: Badges
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);

        BadgeLabel badgeReserva = BadgeLabel.forReservaStatus(r.getStatus());
        leftBadges.add(badgeReserva);

        if (c != null) {
            BadgeLabel badgeCarona = BadgeLabel.forCaronaStatus(c.getStatus());
            BadgeLabel badgeDir = BadgeLabel.forDirecao(c.getDirecao());
            leftBadges.add(badgeCarona);
            leftBadges.add(badgeDir);
        }

        double valorTotal = (c != null ? c.getValor_contribuicao() : 0.0) * r.getVagas_solicitadas();
        JLabel lblValor = new JLabel(String.format("Total: R$ %.2f (%d vaga(s))", valorTotal, r.getVagas_solicitadas()));
        lblValor.setFont(AppTheme.FONT_BODY_BOLD);
        lblValor.setForeground(ModernColors.SUCCESS);

        top.add(leftBadges, BorderLayout.WEST);
        top.add(lblValor, BorderLayout.EAST);

        // Center Details
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        String rotaText = c != null ? ("Origem: " + c.getOrigemFormatada() + "  ->  Destino: " + c.getDestinoFormatado()) : "Carona #" + r.getCarona_id();
        JLabel lblRoute = new JLabel(rotaText);
        lblRoute.setFont(AppTheme.FONT_HEADER);
        lblRoute.setForeground(AppTheme.getTextPrimary());

        String timeAndCar = c != null ? ("Horario: " + c.getHorarioSaida() + " | Veiculo: " + c.getVeiculo_modelo() + " (" + c.getVeiculo_placa() + ")") : "";
        JLabel lblTime = new JLabel(timeAndCar);
        lblTime.setFont(AppTheme.FONT_BODY);
        lblTime.setForeground(ModernColors.ACCENT_CYAN);

        JLabel lblEmbarque = new JLabel("Seu Ponto de Embarque: " + r.getPonto_embarque());
        lblEmbarque.setFont(AppTheme.FONT_SMALL_BOLD);
        lblEmbarque.setForeground(AppTheme.getTextSecondary());

        center.add(lblRoute);
        center.add(Box.createVerticalStrut(4));
        center.add(lblTime);
        center.add(Box.createVerticalStrut(2));
        center.add(lblEmbarque);

        // Bottom Row: Driver Profile & Action Buttons
        JPanel bottom = new JPanel(new BorderLayout(14, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel driverInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        driverInfo.setOpaque(false);

        if (c != null) {
            AvatarPanel avatar = new AvatarPanel(36);
            avatar.setUser(c.getMotorista_nome(), c.getMotorista_foto());

            JPanel driverText = new JPanel();
            driverText.setLayout(new BoxLayout(driverText, BoxLayout.Y_AXIS));
            driverText.setOpaque(false);

            JLabel lblDName = new JLabel("Motorista: " + c.getMotorista_nome() + " (Nota: " + (c.getMotorista_avaliacao() != null ? c.getMotorista_avaliacao() : "5.0") + ")");
            lblDName.setFont(AppTheme.FONT_BODY_BOLD);
            lblDName.setForeground(AppTheme.getTextPrimary());

            JLabel lblDSub = new JLabel("Curso: " + (c.getMotorista_curso() != null ? c.getMotorista_curso() : "Estudante") + " | Tel: " + (c.getMotorista_telefone() != null ? c.getMotorista_telefone() : "A combinar"));
            lblDSub.setFont(AppTheme.FONT_SMALL);
            lblDSub.setForeground(AppTheme.getTextSecondary());

            driverText.add(lblDName);
            driverText.add(lblDSub);

            driverInfo.add(avatar);
            driverInfo.add(driverText);
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (c != null && c.getMotorista_telefone() != null) {
            RoundedButton btnWhats = new RoundedButton("Copiar Contato", RoundedButton.ButtonStyle.GHOST);
            btnWhats.setFont(AppTheme.FONT_SMALL_BOLD);
            btnWhats.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(c.getMotorista_telefone()), null);
                ToastNotification.show(this, "Telefone Copiado!", "Numero do motorista: " + c.getMotorista_telefone(), ToastNotification.ToastType.INFO);
            });
            actions.add(btnWhats);
        }

        if (!r.isCancelada() && !r.isRecusada() && (c == null || !c.isFinalizada())) {
            RoundedButton btnCancelar = new RoundedButton("Cancelar Reserva", RoundedButton.ButtonStyle.DANGER);
            btnCancelar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnCancelar.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(this, "Deseja cancelar sua solicitacao de reserva?", "Cancelar Reserva", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    ApiResponse<String> delResp = apiService.cancelarReserva(r.getId());
                    if (delResp.isSuccess()) {
                        ToastNotification.show(this, "Reserva Cancelada", "Sua reserva foi cancelada com sucesso.", ToastNotification.ToastType.INFO);
                        loadReservas();
                    } else {
                        ToastNotification.show(this, "Erro", delResp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
                    }
                }
            });
            actions.add(btnCancelar);
        } else if (c != null && c.isFinalizada()) {
            RoundedButton btnAvaliar = new RoundedButton("Avaliar Motorista", RoundedButton.ButtonStyle.SECONDARY);
            btnAvaliar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnAvaliar.addActionListener(e -> {
                AvaliacaoDialog dlg = new AvaliacaoDialog(SwingUtilities.getWindowAncestor(this), apiService, c.getId(), c.getMotorista_id(), c.getMotorista_nome(), "COMO_PASSAGEIRO", () -> {
                    ToastNotification.show(this, "Avaliacao Enviada", "Obrigado por avaliar seu motorista!", ToastNotification.ToastType.SUCCESS);
                });
                dlg.setVisible(true);
            });
            actions.add(btnAvaliar);
        }

        bottom.add(driverInfo, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }
}
