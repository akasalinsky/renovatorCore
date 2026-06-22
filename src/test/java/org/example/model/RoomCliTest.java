package org.example.model;

import org.example.cli.RoomCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for RoomCli command processing")
class RoomCliTest {

    private RoomCli cli;

    /*@BeforeEach
    void setUp() {
        cli = new RoomCli();
    }*/

    @Nested
    @DisplayName("Create command")
    class CreateCommand {

        @Test
        @DisplayName("Should create a new room with valid parameters")
        void shouldCreateNewRoom() {
            String result = cli.execute("create Hall 4610 3020 2700");

            assertThat(result).isEqualTo("Room 'Hall' created.");
        }

        @Test
        @DisplayName("Should update existing room with same name")
        void shouldUpdateExistingRoom() {
            cli.execute("create Hall 4610 3020 2700");
            String result = cli.execute("create Hall 5000 3000 2800");

            assertThat(result).isEqualTo("Room 'Hall' created.");
        }

        @Test
        @DisplayName("Should return usage when insufficient arguments")
        void shouldReturnUsageForInsufficientArguments() {
            String result = cli.execute("create Hall 4610 3020");

            assertThat(result).isEqualTo("Usage: create <name> <length> <width> <height>");
        }

        @Test
        @DisplayName("Should handle command with extra spaces")
        void shouldHandleExtraSpaces() {
            String result = cli.execute("  create   Hall   4610   3020   2700   ");

            assertThat(result).isEqualTo("Room 'Hall' created.");
        }

        @Test
        @DisplayName("Should be case-insensitive")
        void shouldBeCaseInsensitive() {
            String result = cli.execute("CREATE Hall 4610 3020 2700");

            assertThat(result).isEqualTo("Room 'Hall' created.");
        }
    }

    @Nested
    @DisplayName("Describe command")
    class DescribeCommand {

        @Test
        @DisplayName("Should return error when no room created")
        void shouldReturnErrorWhenNoRoomCreated() {
            String result = cli.execute("describe");

            assertThat(result).isEqualTo("No room created.");
        }

        @Test
        @DisplayName("Should return room description when room exists")
        void shouldReturnRoomDescription() {
            cli.execute("create Hall 4610 3020 2700");
            String result = cli.execute("describe");

            assertThat(result).isNotEmpty();
            assertThat(result).contains("Hall");
        }
    }

    @Nested
    @DisplayName("Plan command")
    class PlanCommand {

        @Test
        @DisplayName("Should return error when no room created")
        void shouldReturnErrorWhenNoRoomCreated() {
            String result = cli.execute("plan");

            assertThat(result).isEqualTo("No room created.");
        }

        @Test
        @DisplayName("Should render plan with default scale 500")
        void shouldRenderPlanWithDefaultScale() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("plan");

            assertThat(result).contains("+");
            assertThat(result).contains("|");
            assertThat(result).contains("-");
        }

        @Test
        @DisplayName("Should render plan with custom scale")
        void shouldRenderPlanWithCustomScale() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("plan 1000");

            assertThat(result).contains("+");
            assertThat(result).contains("|");
            assertThat(result).contains("-");
        }
    }

    @Nested
    @DisplayName("Area command")
    class AreaCommand {

        @Test
        @DisplayName("Should return error when no room created")
        void shouldReturnErrorWhenNoRoomCreated() {
            String result = cli.execute("area");

            assertThat(result).isEqualTo("No room created.");
        }

        @Test
        @DisplayName("Should return floor area with two decimal places")
        void shouldReturnFloorArea() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("area");

            assertThat(result).matches("Floor area: \\d+\\.\\d{2} m²");
        }

        @Test
        @DisplayName("Should calculate correct area for 4x3 meter room")
        void shouldCalculateCorrectArea() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("area");

            assertThat(result).isEqualTo("Floor area: 12.00 m²");
        }
    }

    @Nested
    @DisplayName("Wallpaper command")
    class WallpaperCommand {

        @Test
        @DisplayName("Should return error when no room created")
        void shouldReturnErrorWhenNoRoomCreated() {
            String result = cli.execute("wallpaper 530 10000");

            assertThat(result).isEqualTo("No room created.");
        }

        @Test
        @DisplayName("Should calculate required rolls")
        void shouldCalculateRequiredRolls() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("wallpaper 530 10000");

            assertThat(result).matches("You need \\d+ rolls\\.");
        }

        @Test
        @DisplayName("Should return usage when insufficient arguments")
        void shouldReturnUsageForInsufficientArguments() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("wallpaper 530");

            assertThat(result).isEqualTo("Usage: wallpaper <rollWidth> <rollLength>");
        }

        @Test
        @DisplayName("Should handle command with extra spaces")
        void shouldHandleExtraSpaces() {
            cli.execute("create Hall 4000 3000 2500");
            String result = cli.execute("  wallpaper   530   10000  ");

            assertThat(result).matches("You need \\d+ rolls\\.");
        }
    }

    @Nested
    @DisplayName("Exit command")
    class ExitCommand {

        @Test
        @DisplayName("Should return goodbye message")
        void shouldReturnGoodbyeMessage() {
            String result = cli.execute("exit");

            assertThat(result).isEqualTo("Goodbye!");
        }

        @Test
        @DisplayName("Should be case-insensitive")
        void shouldBeCaseInsensitive() {
            String result = cli.execute("EXIT");

            assertThat(result).isEqualTo("Goodbye!");
        }
    }

    @Nested
    @DisplayName("Unknown command")
    class UnknownCommand {

        @Test
        @DisplayName("Should return unknown command message")
        void shouldReturnUnknownCommandMessage() {
            String result = cli.execute("foo");

            assertThat(result).isEqualTo("Unknown command: foo");
        }

        @Test
        @DisplayName("Should preserve original command text in error message")
        void shouldPreserveOriginalCommandText() {
            String result = cli.execute("invalidCommand arg1 arg2");

            assertThat(result).isEqualTo("Unknown command: invalidCommand arg1 arg2");
        }
    }
}