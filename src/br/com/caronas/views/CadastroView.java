package br.com.caronas.views;

import br.com.caronas.components.*;
import br.com.caronas.model.ApiResponse;
import br.com.caronas.model.AuthResponse;
import br.com.caronas.model.Instituicao;
import br.com.caronas.service.ApiService;
import br.com.caronas.theme.AppTheme;
import br.com.caronas.theme.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CadastroView extends JPanel {
    private final ApiService apiService;
    private final Runnable onCadastroSuccess;
    private final Runnable onNavigateToLogin;

    private ModernTextField txtNome;
    private ModernTextField txtEmail;
    private ModernPasswordField txtSenha;
    private ModernTextField txtTelefone;
    private ModernTextField txtCurso;
    private JComboBox<Instituicao> cbInstituicao;
    private JLabel lblStatus;

    public CadastroView(ApiService apiService, Runnable onCadastroSuccess, Runnable onNavigateToLogin) {
        this.apiService = apiService;
        this.onCadastroSuccess = onCadastroSuccess;
        this.onNavigateToLogin = onNavigateToLogin;

        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(20);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(860, 560));

        // LEFT: Info Banner
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, ModernColors.PRIMARY_HOVER,
                        getWidth(), getHeight(), ModernColors.ACCENT_CYAN
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.fillRect(getWidth() - 20, 0, 20, getHeight());

                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-50, getHeight() - 150, 220, 220);
                g2.fillOval(getWidth() - 80, -40, 160, 160);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setPreferredSize(new Dimension(340, 560));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(40, 32, 40, 32));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel lblIcon = new JLabel("🎓✨");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));

        JLabel lblTitle = new JLabel("<html>Novo Aluno<br>Cadastre-se</html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html>Crie sua conta para participar do sistema de caronas. Ao se cadastrar, seu perfil padrão é <b>ALUNO</b> e um token JWT é emitido automaticamente (HTTP 201 Created).</html>");
        lblDesc.setFont(AppTheme.FONT_BODY);
        lblDesc.setForeground(new Color(235, 245, 255));
        lblDesc.setBorder(new EmptyBorder(14, 0, 14, 0));

        JPanel validationRules = new JPanel(new GridLayout(5, 1, 0, 8));
        validationRules.setOpaque(false);
        validationRules.add(createRuleItem("• Nome: mín. 3 caracteres"));
        validationRules.add(createRuleItem("• E-mail institucional único"));
        validationRules.add(createRuleItem("• Senha: mín. 6 caracteres (bcrypt)"));
        validationRules.add(createRuleItem("• Telefone numérico com DDD"));
        validationRules.add(createRuleItem("• Instituição de ensino vinculada"));

        heroContent.add(lblIcon);
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(lblTitle);
        heroContent.add(Box.createVerticalStrut(8));
        heroContent.add(lblDesc);
        heroContent.add(Box.createVerticalStrut(10));
        heroContent.add(validationRules);

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // RIGHT: Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel lblFormTitle = new JLabel("Cadastro de Estudante");
        lblFormTitle.setFont(AppTheme.FONT_TITLE);
        lblFormTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblFormSub = new JLabel("Preencha os campos obrigatórios da especificação (DTO)");
        lblFormSub.setFont(AppTheme.FONT_SMALL);
        lblFormSub.setForeground(AppTheme.getTextSecondary());

        // Fields
        txtNome = new ModernTextField("Carlos Eduardo");
        txtEmail = new ModernTextField("carlos.edu@gmail.com");
        txtSenha = new ModernPasswordField("senhaSegura123");
        txtTelefone = new ModernTextField("62999998888");
        txtCurso = new ModernTextField("Engenharia de Software");

        List<Instituicao> instList = apiService.getInstituicoes();
        cbInstituicao = new JComboBox<>(instList.toArray(new Instituicao[0]));
        cbInstituicao.setFont(AppTheme.FONT_BODY);

        // Grid 2 cols for compact beautiful look
        JPanel fieldsGrid = new JPanel(new GridLayout(3, 2, 14, 10));
        fieldsGrid.setOpaque(false);

        fieldsGrid.add(createLabeledField("NOME COMPLETO *", txtNome));
        fieldsGrid.add(createLabeledField("E-MAIL *", txtEmail));
        fieldsGrid.add(createLabeledField("SENHA (MÍN. 6 DÍGITOS) *", txtSenha));
        fieldsGrid.add(createLabeledField("TELEFONE COM DDD *", txtTelefone));
        fieldsGrid.add(createLabeledField("CURSO *", txtCurso));
        fieldsGrid.add(createLabeledField("INSTITUIÇÃO *", cbInstituicao));

        RoundedButton btnCadastrar = new RoundedButton("Finalizar Cadastro (POST /auth/cadastrar)", RoundedButton.ButtonStyle.SUCCESS);
        btnCadastrar.setPreferredSize(new Dimension(0, 42));
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnCadastrar.addActionListener(e -> executeCadastro());

        // Fill Sample Data Button
        RoundedButton btnFillSample = new RoundedButton("⚡ Preencher Dados Exemplo", RoundedButton.ButtonStyle.GHOST);
        btnFillSample.setFont(AppTheme.FONT_SMALL);
        btnFillSample.addActionListener(e -> {
            txtNome.setText("Carlos Eduardo " + (int)(Math.random() * 900 + 100));
            txtEmail.setText("carlos." + (int)(Math.random() * 900 + 100) + "@gmail.com");
            txtSenha.setText("senhaSegura123");
            txtTelefone.setText("62999998888");
            txtCurso.setText("Engenharia de Software");
        });

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL_BOLD);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Footer Link
        JPanel footerLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerLink.setOpaque(false);
        JLabel lblAlready = new JLabel("Já possui uma conta cadastrada?");
        lblAlready.setFont(AppTheme.FONT_BODY);
        lblAlready.setForeground(AppTheme.getTextSecondary());

        RoundedButton btnLogin = new RoundedButton("Fazer Login", RoundedButton.ButtonStyle.GHOST);
        btnLogin.setFont(AppTheme.FONT_BODY_BOLD);
        btnLogin.setForeground(ModernColors.PRIMARY);
        btnLogin.addActionListener(e -> onNavigateToLogin.run());

        footerLink.add(lblAlready);
        footerLink.add(btnLogin);

        formPanel.add(lblFormTitle);
        formPanel.add(Box.createVerticalStrut(2));
        formPanel.add(lblFormSub);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(fieldsGrid);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(btnCadastrar);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(btnFillSample);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(lblStatus);
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(footerLink);

        card.add(heroPanel, BorderLayout.WEST);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);
    }

    private JPanel createRuleItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private void executeCadastro() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        String telefone = txtTelefone.getText().trim();
        String curso = txtCurso.getText().trim();
        Instituicao inst = (Instituicao) cbInstituicao.getSelectedItem();
        String instId = inst != null ? inst.getId() : null;

        ApiResponse<AuthResponse> resp = apiService.cadastrar(nome, email, senha, telefone, curso, instId);
        if (resp.isSuccess()) {
            lblStatus.setText("Estudante cadastrado com sucesso! (201 Created)");
            lblStatus.setForeground(ModernColors.SUCCESS);
            ToastNotification.show(this, "Cadastro Concluído (201 Created)", "Bem-vindo ao sistema, " + nome + "!", ToastNotification.ToastType.SUCCESS);
            onCadastroSuccess.run();
        } else {
            lblStatus.setText("Erro: " + resp.getError().getMensagemFormatada());
            lblStatus.setForeground(ModernColors.DANGER);
            ToastNotification.show(this, "Erro no Cadastro (" + resp.getStatusCode() + " " + resp.getError().getErro() + ")",
                    resp.getError().getMensagemFormatada(), ToastNotification.ToastType.ERROR);
        }
    }
}
