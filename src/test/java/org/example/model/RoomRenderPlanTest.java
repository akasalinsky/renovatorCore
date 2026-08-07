package org.example.model;

import org.example.service.RoomRenderingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for Room.renderPlan() method")
class RoomRenderPlanTest {

    private static final int RIGHT_ANGLE = 90;
    private static final List<Integer> RECTANGULAR_CORNERS =
            List.of(RIGHT_ANGLE, RIGHT_ANGLE, RIGHT_ANGLE, RIGHT_ANGLE);
    private RoomRenderingService roomRenderingService;

    @Nested
    @DisplayName("1. Simple rectangular room without openings")
    class SimpleRectangularRoomWithoutOpenings {

        @Test
        @DisplayName("Should render a 4x3 meter rectangle")
        void shouldRenderSimpleRectangle() {
            // given
            List<Wall> walls = List.of(
                    new Wall(4000, 2700, 0, List.of()),
                    new Wall(3000, 2700, 1, List.of()),
                    new Wall(4000, 2700, 2, List.of()),
                    new Wall(3000, 2700, 3, List.of())
            );
            Room room = new Room("Living Room", 2500, walls, RECTANGULAR_CORNERS);

            // when
            String plan = roomRenderingService.renderPlan(room);

            // then
            String expected =
                    "+----+\n" +
                            "|    |\n" +
                            "|    |\n" +
                            "|    |\n" +
                            "+----+\n";
            assertThat(plan).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("2. Room with a single window on the north wall")
    class RoomWithSingleWindowOnNorthWall {

        @Test
        @DisplayName("1m wide window at 1m from the left edge should break the top wall")
        void shouldRenderWindowOnNorthWall() {
            // given
            WallOpening window = new WallOpening(
                    new Opening(OpeningType.WINDOW, 1000, 1500), 1000, 900);
            List<Wall> walls = List.of(
                    new Wall(4000, 2700, 0, List.of(window)),
                    new Wall(3000, 2700, 1, List.of()),
                    new Wall(4000, 2700, 2, List.of()),
                    new Wall(3000, 2700, 3, List.of())
            );
            Room room = new Room("Bedroom", 2500, walls, RECTANGULAR_CORNERS);

            // when
            String plan = roomRenderingService.renderPlan(room);

            // then
            String expected =
                    "+- --+\n" +
                            "|    |\n" +
                            "|    |\n" +
                            "|    |\n" +
                            "+----+\n";
            assertThat(plan).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("3. Door on the east wall")
    class RoomWithDoorOnEastWall {

        @Test
        @DisplayName("1m wide door at 1m from the start of the wall should break the right wall")
        void shouldRenderDoorOnEastWall() {
            // given
            WallOpening door = new WallOpening(
                    new Opening(OpeningType.DOOR, 1000, 2000), 1000, 0);
            List<Wall> walls = List.of(
                    new Wall(4000, 2700, 0, List.of()),
                    new Wall(3000, 2700, 1, List.of(door)),
                    new Wall(4000, 2700, 2, List.of()),
                    new Wall(3000, 2700, 3, List.of())
            );
            Room room = new Room("Hall", 2500, walls, RECTANGULAR_CORNERS);

            // when
            String plan = roomRenderingService.renderPlan(room);

            // then
            String expected =
                    "+----+\n" +
                            "|    |\n" +
                            "|     \n" +
                            "|    |\n" +
                            "+----+\n";
            assertThat(plan).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("4. Multiple openings on different walls")
    class MultipleOpeningsOnDifferentWalls {

        @Test
        @DisplayName("Openings should be correctly rendered on each wall according to coordinates")
        void shouldRenderOpeningsOnAllFourWalls() {
            // given
            WallOpening northOpening = new WallOpening(
                    new Opening(OpeningType.WINDOW, 1000, 1000), 1000, 0);
            WallOpening eastOpening = new WallOpening(
                    new Opening(OpeningType.WINDOW, 2000, 1000), 1000, 0);
            WallOpening southOpening = new WallOpening(
                    new Opening(OpeningType.WINDOW, 1000, 2000), 1000, 0);
            WallOpening westOpening = new WallOpening(
                    new Opening(OpeningType.WINDOW, 2000, 2000), 1000, 0);

            List<Wall> walls = List.of(
                    new Wall(4000, 2700, 0, List.of(northOpening)),
                    new Wall(3000, 2700, 1, List.of(eastOpening)),
                    new Wall(4000, 2700, 2, List.of(southOpening)),
                    new Wall(3000, 2700, 3, List.of(westOpening))
            );
            Room room = new Room("Complex Room", 2500, walls, RECTANGULAR_CORNERS);

            // when
            String plan = roomRenderingService.renderPlan(room);

            // then
            String expected =
                    "+- --+\n" +
                            "|    |\n" +
                            "      \n" +
                            "      \n" +
                            "+- --+\n";
            assertThat(plan).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("5. Scaling")
    class Scaling {

        @Test
        @DisplayName("Scale of 1 meter = 1 character for a small 2x1 meter room")
        void shouldScaleOneMeterToOneCharacter() {
            // given
            List<Wall> walls = List.of(
                    new Wall(2000, 2700, 0, List.of()),
                    new Wall(1000, 2700, 1, List.of()),
                    new Wall(2000, 2700, 2, List.of()),
                    new Wall(1000, 2700, 3, List.of())
            );
            Room room = new Room("Small Room", 2500, walls, RECTANGULAR_CORNERS);

            // when
            String plan = roomRenderingService.renderPlan(room);

            // then
            String expected =
                    "+--+\n" +
                            "|  |\n" +
                            "+--+\n";
            assertThat(plan).isEqualTo(expected);
        }
    }
}