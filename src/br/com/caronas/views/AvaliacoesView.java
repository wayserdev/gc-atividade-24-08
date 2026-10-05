package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.Avaliacao;
import br.com.caronas.model.Usuario;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AvaliacoesView extends JPanel {
    private final ApiService apiService;

    private JLabel lblNotaMedia;
    private JLabel lblTotalReviews;
    private JPanel listPanel;
    private StarRatingPanel headerStarPanel;

    public AvaliacoesView(ApiService apiService) {
        this.apiService = apiService;
        initUI();
        loadAvaliacoes();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Top Header Card: Summary Score
        RoundedPanel header = new RoundedPanel(16);
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel scoreBox = new JPanel();
        scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));
        scoreBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Reputacao e Avaliacoes Recebidas");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JPanel starsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        starsRow.setOpaque(false);

        lblNotaMedia = new JLabel("4.85 / 5.00");
        lblNotaMedia.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblNotaMedia.setForeground(ModernColors.GOLD);

        headerStarPanel = new StarRatingPanel(5, false, 24);

        starsRow.add(lblNotaMedia);
        starsRow.add(headerStarPanel);

        lblTotalReviews = new JLabel("Baseado em avaliacoes de viagens como motorista e passageiro.");
        lblTotalReviews.setFont(AppTheme.FONT_BODY);
        lblTotalReviews.setForeground(AppTheme.getTextSecondary());

        scoreBox.add(lblTitle);
        scoreBox.add(Box.createVerticalStrut(4));
        scoreBox.add(starsRow);
        scoreBox.add(Box.createVerticalStrut(2));
        scoreBox.add(lblTotalReviews);

        header.add(scoreBox, BorderLayout.CENTER);

        // Center: Reviews List
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

    public void loadAvaliacoes() {
        listPanel.removeAll();

        Usuario u = apiService.getUsuarioLogado();
        if (u != null) {
            String media = u.getMedia_avaliacao() != null ? u.getMedia_avaliacao() : "5.00";
            lblNotaMedia.setText(media + " / 5.00");
            try {
                int rounded = (int) Math.round(Double.parseDouble(media));
                headerStarPanel.setRating(rounded);
            } catch (Exception ignored) {}
        }

        ApiResponse<List<Avaliacao>> resp = apiService.getMinhasAvaliacoes();
        List<Avaliacao> lista = resp.getData();

        if (lista == null || lista.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(60, 20, 60, 20));

            JLabel lblEmpty = new JLabel("Voce ainda nao recebeu avaliacoes.");
            lblEmpty.setFont(AppTheme.FONT_BODY_BOLD);
            lblEmpty.setForeground(AppTheme.getTextSecondary());
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblSub = new JLabel("Complete viagens como motorista ou passageiro para construir sua reputacao academica.");
            lblSub.setFont(AppTheme.FONT_SMALL);
            lblSub.setForeground(AppTheme.getTextMuted());
            lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(lblEmpty);
            empty.add(Box.createVerticalStrut(6));
            empty.add(lblSub);
            listPanel.add(empty);

            lblTotalReviews.setText("Nenhuma avaliacao recebida ate o momento.");
        } else {
            lblTotalReviews.setText("Baseado em " + lista.size() + " avaliacao(oes) de viagens realizadas.");
            for (Avaliacao a : lista) {
                listPanel.add(createAvaliacaoCard(a));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createAvaliacaoCard(Avaliacao a) {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Reviewer Info
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        AvatarPanel avatar = new AvatarPanel(42);
        avatar.setUser(a.getAvaliador_nome(), a.getAvaliador_foto());

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblName = new JLabel(a.getAvaliador_nome());
        lblName.setFont(AppTheme.FONT_BODY_BOLD);
        lblName.setForeground(AppTheme.getTextPrimary());

        JPanel starRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        starRow.setOpaque(false);
        StarRatingPanel cardStars = new StarRatingPanel(a.getNota(), false, 16);
        JLabel lblNotaNum = new JLabel("(" + a.getNota() + "/5)");
        lblNotaNum.setFont(AppTheme.FONT_SMALL_BOLD);
        lblNotaNum.setForeground(ModernColors.GOLD);
        starRow.add(cardStars);
        starRow.add(lblNotaNum);

        JLabel lblComment = new JLabel("\"" + (a.getComentario() != null ? a.getComentario() : "") + "\"");
        lblComment.setFont(AppTheme.FONT_BODY);
        lblComment.setForeground(AppTheme.getTextSecondary());

        info.add(lblName);
        info.add(Box.createVerticalStrut(2));
        info.add(starRow);
        info.add(Box.createVerticalStrut(4));
        info.add(lblComment);

        left.add(avatar);
        left.add(info);

        // Date & Role Badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.setOpaque(false);

        String roleText = "COMO_MOTORISTA".equalsIgnoreCase(a.getTipo()) ? "Avaliado como Motorista" : "Avaliado como Passageiro";
        BadgeLabel badgeTipo = new BadgeLabel(roleText, AppTheme.getSurface(), AppTheme.getTextPrimary());

        JLabel lblDate = new JLabel("Data: " + (a.getCriado_em() != null ? a.getCriado_em().split("T")[0] : "Recente"));
        lblDate.setFont(AppTheme.FONT_SMALL);
        lblDate.setForeground(AppTheme.getTextMuted());

        right.add(badgeTipo);
        right.add(lblDate);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }
}
