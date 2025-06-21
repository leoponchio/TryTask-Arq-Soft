import views.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            // Configurar o look and feel
            com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme.setup();
            
            // Iniciar a aplicação
            new LoginFrame().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 