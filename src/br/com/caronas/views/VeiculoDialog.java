package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.Veiculo;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VeiculoDialog extends JDialog {
    private final ApiService apiService;
    private final Veiculo veiculoParaEditar;
    private final Runnable onSuccess;

    private ModernTextField txtModelo;
    private ModernTextField txtPlaca;
    private JSpinner spinCapacidade;
    private JLabel lblStatus;

    public VeiculoDialog(Window owner, ApiService apiService, Veiculo veiculoParaEditar, Runnable onSuccess) {
        super(owner, veiculoParaEditar == null ? "Cadastrar Novo Veículo" : "Editar Veículo", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.veiculoParaEditar = veiculoParaEditar;
        this.onSuccess = onSuccess;

        initUI();
    }

    private void initUI() {
        setSize(460, 420);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.getBackground());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lblTitle = new JLabel(veiculoParaEditar == null ? "Novo Veículo" : "Editar Veículo");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Informe os dados do seu automóvel ou moto");
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel lblModelo = new JLabel("MODELO / MARCA (EX: HYUNDAI HB20):");
        lblModelo.setFont(AppTheme.FONT_SMALL_BOLD);
        lblModelo.setForeground(AppTheme.getTextSecondary());

        txtModelo = new ModernTextField(veiculoParaEditar != null ? veiculoParaEditar.getModelo() : "");

        JLabel lblPlaca = new JLabel("PLACA (PADRÃO MERCOSUL OU ANTIGO):");
        lblPlaca.setFont(AppTheme.FONT_SMALL_BOLD);
        lblPlaca.setForeground(AppTheme.getTextSecondary());

        txtPlaca = new ModernTextField(veiculoParaEditar != null ? veiculoParaEditar.getPlaca() : "");

        JLabel lblCapacidade = new JLabel("CAPACIDADE DE ASSENTOS PARA PASSAGEIROS:");
        lblCapacidade.setFont(AppTheme.FONT_SMALL_BOLD);
        lblCapacidade.setForeground(AppTheme.getTextSecondary());

        int initialCap = veiculoParaEditar != null ? veiculoParaEditar.getCapacidade_assentos() : 4;
        spinCapacidade = new JSpinner(new SpinnerNumberModel(initialCap, 1, 8, 1));
        spinCapacidade.setFont(AppTheme.FONT_BODY_BOLD);
        spinCapacidade.setPreferredSize(new Dimension(80, 36));
        spinCapacidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);

        form.add(lblModelo);
        form.add(Box.createVerticalStrut(4));
        form.add(txtModelo);
        form.add(Box.createVerticalStrut(12));
        form.add(lblPlaca);
        form.add(Box.createVerticalStrut(4));
        form.add(txtPlaca);
        form.add(Box.createVerticalStrut(12));
        form.add(lblCapacidade);
        form.add(Box.createVerticalStrut(4));
        form.add(spinCapacidade);
        form.add(Box.createVerticalStrut(12));
        form.add(lblStatus);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancelar", RoundedButton.ButtonStyle.GHOST);
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSave = new RoundedButton(veiculoParaEditar == null ? "Cadastrar Veículo" : "Salvar Alterações", RoundedButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> submitVeiculo());

        buttons.add(btnCancel);
        buttons.add(btnSave);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void submitVeiculo() {
        String modelo = txtModelo.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        int capacidade = (Integer) spinCapacidade.getValue();

        if (modelo.isEmpty() || placa.isEmpty()) {
            lblStatus.setText("Preencha o modelo e a placa do veículo.");
            lblStatus.setForeground(ModernColors.WARNING);
            return;
        }

        ApiResponse<?> resp;
        if (veiculoParaEditar == null) {
            resp = apiService.cadastrarVeiculo(modelo, placa, capacidade);
        } else {
            resp = apiService.updateVeiculo(veiculoParaEditar.getId(), modelo, placa, capacidade);
        }

        if (resp.isSuccess()) {
            ToastNotification.show(getOwner(), "Veículo Salvo!", "Veículo registrado com sucesso na sua conta.", ToastNotification.ToastType.SUCCESS);
            if (onSuccess != null) onSuccess.run();
            dispose();
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro no Cadastro", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
