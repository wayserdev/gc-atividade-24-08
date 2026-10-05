package br.com.caronas.components;

import br.com.caronas.model.Carona;
import br.com.caronas.model.Usuario;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class CaronaCard extends RoundedPanel {
    private final Carona carona;
    private final Usuario usuarioLogado;
    private final Consumer<Carona> onSolicitar;
    private final Consumer<Carona> onGerenciar;

    public CaronaCard(Carona carona, Usuario usuarioLogado, Consumer<Carona> onSolicitar, Consumer<Carona> onGerenciar) {
        super(16);
        this.carona = carona;
        this.usuarioLogado = usuarioLogado;
        this.onSolicitar = onSolicitar;
        this.onGerenciar = onGerenciar;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setOpaque(false);

        // --- TOP ROW: Badges & Price ---
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);

        BadgeLabel badgeDir = BadgeLabel.forDirecao(carona.getDirecao());
        BadgeLabel badgeStatus = BadgeLabel.forCaronaStatus(carona.getStatus());
        BadgeLabel badgeVagas = BadgeLabel.forVagas(carona.getAssentos_disponiveis(), carona.getAssentos_totais());

        leftBadges.add(badgeDir);
        leftBadges.add(badgeStatus);
        leftBadges.add(badgeVagas);

        // Price Tag
        RoundedPanel priceTag = new RoundedPanel(10);
        priceTag.setCustomBackground(new Color(0x06, 0x5F, 0x46));
        priceTag.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 4));
        JLabel lblPrice = new JLabel(String.format("R$ %.2f / vaga", carona.getValor_contribuicao()));
        lblPrice.setFont(AppTheme.FONT_BODY_BOLD);
        lblPrice.setForeground(new Color(0xA7, 0xF3, 0xD0));
        priceTag.add(lblPrice);

        topRow.add(leftBadges, BorderLayout.WEST);
        topRow.add(priceTag, BorderLayout.EAST);

        // --- CENTER: Route & Details ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Route row
        JPanel routeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        routeRow.setOpaque(false);

        JLabel lblOrigem = new JLabel("Origem: " + carona.getOrigemFormatada());
        lblOrigem.setFont(AppTheme.FONT_HEADER);
        lblOrigem.setForeground(AppTheme.getTextPrimary());

        JLabel lblArrow = new JLabel("  ->  ");
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArrow.setForeground(ModernColors.PRIMARY_LIGHT);

        JLabel lblDestino = new JLabel("Destino: " + carona.getDestinoFormatado());
        lblDestino.setFont(AppTheme.FONT_HEADER);
        lblDestino.setForeground(ModernColors.PRIMARY_LIGHT);

        routeRow.add(lblOrigem);
        routeRow.add(lblArrow);
        routeRow.add(lblDestino);

        // Meeting point & time
        JPanel detailRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 2));
        detailRow.setOpaque(false);

        JLabel lblTime = new JLabel("Horario: " + carona.getHorarioSaida());
        lblTime.setFont(AppTheme.FONT_BODY_BOLD);
        lblTime.setForeground(ModernColors.ACCENT_CYAN);

        JLabel lblMeeting = new JLabel("Ponto de Encontro: " + (carona.getPonto_encontro() != null ? carona.getPonto_encontro() : "A combinar"));
        lblMeeting.setFont(AppTheme.FONT_BODY);
        lblMeeting.setForeground(AppTheme.getTextSecondary());

        JLabel lblCar = new JLabel("Veiculo: " + (carona.getVeiculo_modelo() != null ? carona.getVeiculo_modelo() : "Carro") + " (" + (carona.getVeiculo_placa() != null ? carona.getVeiculo_placa() : "") + ")");
        lblCar.setFont(AppTheme.FONT_SMALL);
        lblCar.setForeground(AppTheme.getTextMuted());

        detailRow.add(lblTime);
        detailRow.add(lblMeeting);
        detailRow.add(lblCar);

        centerPanel.add(routeRow);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(detailRow);

        // --- BOTTOM ROW: Driver Profile & Action Button ---
        JPanel bottomRow = new JPanel(new BorderLayout(14, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(6, 0, 0, 0));

        // Driver Info
        JPanel driverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        driverPanel.setOpaque(false);

        AvatarPanel avatar = new AvatarPanel(38);
        avatar.setUser(carona.getMotorista_nome(), carona.getMotorista_foto());

        JPanel driverInfo = new JPanel();
        driverInfo.setLayout(new BoxLayout(driverInfo, BoxLayout.Y_AXIS));
        driverInfo.setOpaque(false);

        JLabel lblDriverName = new JLabel(carona.getMotorista_nome() != null ? carona.getMotorista_nome() : "Motorista Universitario");
        lblDriverName.setFont(AppTheme.FONT_BODY_BOLD);
        lblDriverName.setForeground(AppTheme.getTextPrimary());

        JPanel driverSubRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        driverSubRow.setOpaque(false);

        JLabel lblCourse = new JLabel(carona.getMotorista_curso() != null ? carona.getMotorista_curso() : "Estudante");
        lblCourse.setFont(AppTheme.FONT_SMALL);
        lblCourse.setForeground(AppTheme.getTextSecondary());

        JLabel lblRating = new JLabel("Nota: " + (carona.getMotorista_avaliacao() != null ? carona.getMotorista_avaliacao() : "5.00") + " / 5.00");
        lblRating.setFont(AppTheme.FONT_SMALL_BOLD);
        lblRating.setForeground(ModernColors.GOLD);

        driverSubRow.add(lblCourse);
        driverSubRow.add(lblRating);

        driverInfo.add(lblDriverName);
        driverInfo.add(driverSubRow);

        driverPanel.add(avatar);
        driverPanel.add(driverInfo);

        // Right Button
        boolean isMine = usuarioLogado != null && usuarioLogado.getId().equals(carona.getMotorista_id());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);

        if (isMine) {
            RoundedButton btnGerenciar = new RoundedButton("Gerenciar Minha Carona", RoundedButton.ButtonStyle.SECONDARY);
            btnGerenciar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnGerenciar.addActionListener(e -> {
                if (onGerenciar != null) onGerenciar.accept(carona);
            });
            actionPanel.add(btnGerenciar);
        } else if (carona.isAgendada() && carona.getAssentos_disponiveis() > 0) {
            RoundedButton btnSolicitar = new RoundedButton("Solicitar Vaga", RoundedButton.ButtonStyle.PRIMARY);
            btnSolicitar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnSolicitar.addActionListener(e -> {
                if (onSolicitar != null) onSolicitar.accept(carona);
            });
            actionPanel.add(btnSolicitar);
        } else if (!carona.isAgendada()) {
            RoundedButton btnStatus = new RoundedButton(carona.getStatus(), RoundedButton.ButtonStyle.GHOST);
            btnStatus.setEnabled(false);
            actionPanel.add(btnStatus);
        } else {
            RoundedButton btnLotado = new RoundedButton("Vagas Esgotadas", RoundedButton.ButtonStyle.GHOST);
            btnLotado.setEnabled(false);
            actionPanel.add(btnLotado);
        }

        bottomRow.add(driverPanel, BorderLayout.WEST);
        bottomRow.add(actionPanel, BorderLayout.EAST);

        add(topRow, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);
    }
}
