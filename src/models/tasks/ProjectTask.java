package models.tasks;

import models.Priority;
import java.util.List;
import java.util.ArrayList;

public class ProjectTask extends BaseTask {
    private String phase;
    private List<String> teamMembers;
    private List<String> subtasks;

    public ProjectTask(String id, String title, String description, String dueDate, Priority priority, String category) {
        super(id, title, description, dueDate, priority, category);
        this.phase = "";
        this.teamMembers = new ArrayList<>();
        this.subtasks = new ArrayList<>();
    }

    public void addTeamMember(String member) {
        teamMembers.add(member);
    }

    public void addSubtask(String subtask) {
        subtasks.add(subtask);
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public List<String> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return String.format("ProjectTask{title='%s', phase='%s', team=%s}",
                getTitle(), phase, String.join(", ", teamMembers));
    }
} 