package main;

import views.LoginFrame;
import javax.swing.SwingUtilities;

public class TryTasks {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
} 