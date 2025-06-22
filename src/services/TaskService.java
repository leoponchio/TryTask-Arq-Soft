package services;

import models.Priority;
import models.User;
import models.UserType;
import models.tasks.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskService {
    private static TaskService instance;
    private List<BaseTask> tasks;
    private TaskTypeAdapter taskTypeAdapter;
    private User currentUser;
    private UserType currentUserType;

    private TaskService() {
        this.tasks = new ArrayList<>();
        this.taskTypeAdapter = new TaskTypeAdapter();
    }

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }

    public void setCurrentUser(User user, UserType userType) {
        this.currentUser = user;
        this.currentUserType = userType;
        this.taskTypeAdapter.setCurrentUser(user, userType);
        loadTasks(); // Carrega as tarefas do usuário atual
    }

    public void addTask(BaseTask task) {
        tasks.add(task);
        saveTask(task);
    }

    public void updateTask(BaseTask task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                break;
            }
        }
        saveToAdapter();
    }

    public void deleteTask(String taskId) {
        tasks.removeIf(task -> task.getId().equals(taskId));
        saveToAdapter();
    }

    public List<BaseTask> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public BaseTask getTaskByTitle(String title) {
        return tasks.stream()
            .filter(task -> task.getTitle().equals(title))
            .findFirst()
            .orElse(null);
    }

    public List<BaseTask> getTasksByCategory(String category) {
        return tasks.stream()
            .filter(task -> task.getCategory().equals(category))
            .collect(Collectors.toList());
    }

    public List<BaseTask> getTasksByStatus(boolean completed) {
        return tasks.stream()
            .filter(task -> task.isCompleted() == completed)
            .collect(Collectors.toList());
    }

    public List<BaseTask> getTasksByType(Class<? extends BaseTask> taskType) {
        return tasks.stream()
            .filter(task -> taskType.isInstance(task))
            .collect(Collectors.toList());
    }

    public void updateTaskStatus(String taskId, TaskStatus newStatus) {
        for (BaseTask task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setStatus(newStatus);
                saveTask(task);
                break;
            }
        }
    }

    private void loadTasks() {
        if (currentUser != null && currentUserType != null) {
            tasks = taskTypeAdapter.loadTasks();
        } else {
            tasks = new ArrayList<>();
        }
    }

    private void saveToAdapter() {
        if (currentUser != null && currentUserType != null) {
            taskTypeAdapter.saveTasks(tasks);
        }
    }

    private void saveTask(BaseTask task) {
        saveToAdapter();
    }
} 