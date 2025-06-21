package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.User;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final String FILE_PATH = "users.json";
    private final Gson gson;
    private Map<String, User> users; // username -> User
    private User currentUser;

    public AuthService() {
        this.gson = new Gson();
        this.users = loadUsers();
        if (this.users.isEmpty()) {
            // Criar usuário padrão se não existir nenhum
            User admin = new User("admin", "Administrador", "admin@trytask.com", "admin123");
            this.users.put("admin", admin);
            saveUsers();
        }
    }

    private Map<String, User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            Reader reader = new FileReader(file);
            Type type = new TypeToken<HashMap<String, User>>(){}.getType();
            Map<String, User> loadedUsers = gson.fromJson(reader, type);
            reader.close();
            return loadedUsers != null ? loadedUsers : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try {
            FileWriter writer = new FileWriter(FILE_PATH);
            gson.toJson(users, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Usuário";
    }

    public boolean registerUser(String username, String name, String email, String password) {
        if (!users.containsKey(username)) {
            User newUser = new User(username, name, email, password);
            users.put(username, newUser);
            saveUsers();
            return true;
        }
        return false;
    }

    public boolean isUsernameAvailable(String username) {
        return !users.containsKey(username);
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
} 