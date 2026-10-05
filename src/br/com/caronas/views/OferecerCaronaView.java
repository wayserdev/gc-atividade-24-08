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

public class OferecerCaronaView extends JPanel {
    private final ApiService apiService;
    private final Consumer<String> navigateTo;

    private JComboBox<Veiculo> cbVeiculo;
    private JComboBox<String> cbDirecao;
    private JComboBox<Bairro> cbBairro;
    private JComboBox<Campus> cbCampus;
    private ModernTextField txtPontoEncontro;
    private ModernTextField txtHorarioSaida;
    private JSpinner spinAssentos;
    private ModernTextField txtValor;

    private JLabel lblPreviewRoute;
    private JLabel lblPreviewDetails;
    private JLabel lblStatus;

    public OferecerCaronaView(ApiService apiService, Consumer<String> navigateTo) {
        this.apiService = apiService;
        this.navigateTo = navigateTo;

        initUI();
        refreshVeiculos();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        // LEFT: Form Card
        RoundedPanel formCard = new RoundedPanel(16);
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lblTitle = new JLabel("Publicar Nova Carona");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Compartilhe os assentos livres do seu veiculo com colegas.");
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);
        headerLeft.add(lblTitle);
        headerLeft.add(Box.createVerticalStrut(2));
        headerLeft.add(lblSub);

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setOpaque(false);

        // 1. Veículo
        JPanel veiculoRow = new JPanel(new BorderLayout(8, 0));
        veiculoRow.setOpaque(false);
        cbVeiculo = new JComboBox<>();
        cbVeiculo.setFont(AppTheme.FONT_BODY);
        cbVeiculo.addActionListener(e -> updatePreview());

        RoundedButton btnAddCar = new RoundedButton("+ Novo Carro", RoundedButton.ButtonStyle.SECONDARY);
        btnAddCar.setFont(AppTheme.FONT_SMALL_BOLD);
        btnAddCar.addActionListener(e -> openAddVeiculoDialog());

        veiculoRow.add(cbVeiculo, BorderLayout.CENTER);
        veiculoRow.add(btnAddCar, BorderLayout.EAST);

        // 2. Direção
        cbDirecao = new JComboBox<>(new String[]{"Bairro -> Campus", "Campus -> Bairro"});
        cbDirecao.setFont(AppTheme.FONT_BODY);
        cbDirecao.addActionListener(e -> updatePreview());

        // 3. Bairro
        DefaultComboBoxModel<Bairro> modelBairros = new DefaultComboBoxModel<>();
        for (Bairro b : apiService.getBairros()) modelBairros.addElement(b);
        cbBairro = new JComboBox<>(modelBairros);
        cbBairro.setFont(AppTheme.FONT_BODY);
        cbBairro.addActionListener(e -> updatePreview());

        // 4. Campus
        DefaultComboBoxModel<Campus> modelCampi = new DefaultComboBoxModel<>();
        for (Campus c : apiService.getCampi()) modelCampi.addElement(c);
        cbCampus = new JComboBox<>(modelCampi);
        cbCampus.setFont(AppTheme.FONT_BODY);
        cbCampus.addActionListener(e -> updatePreview());

        // 5. Ponto de encontro
        txtPontoEncontro = new ModernTextField("Em frente ao ponto de onibus da praca central");
        txtPontoEncontro.addCaretListener(e -> updatePreview());

        // 6. Horário
        txtHorarioSaida = new ModernTextField("Hoje, 07:30");
        txtHorarioSaida.addCaretListener(e -> updatePreview());

        // 7. Assentos & Valor Row
        JPanel seatPriceRow = new JPanel(new GridLayout(1, 2, 12, 0));
        seatPriceRow.setOpaque(false);

        spinAssentos = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        spinAssentos.setFont(AppTheme.FONT_HEADER);
        spinAssentos.addChangeListener(e -> updatePreview());

        txtValor = new ModernTextField("5.00");
        txtValor.addCaretListener(e -> updatePreview());

        seatPriceRow.add(createFieldGroup("VAGAS DISPONIVEIS:", spinAssentos));
        seatPriceRow.add(createFieldGroup("VALOR POR VAGA (R$):", txtValor));

