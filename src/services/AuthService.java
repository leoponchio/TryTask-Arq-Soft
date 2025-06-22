package services;

import models.User;
import models.UserType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private Map<String, User> users;
    private User currentUser;
    private static final String USERS_FILE = "users.json";
    private final Gson gson;

    private AuthService() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()  // Serializa campos nulos
            .create();
        this.users = loadUsers();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private Map<String, User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, User>>(){}.getType();
            Map<String, User> loadedUsers = gson.fromJson(reader, type);
            return loadedUsers != null ? loadedUsers : new HashMap<>();
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            // Se houver erro na leitura do arquivo, deleta o arquivo corrompido
            file.delete();
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String name, String email, String password, UserType userType) {
        if (!users.containsKey(email)) {
            User newUser = new User(name, email, password, userType);
            users.put(email, newUser);
            saveUsers();
            return true;
        }
        return false;
    }

    public boolean login(String email, String password, UserType userType) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password) && user.hasUserType(userType)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean associateUserType(String email, String password, UserType newUserType) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            if (!user.hasUserType(newUserType)) {
                user.addUserType(newUserType);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public boolean isEmailRegistered(String email) {
        return users.containsKey(email);
    }

    public boolean hasUserType(String email, UserType userType) {
        User user = users.get(email);
        return user != null && user.hasUserType(userType);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
} 