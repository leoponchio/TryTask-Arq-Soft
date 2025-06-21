package test;

import data.DbManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SelecionarTarefaConcluidaTest {

    private DbManager db;

    @BeforeEach
    public void setUp() {
        db = new DbManager();
        db.deleteAllTasks(); // <- limpa o banco antes do teste
    }

    @AfterEach
    public void tearDown() throws SQLException {
        db.deleteAllTasks(); // <- limpa o banco após o teste
        db.close();
    }

    @Test
    public void selecionarTarefasConcluidasComSucesso() {
        db.deleteAllTasks(); // Garantir banco limpo

        // Inserir tarefas
        db.insertTasks("Passear com cachorro");
        db.insertTasks("Pagar internet");
        db.insertTasks("Pagar cartão"); // essa NÃO será marcada como concluída

        // Marcar 2 tarefas como concluídas
        Map<Integer, String> todas = db.selectTasks(false);
        for (Map.Entry<Integer, String> entry : todas.entrySet()) {
            if (entry.getValue().equals("Passear com cachorro") || entry.getValue().equals("Pagar internet")) {
                db.updateStats(entry.getKey(), true);
            }
        }

        // Buscar tarefas concluídas
        Map<Integer, String> concluidas = db.selectTasks(true);

        // Verificações
        assertTrue(concluidas.containsValue("Passear com cachorro"));
        assertTrue(concluidas.containsValue("Pagar internet"));
        assertFalse(concluidas.containsValue("Pagar cartão"));
    }
}
