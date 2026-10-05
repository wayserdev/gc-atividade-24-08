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

public class SolicitarCaronaDialog extends JDialog {
    private final ApiService apiService;
    private final Carona carona;
    private final Runnable onSuccess;

    private JSpinner spinVagas;
    private ModernTextField txtPontoEmbarque;
    private JLabel lblTotal;
    private JLabel lblStatus;

    public SolicitarCaronaDialog(Window owner, ApiService apiService, Carona carona, Runnable onSuccess) {
        super(owner, "Solicitar Vaga na Carona", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.carona = carona;
        this.onSuccess = onSuccess;

        initUI();
    }

    private void initUI() {
        setSize(520, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.getBackground());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("Confirmar Solicitacao de Carona");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Envie seu pedido ao motorista " + carona.getMotorista_nome());
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        // Ride Summary Box
        RoundedPanel summaryBox = new RoundedPanel(12);
        summaryBox.setBorder(new EmptyBorder(12, 16, 12, 16));
        summaryBox.setLayout(new GridLayout(3, 1, 0, 4));

        JLabel lblTrajeto = new JLabel("Origem: " + carona.getOrigemFormatada() + "  ->  Destino: " + carona.getDestinoFormatado());
        lblTrajeto.setFont(AppTheme.FONT_BODY_BOLD);
        lblTrajeto.setForeground(ModernColors.PRIMARY_LIGHT);

        JLabel lblHorario = new JLabel("Horario: " + carona.getHorarioSaida() + " | " + carona.getAssentos_disponiveis() + " vagas disponiveis");
        lblHorario.setFont(AppTheme.FONT_SMALL);
        lblHorario.setForeground(AppTheme.getTextSecondary());

        JLabel lblVeiculo = new JLabel("Veiculo: " + carona.getVeiculo_modelo() + " (" + carona.getVeiculo_placa() + ") | Valor: R$ " + String.format("%.2f", carona.getValor_contribuicao()) + " / vaga");
        lblVeiculo.setFont(AppTheme.FONT_SMALL);
        lblVeiculo.setForeground(AppTheme.getTextMuted());

        summaryBox.add(lblTrajeto);
        summaryBox.add(lblHorario);
        summaryBox.add(lblVeiculo);

        // Form fields
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(12, 0, 12, 0));

        JLabel lblVagas = new JLabel("QUANTIDADE DE VAGAS DESEJADAS:");
        lblVagas.setFont(AppTheme.FONT_SMALL_BOLD);
        lblVagas.setForeground(AppTheme.getTextSecondary());

        int maxVagas = Math.max(1, carona.getAssentos_disponiveis());
        spinVagas = new JSpinner(new SpinnerNumberModel(1, 1, maxVagas, 1));
        spinVagas.setFont(AppTheme.FONT_HEADER);
        spinVagas.setPreferredSize(new Dimension(100, 36));
        spinVagas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spinVagas.addChangeListener(e -> updateTotalCalculado());

        JLabel lblPonto = new JLabel("SEU PONTO DE EMBARQUE / REFERENCIA:");
        lblPonto.setFont(AppTheme.FONT_SMALL_BOLD);
        lblPonto.setForeground(AppTheme.getTextSecondary());

        txtPontoEmbarque = new ModernTextField(carona.getPonto_encontro() != null ? carona.getPonto_encontro() : "Proximo a portaria");

        lblTotal = new JLabel("Contribuicao Total Estimada: R$ " + String.format("%.2f", carona.getValor_contribuicao()));
        lblTotal.setFont(AppTheme.FONT_HEADER);
        lblTotal.setForeground(ModernColors.SUCCESS);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);

        form.add(lblVagas);
        form.add(Box.createVerticalStrut(4));
        form.add(spinVagas);
        form.add(Box.createVerticalStrut(12));
        form.add(lblPonto);
        form.add(Box.createVerticalStrut(4));
        form.add(txtPontoEmbarque);
        form.add(Box.createVerticalStrut(14));
        form.add(lblTotal);
        form.add(Box.createVerticalStrut(8));
        form.add(lblStatus);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        RoundedButton btnCancelar = new RoundedButton("Cancelar", RoundedButton.ButtonStyle.GHOST);
        btnCancelar.addActionListener(e -> dispose());

        RoundedButton btnConfirmar = new RoundedButton("Confirmar Pedido (POST /reservas)", RoundedButton.ButtonStyle.PRIMARY);
        btnConfirmar.addActionListener(e -> submitReserva());

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnConfirmar);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(summaryBox, BorderLayout.NORTH);
        centerPanel.add(form, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void updateTotalCalculado() {
        int vagas = (Integer) spinVagas.getValue();
        double total = vagas * carona.getValor_contribuicao();
        lblTotal.setText(String.format("Contribuicao Total Estimada: R$ %.2f", total));
    }

    private void submitReserva() {
        int vagas = (Integer) spinVagas.getValue();
        String ponto = txtPontoEmbarque.getText().trim();

        if (ponto.isEmpty()) {
            lblStatus.setText("Informe seu ponto de embarque.");
            lblStatus.setForeground(ModernColors.WARNING);
            return;
        }

        ApiResponse<Reserva> resp = apiService.solicitarCarona(carona.getId(), vagas, ponto);
        if (resp.isSuccess()) {
            ToastNotification.show(getOwner(), "Solicitacao Enviada!", "O motorista foi notificado e respondera seu pedido.", ToastNotification.ToastType.SUCCESS);
            if (onSuccess != null) onSuccess.run();
            dispose();
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro na Reserva", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
