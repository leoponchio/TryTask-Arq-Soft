package controllers;

import models.Task;
import services.TaskService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskController {
    private final TaskService taskService;

    public TaskController() {
        this.taskService = new TaskService();
    }

    public void createTask(String title, String description, String status, String dueDate, Task.Priority priority, String category) {
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, title, description, status, dueDate, priority, category);
        taskService.saveTask(task);
    }

    public void updateTask(Task task) {
        taskService.saveTask(task);
    }

    public void deleteTask(String id) {
        taskService.deleteTask(id);
    }

    public List<Task> getAllTasks() {
        return taskService.loadTasks();
    }

    public List<Task> getFilteredTasks(String status, String category, Task.Priority priority) {
        return taskService.loadTasks().stream()
            .filter(task -> status == null || task.getStatus().equals(status))
            .filter(task -> category == null || 
                (task.getCategory() != null && task.getCategory().equals(category)))
            .filter(task -> priority == null || task.getPriority() == priority)
            .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return taskService.loadTasks().stream()
            .map(Task::getCategory)
            .filter(category -> category != null && !category.trim().isEmpty())
            .distinct()
            .collect(Collectors.toList());
    }
} 