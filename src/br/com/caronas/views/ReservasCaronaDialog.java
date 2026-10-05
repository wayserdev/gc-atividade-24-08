package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.Carona;
import br.com.caronas.model.Reserva;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ReservasCaronaDialog extends JDialog {
    private final ApiService apiService;
    private final Carona carona;
    private final Runnable onDataChanged;

    private JPanel listPanel;
    private JLabel lblVagasStatus;
    private BadgeLabel badgeCaronaStatus;

    public ReservasCaronaDialog(Window owner, ApiService apiService, Carona carona, Runnable onDataChanged) {
        super(owner, "Gerenciamento da Carona & Passageiros", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.carona = carona;
        this.onDataChanged = onDataChanged;

        initUI();
        loadReservas();
    }

    private void initUI() {
        setSize(780, 580);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(AppTheme.getBackground());
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header Card
        RoundedPanel headerCard = new RoundedPanel(14);
        headerCard.setLayout(new BorderLayout(16, 0));
        headerCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);

        JLabel lblRoute = new JLabel(carona.getOrigemFormatada() + "  ->  " + carona.getDestinoFormatado());
        lblRoute.setFont(AppTheme.FONT_TITLE);
        lblRoute.setForeground(AppTheme.getTextPrimary());

        JLabel lblDetails = new JLabel("Horário: " + carona.getHorarioSaida() + "  •  Veículo: " + carona.getVeiculo_modelo() + " (" + carona.getVeiculo_placa() + ")  •  Contribuição: R$ " + String.format("%.2f", carona.getValor_contribuicao()) + " / vaga");
        lblDetails.setFont(AppTheme.FONT_BODY);
        lblDetails.setForeground(AppTheme.getTextSecondary());

        headerLeft.add(lblRoute);
        headerLeft.add(Box.createVerticalStrut(4));
        headerLeft.add(lblDetails);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        headerRight.setOpaque(false);

        badgeCaronaStatus = BadgeLabel.forCaronaStatus(carona.getStatus());
        lblVagasStatus = new JLabel(carona.getAssentos_disponiveis() + "/" + carona.getAssentos_totais() + " vagas livres");
        lblVagasStatus.setFont(AppTheme.FONT_BODY_BOLD);
        lblVagasStatus.setForeground(ModernColors.SUCCESS);

        headerRight.add(badgeCaronaStatus);
        headerRight.add(lblVagasStatus);

        headerCard.add(headerLeft, BorderLayout.CENTER);
        headerCard.add(headerRight, BorderLayout.EAST);

        // Center: Requests List
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // Bottom: Ride Actions (Start, Finish, Cancel)
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setOpaque(false);

        if (carona.isAgendada()) {
            RoundedButton btnIniciar = new RoundedButton("Iniciar Viagem", RoundedButton.ButtonStyle.ACCENT);
            btnIniciar.addActionListener(e -> changeCaronaStatus("EM_ANDAMENTO"));

            RoundedButton btnCancelar = new RoundedButton("Cancelar Carona", RoundedButton.ButtonStyle.DANGER);
            btnCancelar.addActionListener(e -> changeCaronaStatus("CANCELADA"));

            leftActions.add(btnIniciar);
            leftActions.add(btnCancelar);
        } else if (carona.isEmAndamento()) {
            RoundedButton btnFinalizar = new RoundedButton("Finalizar Viagem", RoundedButton.ButtonStyle.PRIMARY);
            btnFinalizar.addActionListener(e -> changeCaronaStatus("FINALIZADA"));
            leftActions.add(btnFinalizar);
        }

        RoundedButton btnFechar = new RoundedButton("Fechar", RoundedButton.ButtonStyle.GHOST);
        btnFechar.addActionListener(e -> dispose());

        bottomBar.add(leftActions, BorderLayout.WEST);
        bottomBar.add(btnFechar, BorderLayout.EAST);

        root.add(headerCard, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void changeCaronaStatus(String newStatus) {
        ApiResponse<Carona> resp = apiService.atualizarStatusCarona(carona.getId(), newStatus);
        if (resp.isSuccess()) {
            carona.setStatus(newStatus);
            badgeCaronaStatus = BadgeLabel.forCaronaStatus(newStatus);
            ToastNotification.show(this, "Status Atualizado", "A carona agora está: " + newStatus, ToastNotification.ToastType.INFO);
            if (onDataChanged != null) onDataChanged.run();
            dispose();
        } else {
            ToastNotification.show(this, "Erro ao alterar status", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }

    private void loadReservas() {
        listPanel.removeAll();
        ApiResponse<List<Reserva>> resp = apiService.getReservasDaCarona(carona.getId());
        List<Reserva> lista = resp.getData();

        if (lista == null || lista.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

            JLabel lblEmpty = new JLabel("Nenhuma solicitação de reserva recebida até o momento.", SwingConstants.CENTER);
            lblEmpty.setFont(AppTheme.FONT_BODY);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(lblEmpty);
            listPanel.add(emptyPanel);
        } else {
            for (Reserva r : lista) {
                listPanel.add(createReservaCard(r));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        lblVagasStatus.setText(carona.getAssentos_disponiveis() + "/" + carona.getAssentos_totais() + " vagas livres");
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createReservaCard(Reserva r) {
        RoundedPanel card = new RoundedPanel(12);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Passenger info
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        AvatarPanel avatar = new AvatarPanel(42);
        avatar.setUser(r.getPassageiro_nome(), r.getPassageiro_foto());

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblName = new JLabel(r.getPassageiro_nome());
        lblName.setFont(AppTheme.FONT_BODY_BOLD);
        lblName.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel((r.getPassageiro_curso() != null ? r.getPassageiro_curso() : "Aluno") + " • Nota: " + (r.getPassageiro_avaliacao() != null ? r.getPassageiro_avaliacao() : "5.00") + " • Tel: " + (r.getPassageiro_telefone() != null ? r.getPassageiro_telefone() : "Sem fone"));
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        JLabel lblEmbarque = new JLabel("Ponto de Embarque: " + r.getPonto_embarque() + "  (" + r.getVagas_solicitadas() + " vaga(s))");
        lblEmbarque.setFont(AppTheme.FONT_SMALL_BOLD);
        lblEmbarque.setForeground(ModernColors.PRIMARY_LIGHT);

        info.add(lblName);
        info.add(Box.createVerticalStrut(2));
        info.add(lblSub);
        info.add(Box.createVerticalStrut(2));
        info.add(lblEmbarque);

        left.add(avatar);
        left.add(info);

        // Right actions
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        right.setOpaque(false);

        BadgeLabel badgeStatus = BadgeLabel.forReservaStatus(r.getStatus());
        right.add(badgeStatus);

        if (r.isPendente() && carona.isAgendada()) {
            RoundedButton btnAceitar = new RoundedButton("Aceitar", RoundedButton.ButtonStyle.PRIMARY);
            btnAceitar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnAceitar.addActionListener(e -> {
                ApiResponse<Reserva> res = apiService.responderReserva(r.getId(), true);
                if (res.isSuccess()) {
                    ToastNotification.show(this, "Reserva Aceita!", "O passageiro foi confirmado.", ToastNotification.ToastType.SUCCESS);
                    loadReservas();
                    if (onDataChanged != null) onDataChanged.run();
                } else {
                    ToastNotification.show(this, "Erro", res.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
                }
            });

            RoundedButton btnRecusar = new RoundedButton("Recusar", RoundedButton.ButtonStyle.DANGER);
            btnRecusar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnRecusar.addActionListener(e -> {
                ApiResponse<Reserva> res = apiService.responderReserva(r.getId(), false);
                if (res.isSuccess()) {
                    ToastNotification.show(this, "Reserva Recusada", "Solicitação rejeitada.", ToastNotification.ToastType.INFO);
                    loadReservas();
                    if (onDataChanged != null) onDataChanged.run();
                } else {
                    ToastNotification.show(this, "Erro", res.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
                }
            });

            right.add(btnAceitar);
            right.add(btnRecusar);
        } else if (r.isAceita() && carona.isFinalizada()) {
            RoundedButton btnAvaliar = new RoundedButton("Avaliar Passageiro", RoundedButton.ButtonStyle.SECONDARY);
            btnAvaliar.setFont(AppTheme.FONT_SMALL_BOLD);
            btnAvaliar.addActionListener(e -> {
                AvaliacaoDialog dlg = new AvaliacaoDialog(this, apiService, carona.getId(), r.getPassageiro_id(), r.getPassageiro_nome(), "COMO_MOTORISTA", () -> {
                    ToastNotification.show(this, "Sucesso", "Passageiro avaliado com sucesso!", ToastNotification.ToastType.SUCCESS);
                });
                dlg.setVisible(true);
            });
            right.add(btnAvaliar);
        }

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }
}
