package models;

public class Category {
    private String id;
    private String name;
    private String userId;

    public Category(String id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUserId() { return userId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUserId(String userId) { this.userId = userId; }
} 