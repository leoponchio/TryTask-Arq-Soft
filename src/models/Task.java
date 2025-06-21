package models;

public class Task {
    private String id;
    private String title;
    private String description;
    private String status;
    private String dueDate;
    private Priority priority;
    private String category;

    public enum Priority {
        ALTA,
        MEDIA,
        BAIXA
    }

    public Task(String id, String title, String description, String status, String dueDate, Priority priority, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public String getCategory() { return category; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setCategory(String category) { this.category = category; }
} 