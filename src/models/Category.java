package models;

public class Category {
    private String id;
    private String name;
    private String userType;
    private String userId;

    public Category(String name) {
        this.name = name;
    }

    public Category(String id, String name, String userType, String userId) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
} 