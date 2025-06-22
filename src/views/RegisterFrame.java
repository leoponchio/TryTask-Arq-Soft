package views;

import controllers.TaskController;
import models.UserType;
import services.AuthService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color BUTTON_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color INPUT_BACKGROUND = new Color(0, 0, 0);
    private static final Color DROPDOWN_BACKGROUND = new Color(60, 60, 60);
    private static final Color SELECTED_BACKGROUND = new Color(100, 100, 100);
    private static final Color BORDER_COLOR = new Color(100, 100, 100);
    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 30;
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JComboBox<UserType> tipoUsuarioCombo;
    private AuthService authService;

    public RegisterFrame() {
        authService = AuthService.getInstance();
        setupUI();
    }

    private void setupUI() {
        setTitle("Cadastro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        nameField.setBackground(INPUT_BACKGROUND);
        nameField.setForeground(TEXT_COLOR);
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        nameField.setCaretColor(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(nameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        emailField.setBackground(INPUT_BACKGROUND);
        emailField.setForeground(TEXT_COLOR);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        emailField.setCaretColor(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(emailField, gbc);

        // Senha
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(senhaLabel, gbc);

        senhaField = new JPasswordField();
        senhaField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        senhaField.setBackground(INPUT_BACKGROUND);
        senhaField.setForeground(TEXT_COLOR);
        senhaField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        senhaField.setCaretColor(TEXT_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(senhaField, gbc);

        // Tipo de Usuário
        JLabel tipoLabel = new JLabel("Tipo de Usuário:");
        tipoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(tipoLabel, gbc);

        tipoUsuarioCombo = new JComboBox<>(UserType.values());
        styleComboBox(tipoUsuarioCombo);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(tipoUsuarioCombo, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton cadastrarButton = new JButton("Cadastrar");
        styleButton(cadastrarButton);
        cadastrarButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String senha = new String(senhaField.getPassword());
            UserType tipoSelecionado = (UserType) tipoUsuarioCombo.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.register(name, email, senha, tipoSelecionado)) {
                JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!");
                new LoginFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email já cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton voltarButton = new JButton("Voltar");
        styleButton(voltarButton);
        voltarButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        buttonPanel.add(cadastrarButton);
        buttonPanel.add(voltarButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 30));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        // Configura o ComboBox
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        comboBox.setPreferredSize(new Dimension(120, FIELD_HEIGHT));
        ((JComponent) comboBox.getRenderer()).setOpaque(true);

        // Remove a borda de foco
        comboBox.setFocusable(true);
        UIManager.put("ComboBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[0]));
        UIManager.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[0]));
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("Component.focusColor", new Color(60, 60, 60));
        
        // Renderizador personalizado para a lista suspensa
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index == -1) {
                    // Item selecionado (mostrado no ComboBox quando fechado)
                    c.setBackground(INPUT_BACKGROUND);
                    ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
                } else {
                    // Itens na lista suspensa
                    c.setBackground(isSelected ? SELECTED_BACKGROUND : DROPDOWN_BACKGROUND);
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                }
                c.setForeground(TEXT_COLOR);
                return c;
            }
        };
        comboBox.setRenderer(renderer);

        // Configura o popup da lista suspensa
        Object popupComponent = comboBox.getUI().getAccessibleChild(comboBox, 0);
        if (popupComponent instanceof JPopupMenu) {
            JPopupMenu popup = (JPopupMenu) popupComponent;
            popup.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
            popup.setBackground(DROPDOWN_BACKGROUND);
        }

        // Customiza a UI do ComboBox
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setBackground(INPUT_BACKGROUND);
                button.setForeground(TEXT_COLOR);
                button.setBorder(null);
                button.setFocusPainted(false);
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(INPUT_BACKGROUND);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected void installDefaults() {
                super.installDefaults();
                LookAndFeel.installProperty(comboBox, "opaque", Boolean.FALSE);
            }
        });

        // Configura o comportamento do foco
        comboBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            }

            @Override
            public void focusLost(FocusEvent e) {
                comboBox.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            }
        });
    }
} 