package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

class WallOpeningTest {

    @Nested
    class Constructor {

        @Test
        void shouldCreateWallOpeningSuccessfully() {
            // Given
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);
            int distanceFromLeft = 1000;
            int distanceFromFloor = 500;

            // When
            WallOpening wallOpening = new WallOpening(opening, distanceFromLeft, distanceFromFloor);

            // Then
            assertThat(wallOpening.opening()).isEqualTo(opening);
            assertThat(wallOpening.distanceFromLeft()).isEqualTo(distanceFromLeft);
            assertThat(wallOpening.distanceFromFloor()).isEqualTo(distanceFromFloor);
        }
    }

    @Nested
    class Validation {

        @Test
        void shouldThrowIllegalArgumentExceptionForNegativeDistanceFromLeft() {
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);
            assertThatThrownBy(() -> new WallOpening(opening, -1, 500))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Distance from left must be non-negative");
        }

        @Test
        void shouldThrowIllegalArgumentExceptionForNegativeDistanceFromFloor() {
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);
            assertThatThrownBy(() -> new WallOpening(opening, 1000, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Distance from floor must be non-negative");
        }

        @Test
        void shouldThrowNullPointerExceptionForNullOpening() {
            assertThatThrownBy(() -> new WallOpening(null, 1000, 500))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class ComputedProperties {

        @Test
        void widthShouldReturnOpeningWidth() {
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);
            WallOpening wallOpening = new WallOpening(opening, 1000, 500);

            assertThat(wallOpening.width()).isEqualTo(opening.getWidth());
        }

        @Test
        void heightShouldReturnOpeningHeight() {
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);
            WallOpening wallOpening = new WallOpening(opening, 1000, 500);

            assertThat(wallOpening.height()).isEqualTo(opening.getHeight());
        }

        @Test
        void areaShouldReturnOpeningArea() {
            Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000); // area = 1500 * 2000 = 3000000
            WallOpening wallOpening = new WallOpening(opening, 1000, 500);

            assertThat(wallOpening.area()).isEqualTo(opening.getArea());
        }
    }
}