import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleTask {
    private static List<String> tasks = new ArrayList<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    
    public static void main(String[] args) {
        // Criar a janela principal
        JFrame frame = new JFrame("Gerenciador de Tarefas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        // Criar a lista de tarefas
        JList<String> taskList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(taskList);
        
        // Criar o campo de texto e botão
        JTextField taskField = new JTextField(20);
        JButton addButton = new JButton("Adicionar");
        JButton removeButton = new JButton("Remover");
        
        // Painel para os controles
        JPanel controlPanel = new JPanel();
        controlPanel.add(taskField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        
        // Adicionar componentes à janela
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        // Adicionar ações aos botões
        addButton.addActionListener(e -> {
            String task = taskField.getText();
            if (!task.isEmpty()) {
                tasks.add(task);
                listModel.addElement(task);
                taskField.setText("");
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                tasks.remove(selectedIndex);
                listModel.remove(selectedIndex);
            }
        });
        
        // Exibir a janela
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 