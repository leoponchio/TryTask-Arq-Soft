package models.tasks;

import models.Priority;

public class RotinaTask extends BaseTask {
    public RotinaTask(String id, String title, String description, String dueDate, Priority priority, String category) {
        super(id, title, description, dueDate, priority, category);
    }
} 