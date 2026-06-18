package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

class WallTest {

    @Nested
    class ConstructorAndValidation {

        @Test
        void shouldCreateWallWithEmptyOpeningsList() {
            Wall wall = new Wall(5000);
            assertThat(wall.getLength()).isEqualTo(5000);
            assertThat(wall.getWallOpenings()).isEmpty();
        }

        @Test
        void shouldCreateWallWithProvidedOpeningsList() {
            Opening opening = new Opening(OpeningType.WINDOW, 1000, 1000);
            WallOpening wallOpening = new WallOpening(opening, 1000, 0);
            List<WallOpening> openings = List.of(wallOpening);

            Wall wall = new Wall(5000, openings);

            assertThat(wall.getLength()).isEqualTo(5000);
            assertThat(wall.getWallOpenings()).hasSize(1).contains(wallOpening);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNonPositiveLength() {
            assertThatThrownBy(() -> new Wall(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldReplaceNullOpeningsListWithEmptyList() {
            Wall wall = new Wall(5000, null);
            assertThat(wall.getWallOpenings()).isNotNull().isEmpty();
        }

        @Test
        void shouldReturnImmutableOpeningsList() {
            Opening opening = new Opening(OpeningType.WINDOW, 1000, 1000);
            WallOpening wallOpening = new WallOpening(opening, 1000, 0);
            List<WallOpening> mutableList = new java.util.ArrayList<>(List.of(wallOpening));
            Wall wall = new Wall(5000, mutableList);

            List<WallOpening> retrievedList = wall.getWallOpenings();
            assertThatThrownBy(() -> retrievedList.add(new WallOpening(opening, 2000, 0)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionIfAnyOpeningExceedsWallLength() {
            Opening opening = new Opening(OpeningType.WINDOW, 3000, 1000);
            WallOpening wallOpening = new WallOpening(opening, 2100, 0); // 2100 + 3000 = 5100 > 5000
            List<WallOpening> openings = List.of(wallOpening);

            assertThatThrownBy(() -> new Wall(5000, openings))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wall opening width and distanceFromLeft is too big");
        }
    }

    @Nested
    class WithOpeningMethod {

        @Test
        void shouldAddOpeningAndReturnNewWallKeepingOriginalUnchanged() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1000, 1000);
            WallOpening wallOpening1 = new WallOpening(opening1, 1000, 0);
            Wall originalWall = new Wall(5000, List.of(wallOpening1));

            Opening opening2 = new Opening(OpeningType.DOOR, 1000, 2000);
            WallOpening wallOpening2 = new WallOpening(opening2, 3000, 0);

            Wall updatedWall = originalWall.withOpening(wallOpening2);

            assertThat(updatedWall.getLength()).isEqualTo(originalWall.getLength());
            assertThat(updatedWall.getWallOpenings()).hasSize(2).contains(wallOpening1, wallOpening2);

            assertThat(originalWall.getWallOpenings()).hasSize(1).containsExactly(wallOpening1);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionIfNewOpeningExceedsWallLength() {
            Wall wall = new Wall(5000);
            Opening opening = new Opening(OpeningType.WINDOW, 3000, 1000);
            WallOpening wallOpening = new WallOpening(opening, 2100, 0); // 2100 + 3000 = 5100 > 5000

            assertThatThrownBy(() -> wall.withOpening(wallOpening))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wall opening width and distanceFromLeft is too big");
        }

        @Test
        void shouldThrowNpeIfNewOpeningIsNull() {
            Wall wall = new Wall(5000);
            assertThatThrownBy(() -> wall.withOpening(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void shouldChainMultipleWithOpeningCalls() {
            Wall wall = new Wall(10000);
            Opening opening1 = new Opening(OpeningType.WINDOW, 1000, 1000);
            WallOpening wallOpening1 = new WallOpening(opening1, 1000, 0);
            Opening opening2 = new Opening(OpeningType.DOOR, 2000, 2000);
            WallOpening wallOpening2 = new WallOpening(opening2, 5000, 0);

            Wall finalWall = wall.withOpening(wallOpening1).withOpening(wallOpening2);

            assertThat(finalWall.getWallOpenings()).hasSize(2).contains(wallOpening1, wallOpening2);
        }
    }

    @Nested
    class WithoutOpeningMethod {

        /*@Test
        void shouldRemoveOpeningByIdAndReturnNewWallKeepingOriginalUnchanged() {
            UUID idToRemove = UUID.randomUUID();
            Opening openingToRemove = new Opening(OpeningType.WINDOW, 1000, 1000, idToRemove);
            WallOpening wallOpeningToRemove = new WallOpening(openingToRemove, 1000, 0);
            Opening otherOpening = new Opening(OpeningType.DOOR, 1000, 2000);
            WallOpening otherWallOpening = new WallOpening(otherOpening, 3000, 0);

            Wall originalWall = new Wall(5000, List.of(wallOpeningToRemove, otherWallOpening));

            Wall updatedWall = originalWall.withoutOpening(idToRemove);

            assertThat(updatedWall.openings()).hasSize(1).contains(otherWallOpening);
            assertThat(originalWall.openings()).hasSize(2).contains(wallOpeningToRemove, otherWallOpening);
        }*/

        /*@Test
        void shouldThrowNoSuchElementExceptionIfIdNotFound() {
            UUID nonExistentId = UUID.randomUUID();
            Wall wall = new Wall(5000);

            assertThatThrownBy(() -> wall.withoutOpening(nonExistentId))
                    .isInstanceOf(NoSuchElementException.class);
        }*/
    }

    @Nested
    class ComputedProperties {

        @Test
        void totalOpeningsAreaShouldSumAreasOfAllOpenings() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1200, 1500); // area = 1800000
            Opening opening2 = new Opening(OpeningType.DOOR, 900, 2000); // area = 1800000
            WallOpening wallOpening1 = new WallOpening(opening1, 1000, 0);
            WallOpening wallOpening2 = new WallOpening(opening2, 3000, 0);
            Wall wall = new Wall(5000, List.of(wallOpening1, wallOpening2));

            double totalArea = wall.totalOpeningsArea();

            assertThat(totalArea).isEqualTo(3600000); // 1800000 + 1800000
        }

        @Test
        void netAreaShouldSubtractTotalOpeningsAreaFromGrossArea() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1200, 1500); // area = 1800000
            Opening opening2 = new Opening(OpeningType.DOOR, 900, 2000); // area = 1800000
            WallOpening wallOpening1 = new WallOpening(opening1, 1000, 0);
            WallOpening wallOpening2 = new WallOpening(opening2, 3000, 0);
            Wall wall = new Wall(5000, List.of(wallOpening1, wallOpening2)); // totalOpeningsArea = 3600000
            // gross area = 5000 * 2500 = 12500000

            double netArea = wall.netArea();

            assertThat(netArea).isEqualTo(8900000); // 12500000 - 3600000
        }
    }

    @Nested
    class EqualityAndHashCode {

        @Test
        void equalWallsShouldHaveSameHashCode() {
            Opening opening1 = new Opening(OpeningType.WINDOW, 1000, 1000);
            WallOpening wallOpening1 = new WallOpening(opening1, 1000, 0);
            Opening opening2 = new Opening(OpeningType.DOOR, 1000, 2000);
            WallOpening wallOpening2 = new WallOpening(opening2, 3000, 0);

            Wall wall1 = new Wall(5000, List.of(wallOpening1, wallOpening2));
            Wall wall2 = new Wall(5000, List.of(wallOpening1, wallOpening2));

            assertThat(wall1).isEqualTo(wall2);
            assertThat(wall1.hashCode()).isEqualTo(wall2.hashCode());
        }

        @Test
        void unequalWallsShouldNotBeEqual() {
            Wall wall1 = new Wall(5000, List.of());
            Wall wall2 = new Wall(6000, List.of());
            Wall wall3 = new Wall(5000, List.of(new WallOpening(new Opening(OpeningType.WINDOW, 1000, 1000), 1000, 0)));

            assertThat(wall1).isNotEqualTo(wall2);
            assertThat(wall1).isNotEqualTo(wall3);
        }
    }
}