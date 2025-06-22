import views.LoginFrame;
import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    public static void main(String[] args) {
        try {
            // Set FlatLaf Dark theme
            FlatDarkLaf.setup();
            
            // Configure some global UI properties for dark theme
            UIManager.put("Component.focusWidth", 0);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.focusColor", new java.awt.Color(30, 30, 30));
            UIManager.put("TextField.background", new java.awt.Color(30, 30, 30));
            UIManager.put("TextField.foreground", java.awt.Color.WHITE);
            UIManager.put("TextField.caretColor", java.awt.Color.WHITE);
            UIManager.put("ComboBox.background", new java.awt.Color(30, 30, 30));
            UIManager.put("ComboBox.foreground", java.awt.Color.WHITE);
            UIManager.put("ComboBox.selectionBackground", new java.awt.Color(60, 60, 60));
            UIManager.put("ComboBox.selectionForeground", java.awt.Color.WHITE);
            
            // Iniciar a aplicação com a tela de login
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Erro ao iniciar o sistema: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 