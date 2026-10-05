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

public class BuscarCaronasView extends JPanel {
    private final ApiService apiService;

    private JComboBox<String> cbDirecao;
    private JComboBox<Object> cbBairro;
    private JComboBox<Object> cbCampus;
    private ModernTextField txtBusca;
    private JPanel resultsPanel;
    private JLabel lblTotalResults;

    public BuscarCaronasView(ApiService apiService) {
        this.apiService = apiService;
        initUI();
        executeSearch();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Top Filter Card
        RoundedPanel filterCard = new RoundedPanel(16);
        filterCard.setLayout(new BorderLayout(0, 12));
        filterCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Buscar Caronas Universitarias");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        // Filters row
        JPanel filterRow = new JPanel(new GridLayout(2, 2, 12, 10));
        filterRow.setOpaque(false);

        // Direção
        cbDirecao = new JComboBox<>(new String[]{"Todas as Direcoes", "Bairro -> Campus", "Campus -> Bairro"});
        cbDirecao.setFont(AppTheme.FONT_BODY);

        // Bairro
        DefaultComboBoxModel<Object> modelBairros = new DefaultComboBoxModel<>();
        modelBairros.addElement("Todos os Bairros");
        for (Bairro b : apiService.getBairros()) {
            modelBairros.addElement(b);
        }
        cbBairro = new JComboBox<>(modelBairros);
        cbBairro.setFont(AppTheme.FONT_BODY);

        // Campus
        DefaultComboBoxModel<Object> modelCampi = new DefaultComboBoxModel<>();
        modelCampi.addElement("Todos os Campi");
        for (Campus c : apiService.getCampi()) {
            modelCampi.addElement(c);
        }
        cbCampus = new JComboBox<>(modelCampi);
        cbCampus.setFont(AppTheme.FONT_BODY);

        // Search text
        txtBusca = new ModernTextField("Buscar por motorista, ponto de encontro...");
        txtBusca.addActionListener(e -> executeSearch());

        filterRow.add(createLabeledFilter("DIRECAO DO TRAJETO:", cbDirecao));
        filterRow.add(createLabeledFilter("BAIRRO DE ORIGEM / DESTINO:", cbBairro));
        filterRow.add(createLabeledFilter("CAMPUS UNIVERSITARIO:", cbCampus));
        filterRow.add(createLabeledFilter("PALAVRA-CHAVE / MOTORISTA:", txtBusca));

        // Filter Actions
        JPanel filterActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterActions.setOpaque(false);

        RoundedButton btnLimpar = new RoundedButton("Limpar Filtros", RoundedButton.ButtonStyle.GHOST);
        btnLimpar.addActionListener(e -> {
            cbDirecao.setSelectedIndex(0);
            cbBairro.setSelectedIndex(0);
            cbCampus.setSelectedIndex(0);
            txtBusca.setText("");
            executeSearch();
        });

        RoundedButton btnBuscar = new RoundedButton("Filtrar Caronas", RoundedButton.ButtonStyle.PRIMARY);
        btnBuscar.addActionListener(e -> executeSearch());

        filterActions.add(btnLimpar);
        filterActions.add(btnBuscar);

        filterCard.add(lblTitle, BorderLayout.NORTH);
        filterCard.add(filterRow, BorderLayout.CENTER);
        filterCard.add(filterActions, BorderLayout.SOUTH);

        // Results Section
        JPanel resultsContainer = new JPanel(new BorderLayout(0, 10));
        resultsContainer.setOpaque(false);

        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setOpaque(false);

        lblTotalResults = new JLabel("Encontrando caronas disponiveis...");
        lblTotalResults.setFont(AppTheme.FONT_BODY_BOLD);
        lblTotalResults.setForeground(AppTheme.getTextSecondary());

        resultsHeader.add(lblTotalResults, BorderLayout.WEST);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        resultsContainer.add(resultsHeader, BorderLayout.NORTH);
        resultsContainer.add(scroll, BorderLayout.CENTER);

        add(filterCard, BorderLayout.NORTH);
        add(resultsContainer, BorderLayout.CENTER);
    }

    private JPanel createLabeledFilter(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void executeSearch() {
        String direcao = null;
        if (cbDirecao.getSelectedIndex() == 1) direcao = "BAIRRO_PARA_CAMPUS";
        else if (cbDirecao.getSelectedIndex() == 2) direcao = "CAMPUS_PARA_BAIRRO";

        String bairroId = null;
        if (cbBairro.getSelectedItem() instanceof Bairro) {
            bairroId = ((Bairro) cbBairro.getSelectedItem()).getId();
        }

        String campusId = null;
        if (cbCampus.getSelectedItem() instanceof Campus) {
            campusId = ((Campus) cbCampus.getSelectedItem()).getId();
        }

        String busca = txtBusca.getText().trim();
        if (busca.equals("Buscar por motorista, ponto de encontro...")) {
            busca = "";
        }

        ApiResponse<List<Carona>> resp = apiService.buscarCaronas(bairroId, campusId, direcao, busca, "TODAS_ATIVAS");
        List<Carona> lista = resp.getData();

        resultsPanel.removeAll();

        if (lista == null || lista.isEmpty()) {
            lblTotalResults.setText("Nenhuma carona encontrada para os filtros selecionados.");

            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(50, 20, 50, 20));

            JLabel lblEmpty = new JLabel("Nenhuma carona encontrada com estes filtros.");
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblEmptySub = new JLabel("Tente alterar os filtros ou carregue as caronas de teste.");
            lblEmptySub.setFont(AppTheme.FONT_SMALL);
            lblEmptySub.setForeground(AppTheme.getTextMuted());
            lblEmptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundedButton btnSeed = new RoundedButton("Carregar Caronas de Teste", RoundedButton.ButtonStyle.PRIMARY);
            btnSeed.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSeed.addActionListener(e -> {
                if (apiService instanceof MockApiService) {
                    ((MockApiService) apiService).initDefaultData();
                    executeSearch();
                }
            });

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(4));
            empty.add(lblEmptySub);
            empty.add(Box.createVerticalStrut(12));
            empty.add(btnSeed);

            resultsPanel.add(empty);
        } else {
            lblTotalResults.setText("Exibindo " + lista.size() + " carona(s) disponivel(is)");
            for (Carona c : lista) {
                CaronaCard card = new CaronaCard(
                        c,
                        apiService.getUsuarioLogado(),
                        this::openSolicitarDialog,
                        this::openGerenciarDialog
                );
                resultsPanel.add(card);
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void openSolicitarDialog(Carona c) {
        if (!apiService.isAuthenticated()) {
            ToastNotification.show(this, "Login Necessario", "Faca login para solicitar vaga.", ToastNotification.ToastType.WARNING);
            return;
        }
        SolicitarCaronaDialog dlg = new SolicitarCaronaDialog(SwingUtilities.getWindowAncestor(this), apiService, c, this::executeSearch);
        dlg.setVisible(true);
    }

    private void openGerenciarDialog(Carona c) {
        if (!apiService.isAuthenticated()) return;
        ReservasCaronaDialog dlg = new ReservasCaronaDialog(SwingUtilities.getWindowAncestor(this), apiService, c, this::executeSearch);
        dlg.setVisible(true);
    }
}
