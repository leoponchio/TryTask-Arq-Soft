package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskService {
    private static final String FILE_PATH = "tasks.json";
    private final Gson gson;

    public TaskService() {
        this.gson = new Gson();
    }

    public List<Task> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            Reader reader = new FileReader(file);
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> tasks = gson.fromJson(reader, type);
            reader.close();
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveTask(Task task) {
        List<Task> tasks = loadTasks();
        boolean found = false;

        // Atualizar tarefa existente ou adicionar nova
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                found = true;
                break;
            }
        }

        if (!found) {
            tasks.add(task);
        }

        saveTasks(tasks);
    }

    public boolean deleteTask(String id) {
        List<Task> tasks = loadTasks();
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (removed) {
            saveTasks(tasks);
        }
        return removed;
    }

    private void saveTasks(List<Task> tasks) {
        try {
            FileWriter writer = new FileWriter(FILE_PATH);
            gson.toJson(tasks, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Task createTask(String title, String description, String status, String dueDate, Task.Priority priority, String category) {
        Task task = new Task(UUID.randomUUID().toString(), title, description, status, dueDate, priority, category);
        List<Task> tasks = loadTasks();
        tasks.add(task);
        saveTasks(tasks);
        return task;
    }

    public Task updateTask(Task task) {
        List<Task> tasks = loadTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                saveTasks(tasks);
                return task;
            }
        }
        return null;
    }
} 