package org.example.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for BotService message processing")
class BotServiceTest {

    private BotService botService;

    @BeforeEach
    void setUp() {
        botService = new BotService();
    }

    @Nested
    @DisplayName("New user starts interaction")
    class NewUserStart {

        @Test
        @DisplayName("Should return welcome message for /start command")
        void shouldReturnWelcomeMessageForStartCommand() {
            String response = botService.processMessage(1L, "/start");

            assertThat(response).isEqualTo("Welcome! Use 'create' to start building your room.");
        }

        @Test
        @DisplayName("Should return welcome message for hello command")
        void shouldReturnWelcomeMessageForHelloCommand() {
            String response = botService.processMessage(1L, "hello");

            assertThat(response).isEqualTo("Welcome! Use 'create' to start building your room.");
        }

        @Test
        @DisplayName("Should be case-insensitive for start/hello")
        void shouldBeCaseInsensitiveForStartHello() {
            String response = botService.processMessage(1L, "HELLO");

            assertThat(response).isEqualTo("Welcome! Use 'create' to start building your room.");
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

            assertThat(response).isNotBlank();
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

            assertThat(response).contains("+");
            assertThat(response).contains("|");
            assertThat(response).contains("-");
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

            assertThat(response).matches("You need \\d+ rolls\\.");
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

            assertThat(response).matches("Floor area: \\d+\\.\\d{2} m²");
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

            assertThat(response1).contains("Room1");
            assertThat(response1).doesNotContain("Room2");
            assertThat(response2).contains("Room2");
            assertThat(response2).doesNotContain("Room1");
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

            assertThat(exitResponse).isEqualTo("Goodbye!");
            assertThat(nextResponse).isEqualTo("No room created.");
        }

        @Test
        @DisplayName("Should allow new session after exit")
        void shouldAllowNewSessionAfterExit() {
            long chatId = 1L;

            botService.processMessage(chatId, "create FirstRoom 3000 2000 2500");
            botService.processMessage(chatId, "exit");
            botService.processMessage(chatId, "create SecondRoom 4000 3000 2700");
            String response = botService.processMessage(chatId, "describe");

            assertThat(response).contains("SecondRoom");
            assertThat(response).doesNotContain("FirstRoom");
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

            assertThat(response).isEqualTo("Unknown command: unknownCommand arg1 arg2");
        }
    }
}