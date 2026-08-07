package org.example.model;

import org.example.service.RoomCalculationService;
import org.example.service.RoomRenderingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RoomTest {
    private RoomCalculationService roomCalculationService;
    private RoomRenderingService roomRenderingService;
    @BeforeEach
    void setUp() {
        // Вот здесь создаются объекты перед каждым тестом
        roomRenderingService = new RoomRenderingService();
        roomCalculationService = new RoomCalculationService();
    }
    @Nested
    class ConstructorAndGetters {

        @Test
        void shouldCreateRoomSuccessfully() {
            // Given
            String name = "Test Room";
            int height = 2500;
            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0),
                    new Wall(4000, 2500, 1),
                    new Wall(3000, 2500, 2),
                    new Wall(4000, 2500, 3)
            );
            List<Integer> angles = List.of(90, 90, 90, 90);

            // When
            Room room = new Room(name, height, walls, angles);

            // Then
            assertThat(room.name()).isEqualTo(name);
            assertThat(room.height()).isEqualTo(height);

            List<Wall> retrievedWalls = room.walls();
            assertThat(retrievedWalls).hasSize(4);
            assertThatThrownBy(() -> retrievedWalls.add(new Wall(1000, 2500, 0)))
                    .isInstanceOf(UnsupportedOperationException.class);

            List<Integer> retrievedAngles = room.angles();
            assertThat(retrievedAngles).hasSize(4).containsExactlyElementsOf(angles);
            assertThatThrownBy(() -> retrievedAngles.add(100))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void shouldThrowIllegalArgumentExceptionForNonPositiveHeight() {
            List<Wall> walls = List.of(new Wall(1000, 2500, 0));
            List<Integer> angles = List.of(360);
            assertThatThrownBy(() -> new Room("Test", 0, walls, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNullWalls() {
            List<Integer> angles = List.of(360);
            assertThatThrownBy(() -> new Room("Test", 1000, null, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForEmptyWalls() {
            List<Wall> walls = List.of();
            List<Integer> angles = List.of();
            assertThatThrownBy(() -> new Room("Test", 1000, walls, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNullAngles() {
            List<Wall> walls = List.of(new Wall(1000, 2500, 0));
            assertThatThrownBy(() -> new Room("Test", 1000, walls, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        /*@Test
        void shouldThrowIllegalArgumentExceptionIfAnglesSizeDoesNotMatchWallsSize() {
            List<Wall> walls = List.of(new Wall(1000), new Wall(2000));
            List<Integer> angles = List.of(90); // только один угол для двух стен
            assertThatThrownBy(() -> new Room("Test", 1000, walls, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }*/

        @Test
        void shouldThrowIllegalArgumentExceptionIfAnyAngleIsInvalid() {
            List<Wall> walls = List.of(new Wall(1000, 2500, 0), new Wall(2000, 2500, 1));
            List<Integer> angles = List.of(0, 360); // оба угла неверны
            assertThatThrownBy(() -> new Room("Test", 1000, walls, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }

       /* @Test
        void shouldThrowIllegalArgumentExceptionIfSumOfAnglesIsNot360() {
            List<Wall> walls = List.of(new Wall(1000), new Wall(2000), new Wall(1000), new Wall(2000));
            List<Integer> angles = List.of(90, 90, 90, 89); // сумма = 359
            assertThatThrownBy(() -> new Room("Test", 1000, walls, angles))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("angles must sum to 360");
        }*/

        @Test
        void shouldThrowIllegalArgumentExceptionIfWallsListContainsNullElement() {
            List<Wall> wallsWithNull = Arrays.asList(new Wall(1000, 2500, 0), null);
            List<Integer> angles = List.of(180, 180);
            assertThatThrownBy(() -> new Room("Test", 1000, wallsWithNull, angles))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class CalculatedProperties {

        @Test
        void totalWallAreaShouldSumAreasOfAllWalls() {
            // Given
            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0), // netArea(height) = 3000 * 2500 = 7500000
                    new Wall(4000, 2500, 1), // netArea(height) = 4000 * 2500 = 10000000
                    new Wall(3000, 2500, 2), // netArea(height) = 3000 * 2500 = 7500000
                    new Wall(4000, 2500, 3)  // netArea(height) = 4000 * 2500 = 10000000
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 2500, walls, angles);

            // When
            int totalArea = roomCalculationService.getTotalWallArea(room);

            // Then
            assertThat(totalArea).isEqualTo(35000000); // (3000+4000+3000+4000) * 2500
        }

        @Test
        void totalOpeningsAreaShouldSumAreasOfAllOpeningsInAllWalls() {
            // Given
            Opening opening1 = new Opening(OpeningType.WINDOW, 1200, 1500); // area = 1800000
            WallOpening wallOpening1 = new WallOpening(opening1, 100, 0);
            Opening opening2 = new Opening(OpeningType.DOOR, 900, 2000); // area = 1800000
            WallOpening wallOpening2 = new WallOpening(opening2, 200, 0);

            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0, List.of(wallOpening1)), // totalOpeningsArea = 1800000
                    new Wall(4000, 2500, 1, List.of(wallOpening2)), // totalOpeningsArea = 1800000
                    new Wall(3000, 2500, 2, List.of()), // totalOpeningsArea = 0
                    new Wall(4000, 2500, 3, List.of())  // totalOpeningsArea = 0
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 2500, walls, angles);

            // When
            double totalOpeningsArea = roomCalculationService.getTotalOpeningsArea(room);

            // Then
            assertThat(totalOpeningsArea).isEqualTo(3600000); // 1800000 + 1800000
        }

        @Test
        void netWallAreaShouldSubtractTotalOpeningsAreaFromTotalWallArea() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 600, 600);
            WallOpening wallOpening = new WallOpening(opening, 100, 0);
            List<Wall> walls = List.of(
                    new Wall(1000, 2500, 0, List.of(wallOpening)),
                    new Wall(1000, 2500, 1, List.of()),
                    new Wall(1000, 2500, 2, List.of()),
                    new Wall(1000, 2500, 3, List.of())
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 2500, walls, angles);

            // When
            double netWallArea = roomCalculationService.getNetWallArea(room);

            // Then
            assertThat(netWallArea).isEqualTo(9640000); // totalWallArea=4000000, totalOpeningsArea=360000
        }
    }

    @Nested
    class DescribeMethod {

        @Test
        void describeShouldContainHeightWallsCountAndTotalOpeningsArea() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 600, 600); // area = 360000
            WallOpening wallOpening = new WallOpening(opening, 100, 0);
            List<Wall> walls = List.of(
                    new Wall(1000, 2500, 0, List.of(wallOpening)),
                    new Wall(2000, 2500, 1, List.of())
            );
            List<Integer> angles = List.of(180, 180);
            Room room = new Room("Test Room", 1000, walls, angles);

            // When
            String description = roomRenderingService.describe(room, roomCalculationService);

            // Then
            assertThat(description).contains("1000") // height
                    .contains("2")    // walls count
                    .contains("360000"); // total openings area
        }
    }

    @Test
    void shouldAddWallOpeningInRoom(){
        Opening opening = new Opening(OpeningType.WINDOW, 600, 600);
        WallOpening wallOpening = new WallOpening(opening, 100, 0);
        List<Wall> walls = List.of(
                new Wall(1000, 2700, 0, List.of()),
                new Wall(2000, 2700, 1, List.of())
        );
        List<Integer> angles = List.of(180, 180);
        Room originalRoom = new Room("Test Room", 1000, walls, angles);

        // When
        Room updatedRoom = originalRoom.withOpening(1, wallOpening);

        List<Wall> list = new ArrayList<>();
        for(Wall wall: updatedRoom.walls()){
            if(wall.getCounter() == 1) {list.add(wall);}
        }

        assertThat(list.getFirst().getWallOpenings())
                .contains(wallOpening);
    }
}


