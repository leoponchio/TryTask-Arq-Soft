package models.tasks;

import models.Priority;
import java.time.LocalDate;

public abstract class BaseTask {
    private String id;
    private String title;
    private String description;
    private String dueDate;
    private Priority priority;
    private String category;
    private boolean completed;
    private TaskStatus status;
    private LocalDate deadline;

    public BaseTask() {
        this.status = TaskStatus.PENDENTE; // Status padrão
    }

    public BaseTask(String id, String title, String description, String dueDate, Priority priority, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.status = TaskStatus.PENDENTE;
    }

    public BaseTask(String title, String description, LocalDate deadline, Priority priority, String category) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDENTE; // Status padrão
        this.deadline = deadline;
        this.priority = priority;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
} 