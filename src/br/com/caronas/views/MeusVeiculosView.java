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
import java.util.List;

public class MeusVeiculosView extends JPanel {
    private final ApiService apiService;
    private JPanel listPanel;
    private JLabel lblTotal;

    public MeusVeiculosView(ApiService apiService) {
        this.apiService = apiService;
        initUI();
        loadVeiculos();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Header
        RoundedPanel header = new RoundedPanel(16);
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);

        JLabel lblTitle = new JLabel("Meus Veiculos Cadastrados");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        lblTotal = new JLabel("Gerencie os carros e motos vinculados a sua conta de estudante.");
        lblTotal.setFont(AppTheme.FONT_BODY);
        lblTotal.setForeground(AppTheme.getTextSecondary());

        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(lblTotal);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        headerActions.setOpaque(false);

        RoundedButton btnNovo = new RoundedButton("+ Adicionar Veiculo", RoundedButton.ButtonStyle.PRIMARY);
        btnNovo.addActionListener(e -> {
            VeiculoDialog dlg = new VeiculoDialog(SwingUtilities.getWindowAncestor(this), apiService, null, this::loadVeiculos);
            dlg.setVisible(true);
        });

        headerActions.add(btnNovo);

        header.add(headerText, BorderLayout.CENTER);
        header.add(headerActions, BorderLayout.EAST);

        // Center: List
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

    public void loadVeiculos() {
        listPanel.removeAll();

        ApiResponse<List<Veiculo>> resp = apiService.getMeusVeiculos();
        List<Veiculo> lista = resp.getData();

        if (lista == null || lista.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel lblEmpty = new JLabel("Voce ainda nao possui veiculos cadastrados.");
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundedButton btnAdd = new RoundedButton("Cadastrar Meu Primeiro Veiculo", RoundedButton.ButtonStyle.PRIMARY);
            btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAdd.addActionListener(e -> {
                VeiculoDialog dlg = new VeiculoDialog(SwingUtilities.getWindowAncestor(this), apiService, null, this::loadVeiculos);
                dlg.setVisible(true);
            });

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(14));
            empty.add(btnAdd);
            listPanel.add(empty);
            lblTotal.setText("Nenhum veiculo cadastrado.");
        } else {
            lblTotal.setText("Voce possui " + lista.size() + " veiculo(s) cadastrado(s).");
            for (Veiculo v : lista) {
                listPanel.add(createVeiculoCard(v));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createVeiculoCard(Veiculo v) {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left Info
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        RoundedPanel tagBox = new RoundedPanel(10);
        tagBox.setPreferredSize(new Dimension(60, 42));
        tagBox.setCustomBackground(new Color(ModernColors.PRIMARY.getRed(), ModernColors.PRIMARY.getGreen(), ModernColors.PRIMARY.getBlue(), 35));
        tagBox.setLayout(new GridBagLayout());
        JLabel lblTag = new JLabel("[AUTO]");
        lblTag.setFont(AppTheme.FONT_SMALL_BOLD);
        lblTag.setForeground(ModernColors.PRIMARY);
        tagBox.add(lblTag);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblModelo = new JLabel(v.getModelo());
        lblModelo.setFont(AppTheme.FONT_HEADER);
        lblModelo.setForeground(AppTheme.getTextPrimary());

        JLabel lblPlaca = new JLabel("Placa: " + v.getPlaca() + " | Capacidade: " + v.getCapacidade_assentos() + " assentos livres");
        lblPlaca.setFont(AppTheme.FONT_BODY);
        lblPlaca.setForeground(AppTheme.getTextSecondary());

        info.add(lblModelo);
        info.add(Box.createVerticalStrut(2));
        info.add(lblPlaca);

        left.add(tagBox);
        left.add(info);

        // Right Actions
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.setOpaque(false);

        RoundedButton btnEdit = new RoundedButton("Editar", RoundedButton.ButtonStyle.SECONDARY);
        btnEdit.setFont(AppTheme.FONT_SMALL_BOLD);
        btnEdit.addActionListener(e -> {
            VeiculoDialog dlg = new VeiculoDialog(SwingUtilities.getWindowAncestor(this), apiService, v, this::loadVeiculos);
            dlg.setVisible(true);
        });

        RoundedButton btnDelete = new RoundedButton("Excluir", RoundedButton.ButtonStyle.DANGER);
        btnDelete.setFont(AppTheme.FONT_SMALL_BOLD);
        btnDelete.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Deseja remover este veiculo (" + v.getModelo() + ")?", "Excluir Veiculo", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                ApiResponse<String> del = apiService.deleteVeiculo(v.getId());
                if (del.isSuccess()) {
                    ToastNotification.show(this, "Veiculo Removido", "Veiculo excluido com sucesso.", ToastNotification.ToastType.INFO);
                    loadVeiculos();
                } else {
                    ToastNotification.show(this, "Erro", del.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
                }
            }
        });

        right.add(btnEdit);
        right.add(btnDelete);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }
}
