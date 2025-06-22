package models.tasks;

import models.Priority;

public class HomeTask extends BaseTask {
    private String location; // Room or area of the house
    private String frequency; // "ONCE", "DAILY", "WEEKLY", "MONTHLY"
    private String taskType; // "CLEANING", "SHOPPING", "MAINTENANCE", "APPOINTMENT"
    private double estimatedCost;
    private String assignedTo;

    public HomeTask(String id, String title, String description, String dueDate, Priority priority, String category) {
        super(id, title, description, dueDate, priority, category);
        this.location = "";
        this.frequency = "";
        this.taskType = "";
        this.estimatedCost = 0.0;
        this.assignedTo = "";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
} 