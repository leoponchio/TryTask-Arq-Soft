package views;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import services.AuthService;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final JPanel loginPanel;
    private final JPanel registerPanel;
    
    // Campos do login
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Campos do registro
    private JTextField regUsernameField;
    private JTextField regNameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;

    public LoginFrame() {
        this.authService = new AuthService();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        // Configurar tema FlatLaf
        FlatOneDarkIJTheme.setup();
        
        // Configurar janela
        setTitle("TryTask - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        // Criar painéis
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        
        // Adicionar painéis ao card layout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        
        // Iniciar com o painel de login
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Logo/Título
        JLabel titleLabel = new JLabel("TryTask");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Campos de login
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Usuário:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Entrar");
        JButton goToRegisterButton = new JButton("Cadastrar-se");
        
        loginButton.addActionListener(e -> handleLogin());
        goToRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(goToRegisterButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Logo/Título
        JLabel titleLabel = new JLabel("TryTask - Cadastro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Campos de registro
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Usuário:"), gbc);
        
        gbc.gridx = 1;
        regUsernameField = new JTextField(20);
        panel.add(regUsernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        regNameField = new JTextField(20);
        panel.add(regNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        regEmailField = new JTextField(20);
        panel.add(regEmailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        regPasswordField = new JPasswordField(20);
        panel.add(regPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Confirmar Senha:"), gbc);
        
        gbc.gridx = 1;
        regConfirmPasswordField = new JPasswordField(20);
        panel.add(regConfirmPasswordField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Cadastrar");
        JButton backButton = new JButton("Voltar");
        
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (authService.authenticate(username, password)) {
            JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
            dispose();
            new MainFrame(authService).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuário ou senha incorretos!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegister() {
        String username = regUsernameField.getText();
        String name = regNameField.getText();
        String email = regEmailField.getText();
        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(regConfirmPasswordField.getPassword());
        
        // Validações
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem!");
            return;
        }
        
        if (!authService.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email inválido!");
            return;
        }
        
        if (!authService.isUsernameAvailable(username)) {
            JOptionPane.showMessageDialog(this, "Nome de usuário já existe!");
            return;
        }
        
        // Registrar usuário
        if (authService.registerUser(username, name, email, password)) {
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!");
            cardLayout.show(mainPanel, "login");
            usernameField.setText(username);
            passwordField.setText(password);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário!");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
} 