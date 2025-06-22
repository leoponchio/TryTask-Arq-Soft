package services;

import models.Category;
import models.User;
import models.UserType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class CategoryService {
    private static CategoryService instance;
    private static final String CATEGORIES_DIRECTORY = "categories";
    private final Gson gson;
    private Map<String, List<Category>> categoriesByVersion;
    private User currentUser;
    private UserType currentUserType;

    private CategoryService() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        this.categoriesByVersion = new HashMap<>();
        
        // Cria o diretório de categorias se não existir
        File directory = new File(CATEGORIES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public void setCurrentUser(User user, UserType userType) {
        this.currentUser = user;
        this.currentUserType = userType;
        
        // Se ainda não carregamos as categorias desta versão, carrega agora
        String userKey = getUserKey();
        if (!categoriesByVersion.containsKey(userKey)) {
            loadCategoriesForCurrentUser();
        }
    }

    private String getCategoriesFilePath() {
        return CATEGORIES_DIRECTORY + File.separator + getUserKey() + "_categories.json";
    }

    private void loadCategoriesForCurrentUser() {
        if (currentUser == null || currentUserType == null) {
            return;
        }

        String userKey = getUserKey();
        File file = new File(getCategoriesFilePath());
        List<Category> categories = new ArrayList<>();

        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<List<Category>>(){}.getType();
                List<Category> loaded = gson.fromJson(reader, type);
                if (loaded != null) {
                    categories = loaded;
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar categorias: " + e.getMessage());
                // Se houver erro na leitura, tenta criar um novo arquivo
                try (Writer writer = new FileWriter(file)) {
                    writer.write("[]");
                } catch (IOException ex) {
                    System.err.println("Erro ao criar arquivo de categorias: " + ex.getMessage());
                }
            }
        } else {
            // Se o arquivo não existe, cria um novo arquivo vazio
            try (Writer writer = new FileWriter(file)) {
                writer.write("[]");
            } catch (IOException e) {
                System.err.println("Erro ao criar arquivo de categorias: " + e.getMessage());
            }
        }

        categoriesByVersion.put(userKey, categories);
    }

    private String getUserKey() {
        if (currentUser == null || currentUserType == null) {
            throw new IllegalStateException("Usuário ou tipo de usuário não definido");
        }
        return currentUser.getEmail() + "_" + currentUserType.toString();
    }

    public void addCategory(User user, String categoryName) {
        if (!user.equals(currentUser)) {
            throw new IllegalArgumentException("Usuário inválido");
        }

        String userKey = getUserKey();
        List<Category> userCategories = categoriesByVersion.computeIfAbsent(userKey, k -> new ArrayList<>());
        
        // Verifica se a categoria já existe
        boolean categoryExists = userCategories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(categoryName));
        
        if (!categoryExists) {
            Category newCategory = new Category(categoryName);
            newCategory.setUserType(currentUserType.toString());
            newCategory.setUserId(currentUser.getEmail());
            userCategories.add(newCategory);
            saveCategories();
        }
    }

    public void removeCategory(User user, String categoryName) {
        if (!user.equals(currentUser)) {
            throw new IllegalArgumentException("Usuário inválido");
        }

        String userKey = getUserKey();
        List<Category> userCategories = categoriesByVersion.get(userKey);
        if (userCategories != null) {
            userCategories.removeIf(category -> category.getName().equalsIgnoreCase(categoryName));
            saveCategories();
        }
    }

    public List<Category> getUserCategories(User user) {
        if (!user.equals(currentUser)) {
            throw new IllegalArgumentException("Usuário inválido");
        }

        String userKey = getUserKey();
        return new ArrayList<>(categoriesByVersion.getOrDefault(userKey, new ArrayList<>()));
    }

    public List<String> getAllCategories() {
        List<String> allCategories = new ArrayList<>();
        String userKey = getUserKey();
        List<Category> userCategories = categoriesByVersion.get(userKey);
        
        if (userCategories != null) {
            userCategories.stream()
                .map(Category::getName)
                .forEach(name -> {
                    if (!allCategories.contains(name)) {
                        allCategories.add(name);
                    }
                });
        }
        
        return allCategories;
    }

    private boolean saveCategories() {
        if (currentUser == null || currentUserType == null) {
            return false;
        }

        String userKey = getUserKey();
        List<Category> categories = categoriesByVersion.get(userKey);
        if (categories == null) {
            return false;
        }

        // Garante que o diretório existe
        File directory = new File(CATEGORIES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Salva as categorias no arquivo
        File file = new File(getCategoriesFilePath());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(categories, writer);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar categorias: " + e.getMessage());
            return false;
        }
    }
} 