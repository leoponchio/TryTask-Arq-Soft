package models.tasks;

import models.Priority;

public class StudentTask extends BaseTask {
    private String subject;
    private String deliveryDate;
    private String taskType; // "HOMEWORK", "EXAM", "PROJECT"
    private double grade;

    public StudentTask(String id, String title, String description, String dueDate, Priority priority, String category) {
        super(id, title, description, dueDate, priority, category);
        this.subject = "";
        this.deliveryDate = "";
        this.taskType = "";
        this.grade = 0.0;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
} 