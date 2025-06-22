package models;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private Set<UserType> userTypes;

    // Construtor padrão para o Gson
    public User() {
        this.userTypes = new HashSet<>();
    }

    public User(String name, String email, String password, UserType userType) {
        this();  // Chama o construtor padrão para inicializar o Set
        this.name = name;
        this.email = email;
        this.password = password;
        this.userTypes.add(userType);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<UserType> getUserTypes() {
        if (userTypes == null) {
            userTypes = new HashSet<>();
        }
        return new HashSet<>(userTypes);
    }

    public boolean hasUserType(UserType userType) {
        if (userTypes == null) {
            userTypes = new HashSet<>();
        }
        return userTypes.contains(userType);
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addUserType(UserType userType) {
        if (userTypes == null) {
            userTypes = new HashSet<>();
        }
        this.userTypes.add(userType);
    }

    public void removeUserType(UserType userType) {
        if (userTypes == null) {
            userTypes = new HashSet<>();
        }
        this.userTypes.remove(userType);
    }

    // Setter para o Gson
    public void setUserTypes(Set<UserType> userTypes) {
        this.userTypes = userTypes != null ? userTypes : new HashSet<>();
    }
} 