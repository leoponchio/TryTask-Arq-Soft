package views;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.*;
import java.awt.*;
import models.*;
import models.tasks.*;
import services.*;
import controllers.TaskController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Priority;
import services.TaskService;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.LookAndFeel;

public class MainFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color INPUT_BACKGROUND = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(60, 60, 60);
    private static final Color BORDER_COLOR = new Color(100, 100, 100);
    private static final Color SELECTED_BACKGROUND = new Color(80, 80, 80);
    private static final Color DROPDOWN_BACKGROUND = new Color(50, 50, 50);
    private static final int FIELD_HEIGHT = 25;
    private static final int BUTTON_HEIGHT = 25;
    
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private TaskController taskController;
    private JComboBox<String> statusFilter;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> priorityFilter;
    private CategoryService categoryService;
    private User currentUser;
    private UserType userType;
    private String userName;
    private List<String> taskIds = new ArrayList<>();

    public MainFrame(User user, UserType userType) {
        this.taskController = new TaskController();
        this.taskIds = new ArrayList<>();
        this.currentUser = user;
        this.userType = userType;
        this.userName = user.getName();
        this.categoryService = CategoryService.getInstance();
        
        // Configura o usuário atual nos serviços
        TaskService.getInstance().setCurrentUser(user, userType);
        this.categoryService.setCurrentUser(user, userType);
        
        // Inicializa os filtros antes de setupUI
        this.statusFilter = createStatusFilter();
        this.categoryFilter = createCategoryFilter();
        this.priorityFilter = createPriorityFilter();
        
        setupUI();
        loadTasks();
    }

    private void setupUI() {
        setTitle("Gerenciador de Tarefas - " + userType);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(BACKGROUND_COLOR);

        // Painel de usuário
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(BACKGROUND_COLOR);
        JLabel userLabel = new JLabel("Usuário: " + userName);
        userLabel.setForeground(TEXT_COLOR);
        userPanel.add(userLabel);
        topPanel.add(userPanel, BorderLayout.NORTH);

        // Painel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);

        // Filtros em linha
        addFilterComponent(filterPanel, "Status:", statusFilter);
        addFilterComponent(filterPanel, "Categoria:", categoryFilter);
        addFilterComponent(filterPanel, "Prioridade:", priorityFilter);

        JButton applyFilters = new JButton("Filtrar");
        styleButton(applyFilters, 80, BUTTON_HEIGHT);
        filterPanel.add(applyFilters);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Painel de ações principais
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);

        JButton addButton = new JButton("Nova Tarefa");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        JButton manageCategoriesButton = new JButton("Categorias");
        JButton logoutButton = new JButton("Sair");

        styleButton(addButton, 100, BUTTON_HEIGHT);
        styleButton(editButton, 80, BUTTON_HEIGHT);
        styleButton(deleteButton, 80, BUTTON_HEIGHT);
        styleButton(manageCategoriesButton, 90, BUTTON_HEIGHT);
        styleButton(logoutButton, 70, BUTTON_HEIGHT);

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(manageCategoriesButton);
        actionPanel.add(logoutButton);

        topPanel.add(actionPanel, BorderLayout.SOUTH);

        // Tabela de tarefas
        setupTable();

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Adiciona os painéis ao painel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Adiciona o painel principal ao frame
        add(mainPanel);

        // Adiciona os listeners aos botões
        addButton.addActionListener(e -> showCreateTaskDialog());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());
        manageCategoriesButton.addActionListener(e -> showCategoryDialog());
        logoutButton.addActionListener(e -> logout());
        applyFilters.addActionListener(e -> loadTasks());

        setupListeners();
    }

    private void setupTable() {
        String[] columnNames = {
            "Título",
            "Descrição",
            "Status",
            "Data de Entrega",
            "Prioridade",
            "Categoria",
            "Detalhes"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Apenas a coluna de status é editável
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);
        taskTable.setShowGrid(true);
        taskTable.setGridColor(new Color(60, 60, 60));
        taskTable.setBackground(new Color(30, 30, 30));
        taskTable.setForeground(Color.WHITE);
        taskTable.setSelectionBackground(new Color(60, 60, 60));
        taskTable.setSelectionForeground(Color.WHITE);
        
        // Estilo no cabeçalho da tabela
        JTableHeader header = taskTable.getTableHeader();
        header.setBackground(new Color(40, 40, 40));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        // Renderizador escuro para todas as células
        DefaultTableCellRenderer darkRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 2 && value instanceof TaskStatus) {
                    TaskStatus status = (TaskStatus) value;
                    switch (status) {
                        case CONCLUIDO:
                            setBackground(isSelected ? new Color(60, 60, 60) : new Color(40, 60, 40));
                            break;
                        case EM_ANDAMENTO:
                            setBackground(isSelected ? new Color(60, 60, 60) : new Color(60, 60, 40));
                            break;
                        case PENDENTE:
                            setBackground(isSelected ? new Color(60, 60, 60) : new Color(30, 30, 30));
                            break;
                    }
                } else {
                    setBackground(isSelected ? new Color(60, 60, 60) : new Color(30, 30, 30));
                }
                
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
                return c;
            }
        };

        // Aplicar o renderizador escuro para todas as colunas
        for (int i = 0; i < taskTable.getColumnCount(); i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(darkRenderer);
        }
        
        // Editor escuro para a coluna de status (ComboBox)
        JComboBox<TaskStatus> statusComboBox = new JComboBox<>(TaskStatus.values());
        statusComboBox.setBackground(new Color(30, 30, 30));
        statusComboBox.setForeground(Color.WHITE);
        statusComboBox.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        // Customizar o popup do ComboBox
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(60, 60, 60) : new Color(30, 30, 30));
                setForeground(Color.WHITE);
                return this;
            }
        });
        
        DefaultCellEditor statusEditor = new DefaultCellEditor(statusComboBox) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                Component editor = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                if (editor instanceof JComboBox) {
                    JComboBox<?> combo = (JComboBox<?>) editor;
                    combo.setBackground(new Color(30, 30, 30));
                    combo.setForeground(Color.WHITE);
                    combo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
                }
                return editor;
            }
        };
        
        taskTable.getColumnModel().getColumn(2).setCellEditor(statusEditor);
        
        // Adiciona listener para mudanças no status
        taskTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Coluna do status
                int row = e.getFirstRow();
                Object value = taskTable.getValueAt(row, 2);
                if (value instanceof TaskStatus) {
                    TaskStatus newStatus = (TaskStatus) value;
                    String taskId = taskIds.get(row);
                    taskController.updateTaskStatus(taskId, newStatus);
                }
            }
        });
    }

    private void addFilterComponent(JPanel panel, String label, JComponent component) {
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(TEXT_COLOR);
        panel.add(jLabel);
        panel.add(component);
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(30, 30, 30));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, FIELD_HEIGHT));
        
        // Customizar o popup do ComboBox
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(60, 60, 60) : new Color(30, 30, 30));
                setForeground(Color.WHITE);
                return this;
            }
        });
        
        // Customizar o editor do ComboBox
        if (comboBox.getEditor() != null && comboBox.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
            editor.setBackground(new Color(30, 30, 30));
            editor.setForeground(Color.WHITE);
            editor.setCaretColor(Color.WHITE);
            editor.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        }
    }

    private JComboBox<String> createStatusFilter() {
        JComboBox<String> filter = new JComboBox<>();
        filter.addItem("Todos");
        for (TaskStatus status : TaskStatus.values()) {
            filter.addItem(status.toString());
        }
        filter.setSelectedIndex(0);
        styleComboBox(filter);
        return filter;
    }

    private JComboBox<String> createPriorityFilter() {
        JComboBox<String> filter = new JComboBox<>();
        filter.addItem("Todos");
        for (Priority priority : Priority.values()) {
            filter.addItem(priority.toString());
        }
        filter.setSelectedIndex(0);
        styleComboBox(filter);
        return filter;
    }

    private JComboBox<String> createCategoryFilter() {
        JComboBox<String> filter = new JComboBox<>();
        filter.addItem("Todas");
        List<Category> userCategories = categoryService.getUserCategories(currentUser);
        for (Category category : userCategories) {
            filter.addItem(category.getName());
        }
        filter.setSelectedIndex(0);
        styleComboBox(filter);
        return filter;
    }

    private void styleButton(JButton button, int width, int height) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(width, height));
        
        // Adiciona efeito hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SELECTED_BACKGROUND);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }

    private void styleButton(JButton button) {
        styleButton(button, 100, BUTTON_HEIGHT);
    }

    private void styleTable() {
        // Configura cores globais para o cabeçalho da tabela
        UIManager.put("TableHeader.background", BACKGROUND_COLOR);
        UIManager.put("TableHeader.foreground", TEXT_COLOR);
        UIManager.put("Table.gridColor", new Color(60, 60, 60));
        
        taskTable.setBackground(BACKGROUND_COLOR);
        taskTable.setForeground(TEXT_COLOR);
        taskTable.setSelectionBackground(SELECTED_BACKGROUND);
        taskTable.setSelectionForeground(TEXT_COLOR);
        taskTable.setGridColor(new Color(60, 60, 60));
        taskTable.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        // Estilo do cabeçalho
        JTableHeader header = taskTable.getTableHeader();
        header.setBackground(BACKGROUND_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        header.setReorderingAllowed(false); // Impede reorganização de colunas
        
        // Customiza o renderizador do cabeçalho
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(BACKGROUND_COLOR);
                label.setForeground(TEXT_COLOR);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                return label;
            }
        });
        
        // Estilo das células
        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(SELECTED_BACKGROUND);
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(BACKGROUND_COLOR);
                    c.setForeground(TEXT_COLOR);
                }
                
                // Adiciona padding nas células
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    ));
                }
                
                return c;
            }
        });
        
        // Ajusta a altura das linhas
        taskTable.setRowHeight(30);
        
        // Remove a grade entre as células
        taskTable.setShowGrid(false);
        
        // Configura o ScrollPane
        JScrollPane scrollPane = (JScrollPane) taskTable.getParent().getParent();
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        // Configura as cores das barras de rolagem
        UIManager.put("ScrollBar.track", BACKGROUND_COLOR);
        UIManager.put("ScrollBar.thumb", BUTTON_COLOR);
        UIManager.put("ScrollBar.thumbDarkShadow", BUTTON_COLOR);
        UIManager.put("ScrollBar.thumbHighlight", BUTTON_COLOR);
        UIManager.put("ScrollBar.thumbShadow", BUTTON_COLOR);
        
        // Aplica as configurações nas barras de rolagem
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);
        scrollPane.getHorizontalScrollBar().setBackground(BACKGROUND_COLOR);
    }

    private void loadTasks() {
        tableModel.setRowCount(0);
        taskIds.clear();
        
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String selectedPriority = (String) priorityFilter.getSelectedItem();
        
        List<BaseTask> tasks = taskController.getAllTasks();
        
        // Aplicar filtros apenas se não for "Todos"/"Todas"
        if (!"Todos".equals(selectedStatus)) {
            final String status = selectedStatus;
            tasks = tasks.stream()
                .filter(task -> task.getStatus().toString().equals(status))
                .collect(Collectors.toList());
        }
        
        if (!"Todas".equals(selectedCategory)) {
            tasks = tasks.stream()
                .filter(task -> task.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        }
        
        if (!"Todos".equals(selectedPriority)) {
            final String priority = selectedPriority;
            tasks = tasks.stream()
                .filter(task -> task.getPriority().toString().equals(priority))
                .collect(Collectors.toList());
        }
        
        // Preencher a tabela
        for (BaseTask task : tasks) {
            addTaskRow(task);
        }
    }

    private String getTaskDetails(BaseTask task) {
        if (task instanceof ProjectTask) {
            ProjectTask pt = (ProjectTask) task;
            return String.format("Fase: %s, Equipe: %s",
                pt.getPhase(),
                String.join(", ", pt.getTeamMembers()));
        } else if (task instanceof StudentTask) {
            StudentTask st = (StudentTask) task;
            return String.format("Disciplina: %s", st.getSubject());
        } else if (task instanceof HomeTask) {
            HomeTask ht = (HomeTask) task;
            return String.format("Local: %s, Responsável: %s",
                ht.getLocation(),
                ht.getAssignedTo());
        }
        return "";
    }

    private void showCreateTaskDialog() {
        JDialog dialog = new JDialog(this, "Nova Tarefa", true);
        dialog.setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos comuns
        JTextField titleField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField dueDateField = new JTextField(10);
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        
        // Carrega as categorias do usuário atual
        List<Category> userCategories = categoryService.getUserCategories(currentUser);
        String[] categoryNames = userCategories.stream()
            .map(Category::getName)
            .toArray(String[]::new);
        JComboBox<String> categoryCombo = new JComboBox<>(categoryNames);
        
        JComboBox<TaskStatus> statusCombo = new JComboBox<>(TaskStatus.values());
        
        // Estiliza os componentes
        styleTextField(titleField);
        styleTextArea(descriptionArea);
        styleTextField(dueDateField);
        styleComboBox(priorityCombo);
        styleComboBox(categoryCombo);
        styleComboBox(statusCombo);
        
        // Adiciona os campos ao painel
        addField(mainPanel, gbc, "Título:", titleField);
        addField(mainPanel, gbc, "Descrição:", new JScrollPane(descriptionArea));
        addField(mainPanel, gbc, "Data de Entrega:", dueDateField);
        addField(mainPanel, gbc, "Prioridade:", priorityCombo);
        addField(mainPanel, gbc, "Categoria:", categoryCombo);
        addField(mainPanel, gbc, "Status:", statusCombo);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");
        styleButton(okButton);
        styleButton(cancelButton);
        
        okButton.addActionListener(e -> {
            String id = java.util.UUID.randomUUID().toString();
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String dueDate = dueDateField.getText();
            Priority priority = (Priority) priorityCombo.getSelectedItem();
            String category = (String) categoryCombo.getSelectedItem();
            TaskStatus status = (TaskStatus) statusCombo.getSelectedItem();
            
            BaseTask task;
            switch (userType) {
                case STUDENT:
                    task = new StudentTask(id, title, description, dueDate, priority, category);
                    break;
                case PROJECT_MANAGER:
                    task = new ProjectTask(id, title, description, dueDate, priority, category);
                    break;
                case HOME_MANAGER:
                    task = new HomeTask(id, title, description, dueDate, priority, category);
                    break;
                default:
                    task = new RotinaTask(id, title, description, dueDate, priority, category);
            }
            
            task.setStatus(status);
            taskController.createTask(task);
            refreshTasks();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Adiciona os painéis ao diálogo
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Configura o diálogo
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Por favor, selecione uma tarefa para editar.");
            return;
        }

        String taskId = taskIds.get(selectedRow);
        BaseTask task = taskController.getAllTasks().stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElse(null);

        if (task == null) {
            showErrorMessage("Tarefa não encontrada.");
            return;
        }

        JDialog dialog = new JDialog(this, "Editar Tarefa", true);
        dialog.setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos comuns
        JTextField titleField = new JTextField(task.getTitle(), 20);
        JTextArea descriptionArea = new JTextArea(task.getDescription(), 3, 20);
        JTextField dueDateField = new JTextField(task.getDueDate(), 10);
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setSelectedItem(task.getPriority());
        JComboBox<String> categoryCombo = new JComboBox<>(categoryService.getAllCategories().toArray(new String[0]));
        categoryCombo.setSelectedItem(task.getCategory());
        JComboBox<TaskStatus> statusCombo = new JComboBox<>(TaskStatus.values());
        statusCombo.setSelectedItem(task.getStatus());
        
        styleTextField(titleField);
        styleTextArea(descriptionArea);
        styleTextField(dueDateField);
        styleComboBox(priorityCombo);
        styleComboBox(categoryCombo);
        styleComboBox(statusCombo);
        
        addField(mainPanel, gbc, "Título:", titleField);
        addField(mainPanel, gbc, "Descrição:", new JScrollPane(descriptionArea));
        addField(mainPanel, gbc, "Data de Entrega:", dueDateField);
        addField(mainPanel, gbc, "Prioridade:", priorityCombo);
        addField(mainPanel, gbc, "Categoria:", categoryCombo);
        addField(mainPanel, gbc, "Status:", statusCombo);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");
        styleButton(saveButton);
        styleButton(cancelButton);
        
        saveButton.addActionListener(e -> {
            task.setTitle(titleField.getText());
            task.setDescription(descriptionArea.getText());
            task.setDueDate(dueDateField.getText());
            task.setPriority((Priority) priorityCombo.getSelectedItem());
            task.setCategory((String) categoryCombo.getSelectedItem());
            task.setStatus((TaskStatus) statusCombo.getSelectedItem());
            
            taskController.updateTask(task);
            refreshTasks();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(INPUT_BACKGROUND);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(TEXT_COLOR);
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setPreferredSize(new Dimension(200, FIELD_HEIGHT));
        return textField;
    }

    private void addDialogComponent(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(component, gbc);
    }

    private boolean showStyledDialog(JPanel panel, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");
        styleButton(okButton);
        styleButton(cancelButton);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Customiza o JOptionPane para usar as cores do tema
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", TEXT_COLOR);

        final boolean[] result = {false};

        okButton.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return result[0];
    }

    private void showErrorMessage(String message) {
        // Customiza o JOptionPane para usar as cores do tema
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", TEXT_COLOR);

        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selecione uma tarefa para excluir.");
            return;
        }
        
        String title = (String) taskTable.getValueAt(selectedRow, 0);
        BaseTask task = taskController.getTaskByTitle(title);
        if (task == null) {
            showErrorMessage("Tarefa não encontrada.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a tarefa '" + title + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            taskController.deleteTask(task.getId());
            loadTasks();
        }
    }

    private void showCategoryDialog() {
        if (currentUser == null) {
            showErrorMessage("Erro: Usuário não está logado.");
            return;
        }

        JDialog dialog = new JDialog(this, "Gerenciar Categorias", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Lista de categorias
        DefaultListModel<String> categoryListModel = new DefaultListModel<>();
        JList<String> categoryList = new JList<>(categoryListModel);
        categoryList.setBackground(INPUT_BACKGROUND);
        categoryList.setForeground(TEXT_COLOR);
        categoryList.setSelectionBackground(SELECTED_BACKGROUND);
        categoryList.setSelectionForeground(TEXT_COLOR);
        categoryList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        categoryList.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(categoryList);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(INPUT_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton addButton = new JButton("Adicionar");
        JButton removeButton = new JButton("Remover");
        styleButton(addButton, 90, BUTTON_HEIGHT);
        styleButton(removeButton, 90, BUTTON_HEIGHT);
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Carrega as categorias
        try {
            List<Category> categories = categoryService.getUserCategories(currentUser);
            for (Category category : categories) {
                categoryListModel.addElement(category.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erro ao carregar categorias: " + e.getMessage());
        }
        
        // Adicionar categoria
        addButton.addActionListener(e -> {
            String newCategory = JOptionPane.showInputDialog(dialog, 
                "Digite o nome da nova categoria:",
                "Nova Categoria",
                JOptionPane.PLAIN_MESSAGE);
                
            if (newCategory != null && !newCategory.trim().isEmpty()) {
                try {
                    categoryService.addCategory(currentUser, newCategory.trim());
                    categoryListModel.clear();
                    List<Category> updatedCategories = categoryService.getUserCategories(currentUser);
                    for (Category category : updatedCategories) {
                        categoryListModel.addElement(category.getName());
                    }
                    updateCategoryFilter();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorMessage("Erro ao adicionar categoria: " + ex.getMessage());
                }
            }
        });
        
        // Remover categoria
        removeButton.addActionListener(e -> {
            String selectedCategory = categoryList.getSelectedValue();
            if (selectedCategory != null) {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Tem certeza que deseja remover a categoria '" + selectedCategory + "'?",
                    "Confirmar Remoção",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        categoryService.removeCategory(currentUser, selectedCategory);
                        categoryListModel.clear();
                        List<Category> updatedCategories = categoryService.getUserCategories(currentUser);
                        for (Category category : updatedCategories) {
                            categoryListModel.addElement(category.getName());
                        }
                        updateCategoryFilter();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorMessage("Erro ao remover categoria: " + ex.getMessage());
                    }
                }
            }
        });
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }

    private void setupListeners() {
        // Adiciona listener para duplo clique na tabela
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showTaskDetails(taskTable.getSelectedRow());
                }
            }
        });
        
        // Adiciona listener para tecla Delete
        taskTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedTask();
                }
            }
        });
    }

    private void showTaskDetails(int selectedRow) {
        if (selectedRow == -1) return;
        
        String title = (String) taskTable.getValueAt(selectedRow, 0);
        BaseTask task = taskController.getTaskByTitle(title);
        
        if (task == null) return;
        
        showTaskDetails(task);
    }

    private void showTaskDetails(BaseTask task) {
        JDialog dialog = new JDialog(this, "Detalhes da Tarefa", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Campos comuns
        addDetailField(mainPanel, gbc, "Título:", task.getTitle());
        addDetailField(mainPanel, gbc, "Descrição:", task.getDescription());
        addDetailField(mainPanel, gbc, "Data de Entrega:", task.getDueDate());
        addDetailField(mainPanel, gbc, "Prioridade:", task.getPriority().toString());
        addDetailField(mainPanel, gbc, "Categoria:", task.getCategory());
        addDetailField(mainPanel, gbc, "Status:", task.getStatus().toString());
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton editButton = new JButton("Editar");
        JButton closeButton = new JButton("Fechar");
        styleButton(editButton);
        styleButton(closeButton);
        
        editButton.addActionListener(e -> {
            dialog.dispose();
            showEditTaskDialog(task);
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditTaskDialog(BaseTask task) {
        JDialog dialog = new JDialog(this, "Editar Tarefa", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos comuns
        JTextField titleField = new JTextField(task.getTitle(), 20);
        JTextArea descriptionArea = new JTextArea(task.getDescription(), 3, 20);
        JTextField dueDateField = new JTextField(task.getDueDate(), 10);
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        
        // Carrega as categorias do usuário atual
        List<Category> userCategories = categoryService.getUserCategories(currentUser);
        String[] categoryNames = userCategories.stream()
            .map(Category::getName)
            .toArray(String[]::new);
        JComboBox<String> categoryCombo = new JComboBox<>(categoryNames);
        
        JComboBox<TaskStatus> statusCombo = new JComboBox<>(TaskStatus.values());
        
        priorityCombo.setSelectedItem(task.getPriority());
        categoryCombo.setSelectedItem(task.getCategory());
        statusCombo.setSelectedItem(task.getStatus());
        
        styleTextField(titleField);
        styleTextArea(descriptionArea);
        styleTextField(dueDateField);
        styleComboBox(priorityCombo);
        styleComboBox(categoryCombo);
        styleComboBox(statusCombo);
        
        addField(mainPanel, gbc, "Título:", titleField);
        addField(mainPanel, gbc, "Descrição:", new JScrollPane(descriptionArea));
        addField(mainPanel, gbc, "Data de Entrega:", dueDateField);
        addField(mainPanel, gbc, "Prioridade:", priorityCombo);
        addField(mainPanel, gbc, "Categoria:", categoryCombo);
        addField(mainPanel, gbc, "Status:", statusCombo);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");
        styleButton(saveButton);
        styleButton(cancelButton);
        
        saveButton.addActionListener(e -> {
            task.setTitle(titleField.getText());
            task.setDescription(descriptionArea.getText());
            task.setDueDate(dueDateField.getText());
            task.setPriority((Priority) priorityCombo.getSelectedItem());
            task.setCategory((String) categoryCombo.getSelectedItem());
            task.setStatus((TaskStatus) statusCombo.getSelectedItem());
            
            taskController.updateTask(task);
            refreshTasks();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateTaskRow(int row, BaseTask task) {
        tableModel.setValueAt(task.getTitle(), row, 0);
        tableModel.setValueAt(task.getDescription(), row, 1);
        tableModel.setValueAt(task.getStatus(), row, 2);
        tableModel.setValueAt(task.getDueDate(), row, 3);
        tableModel.setValueAt(task.getPriority(), row, 4);
        tableModel.setValueAt(task.getCategory(), row, 5);
        tableModel.setValueAt("Ver Detalhes", row, 6);
    }

    private void addTaskRow(BaseTask task) {
        taskIds.add(task.getId());
        tableModel.addRow(new Object[]{
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getDueDate(),
            task.getPriority(),
            task.getCategory(),
            "Ver Detalhes"
        });
    }

    private void addDetailField(JPanel panel, GridBagConstraints gbc, String labelText, String value) {
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT_COLOR);
        panel.add(valueLabel, gbc);
    }

    private void refreshTasks() {
        loadTasks();
    }

    // Atualiza o status da tarefa quando o usuário muda no ComboBox
    private void updateTaskStatus(int row, TaskStatus newStatus) {
        String taskId = taskIds.get(row);
        taskController.updateTaskStatus(taskId, newStatus);
        refreshTasks();
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(30, 30, 30));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, FIELD_HEIGHT));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void updateCategoryFilter() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        categoryFilter.removeAllItems();
        categoryFilter.addItem("Todas");
        
        List<Category> userCategories = categoryService.getUserCategories(currentUser);
        for (Category category : userCategories) {
            categoryFilter.addItem(category.getName());
        }
        
        if (selectedCategory != null && userCategories.stream().anyMatch(c -> c.getName().equals(selectedCategory))) {
            categoryFilter.setSelectedItem(selectedCategory);
        } else {
            categoryFilter.setSelectedIndex(0);
        }
    }
} 