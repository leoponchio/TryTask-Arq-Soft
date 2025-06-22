package models;

public enum Priority {
    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 