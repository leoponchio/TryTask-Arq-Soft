package models;

public enum UserType {
    STUDENT("Estudante"),
    PROJECT_MANAGER("Gerente de Projeto"),
    HOME_MANAGER("Gerente Doméstico");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 