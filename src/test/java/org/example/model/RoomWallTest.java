package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class RoomWallTest {

    @Nested
    class Constructor {

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

            // When
            RoomWall room = new RoomWall(name, height, walls);

            // Then
            assertThat(room.getName()).isEqualTo(name);
            assertThat(room.getHeight()).isEqualTo(height);
            assertThat(room.getWalls()).hasSize(4);
            List<Wall> retrievedWalls = room.getWalls();
            assertThatThrownBy(() -> retrievedWalls.add(new Wall(1000)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void shouldThrowIllegalArgumentExceptionForNonPositiveHeight() {
            assertThatThrownBy(() -> new RoomWall("Test Room", 0, List.of(new Wall(1000))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNullWalls() {
            assertThatThrownBy(() -> new RoomWall("Test Room", 2500, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForEmptyWallsList() {
            assertThatThrownBy(() -> new RoomWall("Test Room", 2500, List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionIfWallsListContainsNullElement() {
            List<Wall> wallsWithNull = Arrays.asList(new Wall(1000), null);
            assertThatThrownBy(() -> new RoomWall("Test Room", 2500, wallsWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class WallAreaCalculations {

        @Test
        void totalWallAreaShouldSumAreasOfAllWalls() {
            // Given
            int height = 2500;
            List<Wall> walls = List.of(
                    new Wall(300),
                    new Wall(4000),
                    new Wall(3000),
                    new Wall(4000)
            );
            RoomWall room = new RoomWall("Test Room", height, walls);

            // When
            int totalArea = room.getTotalWallArea();

            // Then
            assertThat(totalArea).isEqualTo(35000000);
        }

        @Test
        void totalOpeningsAreaShouldSumAreasOfAllOpeningsInAllWalls() {
            // Given
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1500); // area = 1800000
            Opening door = new Opening(OpeningType.DOOR, 900, 2000); // area = 1800000
            List<Wall> walls = List.of(
                    new Wall(3000),
                    new Wall(4000),
                    new Wall(3000),
                    new Wall(4000)
            );
            RoomWall room = new RoomWall("Test Room", 2500, walls);

            // When
            int totalOpeningsArea = room.getTotalOpeningsArea();

            // Then
            assertThat(totalOpeningsArea).isEqualTo(3600000); // 1800000 + 1800000
        }

        @Test
        void netWallAreaShouldSubtractTotalOpeningsAreaFromTotalWallArea() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 600, 600); // area = 3600
            List<Wall> walls = List.of(
                    new Wall(1000), // wall area = 1000 * 10 = 10000, net = 9964
                    new Wall(1000),
                    new Wall(1000),
                    new Wall(1000)
            );
            RoomWall room = new RoomWall("Test Room", 10000, walls); // height = 10

            // When
            double netWallArea = room.netWallArea();

            // Then
            assertThat(netWallArea).isEqualTo(39640000); // totalWallArea=40000, totalOpeningsArea=3600
        }
    }
}