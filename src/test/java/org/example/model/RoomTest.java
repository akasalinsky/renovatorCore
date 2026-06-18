package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class RoomTest {

    @Nested
    class ConstructorAndGetters {

        @Test
        void shouldCreateRoomSuccessfully() {
            // Given
            String name = "Test Room";
            int height = 2500;
            List<Wall> walls = List.of(
                    new Wall(3000),
                    new Wall(4000),
                    new Wall(3000),
                    new Wall(4000)
            );
            List<Integer> angles = List.of(90, 90, 90, 90);

            // When
            Room room = new Room(name, height, walls, angles);

            // Then
            assertThat(room.getName()).isEqualTo(name);
            assertThat(room.getHeight()).isEqualTo(height);

            List<Wall> retrievedWalls = room.getWalls();
            assertThat(retrievedWalls).hasSize(4);
            assertThatThrownBy(() -> retrievedWalls.add(new Wall(1000)))
                    .isInstanceOf(UnsupportedOperationException.class);

            List<Integer> retrievedAngles = room.getAngles();
            assertThat(retrievedAngles).hasSize(4).containsExactlyElementsOf(angles);
            assertThatThrownBy(() -> retrievedAngles.add(100))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void shouldThrowIllegalArgumentExceptionForNonPositiveHeight() {
            List<Wall> walls = List.of(new Wall(1000));
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
            List<Wall> walls = List.of(new Wall(1000));
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
            List<Wall> walls = List.of(new Wall(1000), new Wall(2000));
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
            List<Wall> wallsWithNull = Arrays.asList(new Wall(1000), null);
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
                    new Wall(3000), // netArea(height) = 3000 * 2500 = 7500000
                    new Wall(4000), // netArea(height) = 4000 * 2500 = 10000000
                    new Wall(3000), // netArea(height) = 3000 * 2500 = 7500000
                    new Wall(4000)  // netArea(height) = 4000 * 2500 = 10000000
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 2500, walls, angles);

            // When
            int totalArea = room.totalWallArea();

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
                    new Wall(3000, List.of(wallOpening1)), // totalOpeningsArea = 1800000
                    new Wall(4000, List.of(wallOpening2)), // totalOpeningsArea = 1800000
                    new Wall(3000, List.of()), // totalOpeningsArea = 0
                    new Wall(4000, List.of())  // totalOpeningsArea = 0
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 2500, walls, angles);

            // When
            double totalOpeningsArea = room.totalOpeningsArea();

            // Then
            assertThat(totalOpeningsArea).isEqualTo(3600000); // 1800000 + 1800000
        }

        @Test
        void netWallAreaShouldSubtractTotalOpeningsAreaFromTotalWallArea() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 600, 600); // area = 360000
            WallOpening wallOpening = new WallOpening(opening, 100, 0);
            List<Wall> walls = List.of(
                    new Wall(1000, List.of(wallOpening)), // wall area = 1000 * 1000 = 1000000, totalOpeningsArea = 360000, net = 640000
                    new Wall(1000, List.of()), // net = 1000000
                    new Wall(1000, List.of()), // net = 1000000
                    new Wall(1000, List.of())  // net = 1000000
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Test Room", 1000, walls, angles); // height = 1000

            // When
            double netWallArea = room.netWallArea();

            // Then
            assertThat(netWallArea).isEqualTo(3640000); // totalWallArea=4000000, totalOpeningsArea=360000
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
                    new Wall(1000, List.of(wallOpening)),
                    new Wall(2000, List.of())
            );
            List<Integer> angles = List.of(180, 180);
            Room room = new Room("Test Room", 1000, walls, angles);

            // When
            String description = room.describe();

            // Then
            assertThat(description).contains("1000") // height
                    .contains("2")    // walls count
                    .contains("360000"); // total openings area
        }
    }
}


