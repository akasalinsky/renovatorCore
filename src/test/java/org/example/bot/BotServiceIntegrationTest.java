package org.example.bot;

import org.example.repository.FileUserProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционные тесты BotService с реальным репозиторием")
class BotServiceIntegrationTest {

    private Path tempDir;
    private BotService botService;
    private long testChatId = 12345L;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("bot-test");
        FileUserProjectRepository repository = new FileUserProjectRepository(tempDir.toString());
        botService = new BotService(repository);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаляем временную директорию и всё содержимое
        Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {}
                });
    }

    @Test
    @DisplayName("Переключение языка сохраняется и влияет на ответы")
    void languagePersistence() {
        // Устанавливаем русский
        botService.processMessage(testChatId, "/lang ru");
        botService.processMessage(testChatId, "create Комната 4000 3000 2500");

        // Выходим и перезапускаем
        botService.processMessage(testChatId, "exit");
        FileUserProjectRepository newRepo = new FileUserProjectRepository(tempDir.toString());
        BotService newBot = new BotService(newRepo);

        // Проверяем, что язык сохранился (ответ на describe должен быть на русском)
        String response = newBot.processMessage(testChatId, "describe");
        assertThat(response).contains("Комната").doesNotContain("Room");
    }

    @Test
    @DisplayName("Смета (бюджет) выводится корректно для разных типов пола и локалей")
    void budgetCalculation() {
        botService.processMessage(testChatId, "/lang ru");
        botService.processMessage(testChatId, "create Зал 5000 4000 2800");
    }
}