        formFields.add(createFieldGroup("VEICULO UTILIZADO:", veiculoRow));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(createFieldGroup("DIRECAO DO PERCURSO:", cbDirecao));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(createFieldGroup("BAIRRO:", cbBairro));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(createFieldGroup("CAMPUS:", cbCampus));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(createFieldGroup("PONTO DE ENCONTRO:", txtPontoEncontro));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(createFieldGroup("HORARIO DE SAIDA:", txtHorarioSaida));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(seatPriceRow);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);

        RoundedButton btnPublicar = new RoundedButton("Publicar Carona (POST /caronas)", RoundedButton.ButtonStyle.PRIMARY);
        btnPublicar.setFont(AppTheme.FONT_BODY_BOLD);
        btnPublicar.setPreferredSize(new Dimension(0, 42));
        btnPublicar.addActionListener(e -> submitCarona());

        JPanel bottomForm = new JPanel(new BorderLayout(0, 6));
        bottomForm.setOpaque(false);
        bottomForm.add(lblStatus, BorderLayout.NORTH);
        bottomForm.add(btnPublicar, BorderLayout.SOUTH);

        formCard.add(headerLeft, BorderLayout.NORTH);
        formCard.add(formFields, BorderLayout.CENTER);
        formCard.add(bottomForm, BorderLayout.SOUTH);

        // RIGHT: Live Route Preview Card
        RoundedPanel previewCard = new RoundedPanel(16);
        previewCard.setLayout(new BorderLayout(0, 16));
        previewCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lblPreviewTitle = new JLabel("Pre-visualizacao da Sua Carona");
        lblPreviewTitle.setFont(AppTheme.FONT_SUBTITLE);
        lblPreviewTitle.setForeground(AppTheme.getTextPrimary());

        RoundedPanel cardBox = new RoundedPanel(14);
        cardBox.setCustomBackground(AppTheme.isDarkMode() ? new Color(0x13, 0x1B, 0x2E) : new Color(0xEE, 0xF2, 0xFF));
        cardBox.setLayout(new BorderLayout(0, 10));
        cardBox.setBorder(new EmptyBorder(16, 18, 16, 18));

        lblPreviewRoute = new JLabel("Origem: Setor Bueno -> Destino: Campus Central UEG");
        lblPreviewRoute.setFont(AppTheme.FONT_HEADER);
        lblPreviewRoute.setForeground(ModernColors.PRIMARY_LIGHT);

        lblPreviewDetails = new JLabel("Horario: Hoje, 07:30 | 3 vagas | R$ 5,00");
        lblPreviewDetails.setFont(AppTheme.FONT_BODY);
        lblPreviewDetails.setForeground(AppTheme.getTextSecondary());

        cardBox.add(lblPreviewRoute, BorderLayout.NORTH);
        cardBox.add(lblPreviewDetails, BorderLayout.CENTER);

        JPanel tipsBox = new JPanel();
        tipsBox.setLayout(new BoxLayout(tipsBox, BoxLayout.Y_AXIS));
        tipsBox.setOpaque(false);

        JLabel lblTipsTitle = new JLabel("Dicas para Motoristas Universitarios:");
        lblTipsTitle.setFont(AppTheme.FONT_BODY_BOLD);
        lblTipsTitle.setForeground(AppTheme.getTextPrimary());

        tipsBox.add(lblTipsTitle);
        tipsBox.add(Box.createVerticalStrut(10));
        tipsBox.add(createTipItem("[Pontualidade]", "Esteja no ponto no horario combinado para evitar atrasos nas aulas."));
        tipsBox.add(Box.createVerticalStrut(8));
        tipsBox.add(createTipItem("[Rateio Justo]", "O valor da carona serve para dividir os custos de combustivel."));
        tipsBox.add(Box.createVerticalStrut(8));
        tipsBox.add(createTipItem("[Comunicacao]", "Fique atento as notificacoes de pedidos de carona dos colegas."));
        tipsBox.add(Box.createVerticalStrut(8));
        tipsBox.add(createTipItem("[Seguranca]", "Apenas estudantes autenticados participam da rede academica."));

        previewCard.add(lblPreviewTitle, BorderLayout.NORTH);
        previewCard.add(cardBox, BorderLayout.CENTER);
        previewCard.add(tipsBox, BorderLayout.SOUTH);

        grid.add(formCard);
        grid.add(previewCard);

        add(grid, BorderLayout.CENTER);
    }

    private JPanel createFieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createTipItem(String tag, String desc) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel("<html><b>" + tag + "</b> " + desc + "</html>");
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    public void refreshVeiculos() {
        DefaultComboBoxModel<Veiculo> model = new DefaultComboBoxModel<>();
        ApiResponse<List<Veiculo>> resp = apiService.getMeusVeiculos();
        List<Veiculo> lista = resp.getData();

        if (lista != null) {
            for (Veiculo v : lista) {
                model.addElement(v);
            }
        }
        cbVeiculo.setModel(model);

        if (model.getSize() > 0) {
            spinAssentos.setModel(new SpinnerNumberModel(model.getElementAt(0).getCapacidade_assentos(), 1, model.getElementAt(0).getCapacidade_assentos(), 1));
        }

        updatePreview();
    }

    private void updatePreview() {
        Bairro b = (Bairro) cbBairro.getSelectedItem();
        Campus c = (Campus) cbCampus.getSelectedItem();
        int dirIdx = cbDirecao.getSelectedIndex();
        String horario = txtHorarioSaida.getText().trim();
        String valorStr = txtValor.getText().trim();
        int vagas = (Integer) spinAssentos.getValue();

        String bNome = b != null ? b.getNome() : "Bairro";
        String cNome = c != null ? c.getNome() : "Campus";

        if (dirIdx == 0) {
            lblPreviewRoute.setText("Origem: " + bNome + " -> Destino: " + cNome);
        } else {
            lblPreviewRoute.setText("Origem: " + cNome + " -> Destino: " + bNome);
        }

        lblPreviewDetails.setText("Horario: " + (horario.isEmpty() ? "A combinar" : horario) + " | " + vagas + " vagas | R$ " + (valorStr.isEmpty() ? "0,00" : valorStr));
        repaint();
    }

    private void openAddVeiculoDialog() {
        VeiculoDialog dlg = new VeiculoDialog(SwingUtilities.getWindowAncestor(this), apiService, null, this::refreshVeiculos);
        dlg.setVisible(true);
    }

    private void submitCarona() {
        if (!apiService.isAuthenticated()) {
            ToastNotification.show(this, "Login Necessario", "Faca login para oferecer carona.", ToastNotification.ToastType.WARNING);
            navigateTo.accept("LOGIN");
            return;
        }

        Veiculo veiculo = (Veiculo) cbVeiculo.getSelectedItem();
        if (veiculo == null) {
            lblStatus.setText("Cadastre um veiculo primeiro para oferecer carona.");
            lblStatus.setForeground(ModernColors.WARNING);
            openAddVeiculoDialog();
            return;
        }

        Bairro bairro = (Bairro) cbBairro.getSelectedItem();
        Campus campus = (Campus) cbCampus.getSelectedItem();
        String direcao = cbDirecao.getSelectedIndex() == 0 ? "BAIRRO_PARA_CAMPUS" : "CAMPUS_PARA_BAIRRO";
        String ponto = txtPontoEncontro.getText().trim();
        String horario = txtHorarioSaida.getText().trim();
        int assentos = (Integer) spinAssentos.getValue();

        double valor = 0.0;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
        } catch (Exception e) {
            lblStatus.setText("Valor de contribuicao invalido.");
            lblStatus.setForeground(ModernColors.DANGER);
            return;
        }

        if (bairro == null || campus == null || horario.isEmpty()) {
            lblStatus.setText("Preencha todos os campos obrigatorios.");
            lblStatus.setForeground(ModernColors.WARNING);
            return;
        }

        ApiResponse<Carona> resp = apiService.oferecerCarona(
                veiculo.getId(),
                direcao,
                bairro.getId(),
                campus.getId(),
                ponto,
                horario,
                assentos,
                valor
        );

        if (resp.isSuccess()) {
            ToastNotification.show(SwingUtilities.getWindowAncestor(this), "Carona Publicada!", "Sua carona foi agendada com sucesso.", ToastNotification.ToastType.SUCCESS);
            lblStatus.setText("Carona cadastrada com sucesso!");
            lblStatus.setForeground(ModernColors.SUCCESS);
            if (navigateTo != null) {
                navigateTo.accept("MINHAS_CARONAS");
            }
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro ao publicar carona", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
