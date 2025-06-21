package views;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import controllers.TaskController;
import models.Task;
import models.Category;
import services.AuthService;
import services.CategoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final TaskController taskController;
    private final AuthService authService;
    private final CategoryService categoryService;
    private final JTable taskTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> categoryFilter;
    private JComboBox<Task.Priority> priorityFilter;
    private String selectedTaskId;

    public MainFrame(AuthService authService) {
        this.taskController = new TaskController();
        this.authService = authService;
        this.categoryService = new CategoryService();
        
        // Configurar tema FlatLaf
        FlatOneDarkIJTheme.setup();
        
        // Configurar janela
        setTitle("TryTask - Bem-vindo, " + authService.getCurrentUserName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Criar modelo da tabela
        String[] columns = {"Título", "Descrição", "Status", "Data", "Prioridade", "Categoria"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Criar tabela
        taskTable = new JTable(tableModel);
        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 4) { // Coluna da Prioridade
                    Object priorityObj = table.getValueAt(row, column);
                    if (priorityObj != null) {
                        String priority = priorityObj.toString();
                        switch (priority) {
                            case "ALTA":
                                setForeground(Color.RED);
                                break;
                            case "MEDIA":
                                setForeground(Color.ORANGE);
                                break;
                            case "BAIXA":
                                setForeground(Color.BLUE);
                                break;
                            default:
                                setForeground(table.getForeground());
                        }
                    }
                } else {
                    setForeground(table.getForeground());
                }
                
                if (!isSelected) {
                    String status = table.getValueAt(row, 2).toString();
                    if (status.equalsIgnoreCase("Concluída")) {
                        c.setBackground(new Color(144, 238, 144)); // Verde claro
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(taskTable);
        
        // Adicionar listener de seleção
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow >= 0) {
                    List<Task> tasks = taskController.getAllTasks();
                    selectedTaskId = tasks.get(selectedRow).getId();
                }
            }
        });
        
        // Criar painel superior com título e nome do usuário
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("TryTask", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel userLabel = new JLabel("Usuário: " + authService.getCurrentUserName(), SwingConstants.RIGHT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        // Criar painel de filtros
        JPanel filterPanel = new JPanel();
        statusFilter = new JComboBox<>(new String[]{"Todos", "Pendente", "Concluída"});
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("Todas");
        Task.Priority[] priorityValues = new Task.Priority[Task.Priority.values().length + 1];
        priorityValues[0] = null;
        System.arraycopy(Task.Priority.values(), 0, priorityValues, 1, Task.Priority.values().length);
        priorityFilter = new JComboBox<>(priorityValues);
        priorityFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "Todas";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Categoria:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Prioridade:"));
        filterPanel.add(priorityFilter);
        
        JButton applyFilterButton = new JButton("Aplicar Filtros");
        filterPanel.add(applyFilterButton);
        
        // Adicionar ação ao botão de filtro
        applyFilterButton.addActionListener(e -> applyFilters());
        
        // Criar painel de botões
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Nova Tarefa");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        JButton categoryButton = new JButton("Gerenciar Categorias");
        JButton logoutButton = new JButton("Sair");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(categoryButton);
        buttonPanel.add(logoutButton);
        
        // Adicionar ações aos botões
        addButton.addActionListener(e -> showAddTaskDialog());
        editButton.addActionListener(e -> showEditTaskDialog());
        deleteButton.addActionListener(e -> deleteSelectedTask());
        categoryButton.addActionListener(e -> showCategoryDialog());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        
        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        
        // Adicionar painel de filtros entre o header e a tabela
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Carregar tarefas e atualizar categorias
        refreshTaskList();
        updateCategoryFilter();
    }
    
    private void showCategoryDialog() {
        JDialog dialog = new JDialog(this, "Gerenciar Categorias", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Lista de categorias
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> categoryList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(categoryList);
        
        // Carregar categorias do usuário
        List<Category> userCategories = categoryService.getUserCategories(authService.getCurrentUserName());
        for (Category category : userCategories) {
            listModel.addElement(category.getName());
        }
        
        // Painel de botões
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Adicionar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Ações dos botões
        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(dialog, "Nome da categoria:");
            if (name != null && !name.trim().isEmpty()) {
                categoryService.createCategory(name, authService.getCurrentUserName());
                listModel.addElement(name);
                updateCategoryFilter();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = categoryList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String oldName = listModel.getElementAt(selectedIndex);
                String newName = JOptionPane.showInputDialog(dialog, "Novo nome da categoria:", oldName);
                if (newName != null && !newName.trim().isEmpty()) {
                    Category category = userCategories.get(selectedIndex);
                    category.setName(newName);
                    categoryService.updateCategory(category);
                    listModel.setElementAt(newName, selectedIndex);
                    updateCategoryFilter();
                }
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedIndex = categoryList.getSelectedIndex();
            if (selectedIndex >= 0) {
                int option = JOptionPane.showConfirmDialog(dialog,
                    "Tem certeza que deseja excluir esta categoria?",
                    "Confirmar exclusão",
                    JOptionPane.YES_NO_OPTION);
                    
                if (option == JOptionPane.YES_OPTION) {
                    Category category = userCategories.get(selectedIndex);
                    categoryService.deleteCategory(category.getId());
                    listModel.remove(selectedIndex);
                    updateCategoryFilter();
                }
            }
        });
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void refreshTaskList() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskController.getAllTasks();
        for (Task task : tasks) {
            Object[] row = {
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getPriority(),
                task.getCategory()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCategoryFilter() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        categoryFilter.removeAllItems();
        categoryFilter.addItem("Todas");
        
        List<Category> categories = categoryService.getUserCategories(authService.getCurrentUserName());
        for (Category category : categories) {
            categoryFilter.addItem(category.getName());
        }
        
        if (selectedCategory != null) {
            categoryFilter.setSelectedItem(selectedCategory);
        }
    }
    
    private void applyFilters() {
        String status = statusFilter.getSelectedItem().equals("Todos") ? null : (String) statusFilter.getSelectedItem();
        String category = categoryFilter.getSelectedItem().equals("Todas") ? null : (String) categoryFilter.getSelectedItem();
        Task.Priority priority = (Task.Priority) priorityFilter.getSelectedItem();
        
        tableModel.setRowCount(0);
        List<Task> filteredTasks = taskController.getFilteredTasks(status, category, priority);
        
        for (Task task : filteredTasks) {
            Object[] row = {
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getPriority(),
                task.getCategory()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddTaskDialog() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Pendente"});
        JTextField dateField = new JTextField();
        JComboBox<Task.Priority> priorityField = new JComboBox<>(Task.Priority.values());
        
        // Usar ComboBox para categorias existentes
        JComboBox<String> categoryField = new JComboBox<>();
        List<Category> categories = categoryService.getUserCategories(authService.getCurrentUserName());
        for (Category category : categories) {
            categoryField.addItem(category.getName());
        }
        
        Object[] message = {
            "Título:", titleField,
            "Descrição:", descField,
            "Status:", statusField,
            "Data:", dateField,
            "Prioridade:", priorityField,
            "Categoria:", categoryField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Nova Tarefa",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (option == JOptionPane.OK_OPTION) {
            taskController.createTask(
                titleField.getText(),
                descField.getText(),
                (String) statusField.getSelectedItem(),
                dateField.getText(),
                (Task.Priority) priorityField.getSelectedItem(),
                (String) categoryField.getSelectedItem()
            );
            refreshTaskList();
            updateCategoryFilter();
        }
    }
    
    private void showEditTaskDialog() {
        if (selectedTaskId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar!");
            return;
        }
        
        int selectedRow = taskTable.getSelectedRow();
        String currentTitle = (String) taskTable.getValueAt(selectedRow, 0);
        String currentDesc = (String) taskTable.getValueAt(selectedRow, 1);
        String currentStatus = (String) taskTable.getValueAt(selectedRow, 2);
        String currentDate = (String) taskTable.getValueAt(selectedRow, 3);
        Task.Priority currentPriority = (Task.Priority) taskTable.getValueAt(selectedRow, 4);
        String currentCategory = (String) taskTable.getValueAt(selectedRow, 5);
        
        JTextField titleField = new JTextField(currentTitle);
        JTextField descField = new JTextField(currentDesc);
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Pendente", "Concluída"});
        statusField.setSelectedItem(currentStatus);
        JTextField dateField = new JTextField(currentDate);
        JComboBox<Task.Priority> priorityField = new JComboBox<>(Task.Priority.values());
        priorityField.setSelectedItem(currentPriority);
        
        // Usar ComboBox para categorias existentes
        JComboBox<String> categoryField = new JComboBox<>();
        List<Category> categories = categoryService.getUserCategories(authService.getCurrentUserName());
        for (Category category : categories) {
            categoryField.addItem(category.getName());
        }
        categoryField.setSelectedItem(currentCategory);
        
        Object[] message = {
            "Título:", titleField,
            "Descrição:", descField,
            "Status:", statusField,
            "Data:", dateField,
            "Prioridade:", priorityField,
            "Categoria:", categoryField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Tarefa",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (option == JOptionPane.OK_OPTION) {
            Task updatedTask = new Task(
                selectedTaskId,
                titleField.getText(),
                descField.getText(),
                (String) statusField.getSelectedItem(),
                dateField.getText(),
                (Task.Priority) priorityField.getSelectedItem(),
                (String) categoryField.getSelectedItem()
            );
            taskController.updateTask(updatedTask);
            refreshTaskList();
            updateCategoryFilter();
        }
    }
    
    private void deleteSelectedTask() {
        if (selectedTaskId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir!");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir esta tarefa?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            taskController.deleteTask(selectedTaskId);
            selectedTaskId = null;
            refreshTaskList();
            updateCategoryFilter();
        }
    }
} 