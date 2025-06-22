package services;

import com.google.gson.*;
import models.Priority;
import models.tasks.*;
import models.User;
import models.UserType;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class TaskTypeAdapter implements JsonSerializer<BaseTask>, JsonDeserializer<BaseTask> {
    private static final String TASKS_DIRECTORY = "tasks";
    private final Gson gson;
    private String currentUserKey;
    private Map<String, List<BaseTask>> tasksByVersion;

    public TaskTypeAdapter() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(BaseTask.class, this)
            .setPrettyPrinting()
            .create();
            
        this.tasksByVersion = new HashMap<>();
            
        // Cria o diretório de tarefas se não existir
        File directory = new File(TASKS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public void setCurrentUser(User user, UserType userType) {
        this.currentUserKey = user.getEmail() + "_" + userType.toString();
        
        // Se ainda não carregamos as tarefas desta versão, carrega agora
        if (!tasksByVersion.containsKey(currentUserKey)) {
            loadTasksForCurrentUser();
        }
    }

    private String getTasksFilePath() {
        return TASKS_DIRECTORY + File.separator + currentUserKey + "_tasks.json";
    }

    @Override
    public JsonElement serialize(BaseTask task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("id", task.getId());
        result.addProperty("title", task.getTitle());
        result.addProperty("description", task.getDescription());
        result.addProperty("dueDate", task.getDueDate());
        result.addProperty("priority", task.getPriority().name());
        result.addProperty("category", task.getCategory());
        result.addProperty("status", task.getStatus().name());
        result.addProperty("type", getTaskType(task));

        return result;
    }

    private String getTaskType(BaseTask task) {
        if (task instanceof StudentTask) return "STUDENT";
        if (task instanceof HomeTask) return "HOME";
        if (task instanceof ProjectTask) return "PROJECT";
        return "ROTINA";
    }

    @Override
    public BaseTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
        JsonObject jsonObject = json.getAsJsonObject();
        
            String id = getStringOrDefault(jsonObject, "id", UUID.randomUUID().toString());
            String title = getStringOrDefault(jsonObject, "title", "");
            String description = getStringOrDefault(jsonObject, "description", "");
            String dueDate = getStringOrDefault(jsonObject, "dueDate", "");
            String priorityStr = getStringOrDefault(jsonObject, "priority", Priority.BAIXA.name());
            String category = getStringOrDefault(jsonObject, "category", "");
            String statusStr = getStringOrDefault(jsonObject, "status", TaskStatus.PENDENTE.name());
            String type = getStringOrDefault(jsonObject, "type", "ROTINA");
            
            Priority priority = Priority.valueOf(priorityStr);
            TaskStatus status = TaskStatus.valueOf(statusStr);
        
        BaseTask task;
        switch (type) {
            case "STUDENT":
                task = new StudentTask(id, title, description, dueDate, priority, category);
                break;
            case "HOME":
                task = new HomeTask(id, title, description, dueDate, priority, category);
                break;
            case "PROJECT":
                task = new ProjectTask(id, title, description, dueDate, priority, category);
                break;
            default:
                task = new RotinaTask(id, title, description, dueDate, priority, category);
        }
        
        task.setStatus(status);
        return task;
        } catch (Exception e) {
            System.err.println("Erro ao deserializar tarefa: " + e.getMessage());
            return null;
        }
    }

    private String getStringOrDefault(JsonObject json, String key, String defaultValue) {
        JsonElement element = json.get(key);
        return element != null && !element.isJsonNull() ? element.getAsString() : defaultValue;
    }

    private void loadTasksForCurrentUser() {
        if (currentUserKey == null) {
            return;
        }

        File file = new File(getTasksFilePath());
        List<BaseTask> tasks = new ArrayList<>();
        
        if (file.exists()) {
        try (Reader reader = new FileReader(file)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                try {
                    BaseTask task = deserialize(element, BaseTask.class, null);
                    if (task != null) {
                        tasks.add(task);
                    }
                    } catch (Exception e) {
                    System.err.println("Erro ao carregar tarefa: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar tarefas: " + e.getMessage());
                // Se houver erro na leitura, tenta criar um novo arquivo
                try (Writer writer = new FileWriter(file)) {
                    writer.write("[]");
                } catch (IOException ex) {
                    System.err.println("Erro ao criar arquivo de tarefas: " + ex.getMessage());
                }
            }
        } else {
            // Se o arquivo não existe, cria um novo arquivo vazio
            try (Writer writer = new FileWriter(file)) {
                writer.write("[]");
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de tarefas: " + e.getMessage());
            }
        }
        
        tasksByVersion.put(currentUserKey, tasks);
    }

    public void saveTasks(List<BaseTask> tasks) {
        if (currentUserKey == null) {
            System.err.println("Erro: Usuário não definido");
            return;
        }

        // Atualiza as tarefas da versão atual
        tasksByVersion.put(currentUserKey, new ArrayList<>(tasks));

        // Garante que o diretório existe
        File directory = new File(TASKS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Salva as tarefas no arquivo
        File file = new File(getTasksFilePath());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (Writer writer = new FileWriter(file)) {
                JsonArray jsonArray = new JsonArray();
                for (BaseTask task : tasks) {
                    jsonArray.add(serialize(task, BaseTask.class, null));
                }
                gson.toJson(jsonArray, writer);
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }

    public List<BaseTask> loadTasks() {
        if (currentUserKey == null) {
            System.err.println("Erro: Usuário não definido");
            return new ArrayList<>();
        }

        // Se ainda não carregamos as tarefas desta versão, carrega agora
        if (!tasksByVersion.containsKey(currentUserKey)) {
            loadTasksForCurrentUser();
        }

        // Retorna uma cópia das tarefas da versão atual
        return new ArrayList<>(tasksByVersion.getOrDefault(currentUserKey, new ArrayList<>()));
    }
} 