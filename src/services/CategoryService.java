package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Category;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryService {
    private static final String FILE_PATH = "categories.json";
    private final Gson gson;

    public CategoryService() {
        this.gson = new Gson();
    }

    public List<Category> loadCategories() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            Reader reader = new FileReader(file);
            Type type = new TypeToken<List<Category>>(){}.getType();
            List<Category> categories = gson.fromJson(reader, type);
            reader.close();
            return categories != null ? categories : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Category> getUserCategories(String userId) {
        return loadCategories().stream()
            .filter(category -> category.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    public Category createCategory(String name, String userId) {
        Category category = new Category(UUID.randomUUID().toString(), name, userId);
        List<Category> categories = loadCategories();
        categories.add(category);
        saveCategories(categories);
        return category;
    }

    public void updateCategory(Category category) {
        List<Category> categories = loadCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(category.getId())) {
                categories.set(i, category);
                saveCategories(categories);
                break;
            }
        }
    }

    public void deleteCategory(String id) {
        List<Category> categories = loadCategories();
        categories.removeIf(category -> category.getId().equals(id));
        saveCategories(categories);
    }

    private void saveCategories(List<Category> categories) {
        try {
            FileWriter writer = new FileWriter(FILE_PATH);
            gson.toJson(categories, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 