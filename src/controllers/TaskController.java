package controllers;

import models.Priority;
import models.tasks.*;
import services.TaskService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {
    private TaskService taskService;

    public TaskController() {
        this.taskService = TaskService.getInstance();
    }

    public void createTask(BaseTask task) {
        taskService.addTask(task);
    }

    public void updateTask(BaseTask task) {
        taskService.updateTask(task);
    }

    public void deleteTask(String taskId) {
        taskService.deleteTask(taskId);
    }

    public List<BaseTask> getAllTasks() {
        return taskService.getAllTasks();
    }

    public BaseTask getTaskByTitle(String title) {
        return taskService.getTaskByTitle(title);
    }

    public List<BaseTask> getTasksByCategory(String category) {
        return taskService.getTasksByCategory(category);
    }

    public List<BaseTask> getCompletedTasks() {
        return taskService.getTasksByStatus(true);
    }

    public List<BaseTask> getPendingTasks() {
        return taskService.getTasksByStatus(false);
    }

    public List<StudentTask> getStudentTasks() {
        return taskService.getTasksByType(StudentTask.class)
            .stream()
            .map(task -> (StudentTask) task)
            .collect(Collectors.toList());
    }

    public List<ProjectTask> getProjectTasks() {
        return taskService.getTasksByType(ProjectTask.class)
            .stream()
            .map(task -> (ProjectTask) task)
            .collect(Collectors.toList());
    }

    public List<HomeTask> getHomeTasks() {
        return taskService.getTasksByType(HomeTask.class)
            .stream()
            .map(task -> (HomeTask) task)
            .collect(Collectors.toList());
    }

    public List<RotinaTask> getRotinaTasks() {
        return taskService.getTasksByType(RotinaTask.class)
            .stream()
            .map(task -> (RotinaTask) task)
            .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return taskService.getAllTasks().stream()
            .map(BaseTask::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    public void markTaskAsCompleted(String taskId) {
        List<BaseTask> tasks = taskService.getAllTasks();
        for (BaseTask task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setStatus(TaskStatus.CONCLUIDO);
                break;
            }
        }
    }

    public void updateTaskStatus(String taskId, TaskStatus newStatus) {
        taskService.updateTaskStatus(taskId, newStatus);
    }
} 