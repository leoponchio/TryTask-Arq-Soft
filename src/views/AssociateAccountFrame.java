package views;

import javax.swing.*;
import java.awt.*;
import models.UserType;
import services.AuthService;

public class AssociateAccountFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color BUTTON_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color FIELD_BACKGROUND = new Color(0, 0, 0);
    private static final Color SELECTION_COLOR = new Color(70, 70, 70);
    private static final int FIELD_HEIGHT = 25;

    private JTextField emailField;
    private JPasswordField senhaField;
    private JComboBox<UserType> tipoUsuarioCombo;
    private AuthService authService;

    public AssociateAccountFrame() {
        authService = AuthService.getInstance();
        setupUI();
    }

    private void setupUI() {
        setTitle("Associar Conta - TryTask");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Associar Nova Versão", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Campo de email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setBackground(FIELD_BACKGROUND);
        emailField.setForeground(TEXT_COLOR);
        emailField.setCaretColor(TEXT_COLOR);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(emailField, gbc);

        // Campo de senha
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(senhaLabel, gbc);

        senhaField = new JPasswordField(20);
        senhaField.setBackground(FIELD_BACKGROUND);
        senhaField.setForeground(TEXT_COLOR);
        senhaField.setCaretColor(TEXT_COLOR);
        senhaField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(senhaField, gbc);

        // ComboBox para novo tipo de acesso
        JLabel tipoLabel = new JLabel("Nova Versão:");
        tipoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(tipoLabel, gbc);

        tipoUsuarioCombo = new JComboBox<>(UserType.values());
        tipoUsuarioCombo.setBackground(FIELD_BACKGROUND);
        tipoUsuarioCombo.setForeground(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(tipoUsuarioCombo, gbc);

        // Botão de associar
        JButton associarButton = new JButton("Associar");
        styleButton(associarButton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(associarButton, gbc);

        // Botão de cancelar
        JButton cancelarButton = new JButton("Cancelar");
        styleButton(cancelarButton);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(cancelarButton, gbc);

        add(mainPanel);

        // Ação do botão de associar
        associarButton.addActionListener(e -> {
            String email = emailField.getText();
            String senha = new String(senhaField.getPassword());
            UserType novoTipo = (UserType) tipoUsuarioCombo.getSelectedItem();

            if (!authService.isEmailRegistered(email)) {
                JOptionPane.showMessageDialog(this, 
                    "Email não encontrado. Por favor, cadastre-se primeiro.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.hasUserType(email, novoTipo)) {
                JOptionPane.showMessageDialog(this, 
                    "Esta conta já possui acesso a esta versão.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authService.associateUserType(email, senha, novoTipo)) {
                JOptionPane.showMessageDialog(this, 
                    "Nova versão associada com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Senha incorreta.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ação do botão de cancelar
        cancelarButton.addActionListener(e -> this.dispose());
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(null);
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SELECTION_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }
} 