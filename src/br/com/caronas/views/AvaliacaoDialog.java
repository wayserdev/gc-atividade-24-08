package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.Avaliacao;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AvaliacaoDialog extends JDialog {
    private final ApiService apiService;
    private final String caronaId;
    private final String avaliadoId;
    private final String avaliadoNome;
    private final String tipo; // "COMO_PASSAGEIRO" ou "COMO_MOTORISTA"
    private final Runnable onSuccess;

    private StarRatingPanel starPanel;
    private JTextArea txtComentario;
    private JLabel lblStatus;

    public AvaliacaoDialog(Window owner, ApiService apiService, String caronaId, String avaliadoId, String avaliadoNome, String tipo, Runnable onSuccess) {
        super(owner, "Avaliar Experiência na Carona", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.caronaId = caronaId;
        this.avaliadoId = avaliadoId;
        this.avaliadoNome = avaliadoNome;
        this.tipo = tipo;
        this.onSuccess = onSuccess;

        initUI();
    }

    private void initUI() {
        setSize(480, 460);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.getBackground());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("Avaliação de Carona");
        lblTitle.setFont(AppTheme.FONT_TITLE);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Como foi sua experiência com " + avaliadoNome + "?");
        lblSub.setFont(AppTheme.FONT_BODY);
        lblSub.setForeground(AppTheme.getTextSecondary());

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        // Center Content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel lblNota = new JLabel("SELECIONE A NOTA (1 A 5 ESTRELAS):");
        lblNota.setFont(AppTheme.FONT_SMALL_BOLD);
        lblNota.setForeground(AppTheme.getTextSecondary());

        starPanel = new StarRatingPanel(5, true);

        JLabel lblComment = new JLabel("DEIXE UM COMENTÁRIO SOBRE A VIAGEM:");
        lblComment.setFont(AppTheme.FONT_SMALL_BOLD);
        lblComment.setForeground(AppTheme.getTextSecondary());

        txtComentario = new JTextArea(4, 20);
        txtComentario.setFont(AppTheme.FONT_BODY);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setBackground(AppTheme.isDarkMode() ? new Color(0x1E, 0x29, 0x3B) : Color.WHITE);
        txtComentario.setForeground(AppTheme.getTextPrimary());
        txtComentario.setCaretColor(AppTheme.getTextPrimary());
        txtComentario.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(txtComentario);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.getBorder(), 1));

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);

        center.add(lblNota);
        center.add(Box.createVerticalStrut(4));
        center.add(starPanel);
        center.add(Box.createVerticalStrut(16));
        center.add(lblComment);
        center.add(Box.createVerticalStrut(6));
        center.add(scroll);
        center.add(Box.createVerticalStrut(10));
        center.add(lblStatus);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancelar", RoundedButton.ButtonStyle.GHOST);
        btnCancel.addActionListener(e -> dispose());

        RoundedButton btnSend = new RoundedButton("Publicar Avaliação", RoundedButton.ButtonStyle.PRIMARY);
        btnSend.addActionListener(e -> submitAvaliacao());

        buttons.add(btnCancel);
        buttons.add(btnSend);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void submitAvaliacao() {
        int nota = starPanel.getRating();
        String comentario = txtComentario.getText().trim();

        if (comentario.isEmpty()) {
            lblStatus.setText("Por favor, escreva um breve comentário.");
            lblStatus.setForeground(ModernColors.WARNING);
            return;
        }

        ApiResponse<Avaliacao> resp = apiService.avaliarUsuario(caronaId, avaliadoId, nota, comentario, tipo);
        if (resp.isSuccess()) {
            ToastNotification.show(getOwner(), "Avaliação Enviada!", "Obrigado por ajudar a comunidade universitária!", ToastNotification.ToastType.SUCCESS);
            if (onSuccess != null) onSuccess.run();
            dispose();
        } else {
            lblStatus.setText(resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro ao Avaliar", resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
