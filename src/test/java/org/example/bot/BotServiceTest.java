package org.example.bot;

import org.example.model.Room;
import org.example.model.UserProjects;
import org.example.model.Wall;
import org.example.repository.FileUserProjectRepository;
import org.example.repository.UserProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for BotService message processing")
class BotServiceTest {

    @TempDir
    Path tempDir;

    private BotService botService;
    private UserProjectRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FileUserProjectRepository(tempDir.toString());
        botService = new BotService(repository);
    }

    @Nested
    @DisplayName("New user starts interaction")
    class NewUserStart {

        @Test
        @DisplayName("Should return welcome message for /start command")
        void shouldReturnWelcomeMessageForStartCommand() {
            String response = botService.processMessage(1L, "/start");
            assertThat(response).contains("Добро пожаловать! 🛠 Я — Бот-планировщик комнат.");
        }

        @Test
        @DisplayName("Should return welcome message for hello command")
        void shouldReturnWelcomeMessageForHelloCommand() {
            String response = botService.processMessage(1L, "hello");
            assertThat(response).contains("Добро пожаловать! 🛠 Я — Бот-планировщик комнат.");
        }

        @Test
        @DisplayName("Should be case-insensitive for start/hello")
        void shouldBeCaseInsensitiveForStartHello() {
            String response = botService.processMessage(1L, "HELLO");
            assertThat(response).contains("Добро пожаловать! 🛠 Я — Бот-планировщик комнат.");
        }
    }

    @Nested
    @DisplayName("Create and describe room")
    class CreateAndDescribe {

        @Test
        @DisplayName("Should create a room and then describe it")
        void shouldCreateAndThenDescribeRoom() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Kitchen 3000 2000 2500");
            String response = botService.processMessage(chatId, "describe");
            assertThat(response).contains("Kitchen");
        }
    }

    @Nested
    @DisplayName("Plan command")
    class Plan {

        @Test
        @DisplayName("Should render plan after creating a room")
        void shouldRenderPlanAfterCreatingRoom() {
            long chatId = 1L;
            botService.processMessage(chatId, "create LivingRoom 4000 3000 2700");
            String response = botService.processMessage(chatId, "plan 100");
            assertThat(response).contains("+").contains("|").contains("-");
        }
    }

    @Nested
    @DisplayName("Wallpaper calculation")
    class Wallpaper {

        @Test
        @DisplayName("Should calculate wallpaper rolls after creating a room")
        void shouldCalculateWallpaperRollsAfterCreatingRoom() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Bedroom 3500 2500 2600");
            String response = botService.processMessage(chatId, "wallpaper 530 10000");
            assertThat(response).matches(".*\\d+.*");
        }
    }

    @Nested
    @DisplayName("Area calculation")
    class Area {

        @Test
        @DisplayName("Should return floor area after creating a room")
        void shouldReturnFloorAreaAfterCreatingRoom() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Office 2000 2000 2500");
            String response = botService.processMessage(chatId, "area");
            assertThat(response).matches(".*\\d+\\.\\d{2}.*");
        }
    }

    @Nested
    @DisplayName("Multiple users isolation")
    class MultipleUsers {

        @Test
        @DisplayName("Should isolate sessions between different users")
        void shouldIsolateSessionsBetweenDifferentUsers() {
            long chatId1 = 1L;
            long chatId2 = 2L;

            botService.processMessage(chatId1, "create Room1 3000 2000 2500");
            botService.processMessage(chatId2, "create Room2 4000 3000 2700");

            String response1 = botService.processMessage(chatId1, "describe");
            String response2 = botService.processMessage(chatId2, "describe");

            assertThat(response1).contains("Room1").doesNotContain("Room2");
            assertThat(response2).contains("Room2").doesNotContain("Room1");
        }
    }

    @Nested
    @DisplayName("Exit command and session reset")
    class Exit {

        @Test
        @DisplayName("Should reset session after exit command")
        void shouldResetSessionAfterExitCommand() {
            long chatId = 1L;

            botService.processMessage(chatId, "create TestRoom 3000 2000 2500");
            String exitResponse = botService.processMessage(chatId, "exit");
            String nextResponse = botService.processMessage(chatId, "describe");

            assertThat(exitResponse).contains("До свидания!");
            assertThat(nextResponse).contains("Сначала нужно создать комнату");
        }

        @Test
        @DisplayName("Should allow new session after exit")
        void shouldAllowNewSessionAfterExit() {
            long chatId = 1L;

            botService.processMessage(chatId, "create FirstRoom 3000 2000 2500");
            botService.processMessage(chatId, "exit");
            botService.processMessage(chatId, "create SecondRoom 4000 3000 2700");
            String response = botService.processMessage(chatId, "describe");

            assertThat(response).contains("SecondRoom").doesNotContain("FirstRoom");
        }
    }

    @Nested
    @DisplayName("Unknown command handling")
    class UnknownCommand {

        @Test
        @DisplayName("Should return unknown command message")
        void shouldReturnUnknownCommandMessage() {
            long chatId = 1L;
            String response = botService.processMessage(chatId, "unknownCommand arg1 arg2");
            assertThat(response).contains("не знаю команду");
        }
    }

    @Nested
    @DisplayName("Repository - JSON persistence")
    class JsonPersistence {

        @Test
        @DisplayName("Should save room to JSON after create command")
        void shouldSaveRoomToJsonAfterCreate() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Kitchen 3000 2000 2500");

            Optional<UserProjects> loaded = repository.findByChatId(chatId);
            assertThat(loaded).isPresent();
            assertThat(loaded.get().rooms()).hasSize(1);
            assertThat(loaded.get().rooms().getFirst().name()).isEqualTo("Kitchen");
        }

        @Test
        @DisplayName("Should load room from JSON after bot restart")
        void shouldLoadRoomFromJsonAfterRestart() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Bedroom 4000 3000 2700");

            BotService newBotService = new BotService(repository);
            // После перезапуска бот должен загрузить данные, но приветственное сообщение не содержит "Welcome back"
            // Просто проверяем, что describe возвращает правильную комнату
            String describeResponse = newBotService.processMessage(chatId, "describe");
            assertThat(describeResponse).contains("Bedroom");
        }

        @Test
        @DisplayName("Should save language change to JSON")
        void shouldSaveLanguageToJson() {
            long chatId = 1L;
            botService.processMessage(chatId, "/lang ru");

            Optional<UserProjects> loaded = repository.findByChatId(chatId);
            assertThat(loaded).isPresent();
            assertThat(loaded.get().locale().getLanguage()).isEqualTo("ru");
        }

        @Test
        @DisplayName("Should restore language after restart")
        void shouldRestoreLanguageAfterRestart() {
            long chatId = 1L;
            botService.processMessage(chatId, "/lang ru");

            BotService newBotService = new BotService(repository);
            newBotService.processMessage(chatId, "hello");

            Optional<UserProjects> loaded = repository.findByChatId(chatId);
            assertThat(loaded.get().locale().getLanguage()).isEqualTo("ru");
        }

        @Test
        @DisplayName("Should keep JSON file after exit but reset session")
        void shouldKeepJsonAfterExit() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Room 3000 2000 2500");
            botService.processMessage(chatId, "exit");

            Optional<UserProjects> loaded = repository.findByChatId(chatId);
            assertThat(loaded).isPresent();
            assertThat(loaded.get().rooms()).hasSize(1);

            // При следующем сообщении должен загрузить из JSON
            BotService newBotService = new BotService(repository);
            // Проверяем, что describe работает с загруженной комнатой
            String response = newBotService.processMessage(chatId, "describe");
            assertThat(response).contains("Room");
        }

        @Test
        @DisplayName("Should persist multiple rooms across restarts")
        void shouldPersistMultipleRooms() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Room1 3000 2000 2500");
            botService.processMessage(chatId, "create Room2 4000 3000 2700");
            botService.processMessage(chatId, "create Room3 5000 4000 2800");

            BotService newBotService = new BotService(repository);
            newBotService.processMessage(chatId, "hello");

            Optional<UserProjects> loaded = repository.findByChatId(chatId);
            assertThat(loaded.get().rooms()).hasSize(3);
        }

        @Test
        @DisplayName("Should isolate data between different chatIds in JSON")
        void shouldIsolateDataBetweenChatIds() {
            long chatId1 = 1L;
            long chatId2 = 2L;

            botService.processMessage(chatId1, "create Room1 3000 2000 2500");
            botService.processMessage(chatId2, "create Room2 4000 3000 2700");

            Optional<UserProjects> loaded1 = repository.findByChatId(chatId1);
            Optional<UserProjects> loaded2 = repository.findByChatId(chatId2);

            assertThat(loaded1.get().rooms().getFirst().name()).isEqualTo("Room1");
            assertThat(loaded2.get().rooms().getFirst().name()).isEqualTo("Room2");
        }
    }
}