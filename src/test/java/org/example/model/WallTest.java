package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

class WallTest {

    @Nested
    class ConstructorValidation {

        @Test
        void shouldCreateWallSuccessfully() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 1000, 1000);
            List<Opening> openings = List.of(opening);

            // When
            Wall wall = new Wall(3000, 90, openings);

            // Then
            assertThat(wall.length()).isEqualTo(3000);
            assertThat(wall.angle()).isEqualTo(90);
            assertThat(wall.openings()).hasSize(1).contains(opening);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNonPositiveLength() {
            assertThatThrownBy(() -> new Wall(0, 90, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Length must be positive");
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForInvalidAngle() {
            assertThatThrownBy(() -> new Wall(3000, 0, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Angle must be greater than 0 and less than 360");
            assertThatThrownBy(() -> new Wall(3000, 360, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Angle must be greater than 0 and less than 360");
        }

        @Test
        void shouldReplaceNullOpeningsListWithEmptyList() {
            Wall wall = new Wall(3000, 90, null);
            assertThat(wall.openings()).isNotNull().isEmpty();
        }

        @Test
        void shouldReturnImmutableOpeningsList() {
            Opening opening = new Opening(OpeningType.WINDOW, 1000, 1000);
            List<Opening> mutableList = new java.util.ArrayList<>(List.of(opening));
            Wall wall = new Wall(3000, 90, mutableList);

            List<Opening> retrievedList = wall.openings();
            assertThatThrownBy(() -> retrievedList.add(new Opening(OpeningType.DOOR, 1000, 1000)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class ComputedProperties {

        @Test
        void totalOpeningsAreaShouldSumAreasOfAllOpenings() {
            // Given
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1500); // area = 1.8
            Opening door = new Opening(OpeningType.DOOR, 900, 2000); // area = 1.8
            Wall wall = new Wall(3000, 90, List.of(window, door));

            // When
            int totalArea = wall.totalOpeningsArea();

            // Then
            assertThat(totalArea).isEqualTo(3600000);
        }
    }

        /*@Test
        void totalOpeningsAreaShouldReturnZeroForEmptyOpeningsList() {
            Wall wall = new Wall(3.0, 90.0, List.of());

            assertThat(wall.totalOpeningsArea()).isEqualTo(0.0);
        }

        @Test
        void netAreaShouldSubtractTotalOpeningsAreaFromGrossArea() {
            // Given
            Opening window = new Opening(OpeningType.WINDOW, 1.2, 1.5);
            Opening door = new Opening(OpeningType.DOOR, 0.9, 2.0);
            Wall wall = new Wall(3.0, 90.0, List.of(window, door)); // totalOpeningsArea = 3.6
            int height = 2.5; // gross area = 3.0 * 2.5 = 7.5

            // When
            int netArea = wall.netArea(height);

            // Then
            assertThat(netArea).isEqualTo(3.9); // 7.5 - 3.6
        }
    }

/*    @Nested
    class Immutability {

        @Test
        void withOpeningShouldReturnNewWallAndKeepOriginalUnchanged() {
            // Given
            Opening initialOpening = new Opening(OpeningType.WINDOW, 1.0, 1.0);
            Wall originalWall = new Wall(3.0, 90.0, List.of(initialOpening));
            Opening newOpening = new Opening(OpeningType.DOOR, 0.9, 2.0);

            // When
            Wall updatedWall = originalWall.withOpening(newOpening);

            // Then
            assertThat(updatedWall.length()).isEqualTo(originalWall.length());
            assertThat(updatedWall.angle()).isEqualTo(originalWall.angle());
            assertThat(updatedWall.openings()).hasSize(2).contains(initialOpening, newOpening);

            assertThat(originalWall.openings()).hasSize(1).containsExactly(initialOpening);
        }

        @Test
        void withOpeningShouldThrowNpeForNullOpening() {
            Wall wall = new Wall(3.0, 90.0, List.of());
            assertThatThrownBy(() -> wall.withOpening(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void withoutOpeningShouldRemoveOpeningByIdAndKeepOriginalUnchanged() {
            // Given
            UUID idToRemove = UUID.randomUUID();
            Opening openingToRemove = new Opening(OpeningType.WINDOW, 1.0, 1.0, idToRemove);
            Opening otherOpening = new Opening(OpeningType.DOOR, 0.9, 2.0);
            Wall originalWall = new Wall(3.0, 90.0, List.of(otherOpening, openingToRemove));

            // When
            Wall updatedWall = originalWall.withoutOpening(idToRemove);

            // Then
            assertThat(updatedWall.openings()).hasSize(1).contains(otherOpening);
            assertThat(originalWall.openings()).hasSize(2).contains(openingToRemove, otherOpening);
        }

        @Test
        void withoutOpeningShouldThrowNoSuchElementExceptionIfIdNotFound() {
            Wall wall = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            UUID nonExistentId = UUID.randomUUID();

            assertThatThrownBy(() -> wall.withoutOpening(nonExistentId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class Equality {

        @Test
        void equalWallsShouldHaveSameHashCode() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1.0, 1.0);
            Opening opening2 = new Opening(OpeningType.DOOR, 0.9, 2.0);
            Wall wall1 = new Wall(3.0, 90.0, List.of(opening1, opening2));
            Wall wall2 = new Wall(3.0, 90.0, List.of(opening1, opening2));

            assertThat(wall1).isEqualTo(wall2);
            assertThat(wall1.hashCode()).isEqualTo(wall2.hashCode());
        }

        @Test
        void unequalWallsShouldHaveDifferentHashCodes() {
            Wall wall1 = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            Wall wall2 = new Wall(4.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));

            assertThat(wall1).isNotEqualTo(wall2);
        }

        @Test
        void wallShouldBeEqualToItself() {
            Wall wall = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            assertThat(wall).isEqualTo(wall);
        }

        @Test
        void wallShouldNotBeEqualToNull() {
            Wall wall = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            assertThat(wall).isNotEqualTo(null);
        }

        @Test
        void wallShouldNotBeEqualToDifferentType() {
            Wall wall = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            assertThat(wall).isNotEqualTo("not a wall");
        }

        @Test
        void wallShouldNotBeEqualToAnotherWithDifferentLength() {
            Wall wall1 = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            Wall wall2 = new Wall(4.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            assertThat(wall1).isNotEqualTo(wall2);
        }

        @Test
        void wallShouldNotBeEqualToAnotherWithDifferentAngle() {
            Wall wall1 = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            Wall wall2 = new Wall(3.0, 180.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            assertThat(wall1).isNotEqualTo(wall2);
        }

        @Test
        void wallShouldNotBeEqualToAnotherWithDifferentOpenings() {
            Wall wall1 = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.WINDOW, 1.0, 1.0)));
            Wall wall2 = new Wall(3.0, 90.0, List.of(new Opening(OpeningType.DOOR, 1.0, 1.0)));
            assertThat(wall1).isNotEqualTo(wall2);
        }
    }

    @Nested
    class ToString {

        @Test
        void toStringShouldContainLengthAngleAndOpeningsCount() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1.0, 1.0);
            Opening opening2 = new Opening(OpeningType.DOOR, 0.9, 2.0);
            Wall wall = new Wall(3.0, 90.0, List.of(opening1, opening2));

            String toString = wall.toString();

            assertThat(toString).isNotNull()
                    .contains("3.0")
                    .contains("90.0")
                    .contains("2"); // количество проёмов
        }
    }*/
}