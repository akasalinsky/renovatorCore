package org.example.bot;

import org.example.model.Room;
import org.example.model.Wall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;


import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for BotService message processing")
class BotServiceTest {

    private BotService botService;

    @BeforeEach
    void setUp() {
        botService = new BotService();
    }

    @Nested
    @DisplayName("Repository")
    class SaveData {
        @Test
        @DisplayName("Should create room and save")
        void shouldCreateRoomAndSave(){
            //BotRepository mockRepo = mock(BotRepository.class);
            BotService service = new BotService();
            String name = "Test Room";
            int height = 2500;
            List<Wall> walls = List.of(
                    new Wall(3000, height, 0),
                    new Wall(4000, height, 1),
                    new Wall(3000, height, 2),
                    new Wall(4000, height, 3)
            );
            List<Integer> angles = List.of(90, 90, 90, 90);

            // When
            Room room = new Room(name, height, walls, angles);
        }
    }


    @Nested
    @DisplayName("New user starts interaction")
    class NewUserStart {

        @Test
        @DisplayName("Should return welcome message for /start command")
        void shouldReturnWelcomeMessageForStartCommand() {
            String response = botService.processMessage(1L, "/start", Locale.US);

            assertThat(response).isEqualTo("""
            Welcome! I'm Room Planner Bot.
            
            I can help you calculate floor area, wall surfaces, and wallpaper rolls for your room.
            
            Available commands:
            • create <name> <length> <width> <height> — create a new room (dimensions in mm, openings in m²)
            • describe — show room description and areas
            • plan [scale] — draw an ASCII floor plan (default scale 500 mm/char)
            • area — show floor area
            • wallpaper <roll width mm> <roll length mm> — calculate number of wallpaper rolls
            • lang en|ru — switch language
            • help — show this instruction again
            • exit — exit (in bot, session resets; in CLI, program terminates)
            
            You can switch language at any time by typing /lang ru or /lang en.""");
        }

        @Test
        @DisplayName("Should return welcome message for hello command")
        void shouldReturnWelcomeMessageForHelloCommand() {
            String response = botService.processMessage(1L, "hello", Locale.US);

            assertThat(response).isEqualTo("""
            Welcome! I'm Room Planner Bot.
            
            I can help you calculate floor area, wall surfaces, and wallpaper rolls for your room.
            
            Available commands:
            • create <name> <length> <width> <height> — create a new room (dimensions in mm, openings in m²)
            • describe — show room description and areas
            • plan [scale] — draw an ASCII floor plan (default scale 500 mm/char)
            • area — show floor area
            • wallpaper <roll width mm> <roll length mm> — calculate number of wallpaper rolls
            • lang en|ru — switch language
            • help — show this instruction again
            • exit — exit (in bot, session resets; in CLI, program terminates)
            
            You can switch language at any time by typing /lang ru or /lang en.""");
        }

        @Test
        @DisplayName("Should be case-insensitive for start/hello")
        void shouldBeCaseInsensitiveForStartHello() {
            String response = botService.processMessage(1L, "HELLO", Locale.US);

            assertThat(response).isEqualTo("""
            Welcome! I'm Room Planner Bot.
            
            I can help you calculate floor area, wall surfaces, and wallpaper rolls for your room.
            
            Available commands:
            • create <name> <length> <width> <height> — create a new room (dimensions in mm, openings in m²)
            • describe — show room description and areas
            • plan [scale] — draw an ASCII floor plan (default scale 500 mm/char)
            • area — show floor area
            • wallpaper <roll width mm> <roll length mm> — calculate number of wallpaper rolls
            • lang en|ru — switch language
            • help — show this instruction again
            • exit — exit (in bot, session resets; in CLI, program terminates)
            
            You can switch language at any time by typing /lang ru or /lang en.""");
        }
    }

    @Nested
    @DisplayName("Create and describe room")
    class CreateAndDescribe {

        @Test
        @DisplayName("Should create a room and then describe it")
        void shouldCreateAndThenDescribeRoom() {
            long chatId = 1L;
            botService.processMessage(chatId, "create Kitchen 3000 2000 2500", Locale.US);
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
            botService.processMessage(chatId, "create Bedroom 3500 2500 2600", Locale.US);
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
            botService.processMessage(chatId, "create Office 2000 2000 2500", Locale.US);
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

            botService.processMessage(chatId, "create TestRoom 3000 2000 2500", Locale.US);
            String exitResponse = botService.processMessage(chatId, "exit");
            String nextResponse = botService.processMessage(chatId, "describe");

            assertThat(exitResponse).isEqualTo("До свидания!");
            assertThat(nextResponse).isEqualTo("Комната не создана.");
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
            String response = botService.processMessage(chatId, "unknownCommand arg1 arg2", Locale.US);

            assertThat(response).isEqualTo("Unknown command: unknownCommand arg1 arg2");
        }
    }
}