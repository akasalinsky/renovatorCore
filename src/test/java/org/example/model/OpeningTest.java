package org.example.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OpeningTest {

    @Test
    void shouldCreateOpeningWithCorrectParameters() {
        // Given
        OpeningType type = OpeningType.WINDOW;
        double width = 1.5;
        double height = 2.0;

        // When
        Opening opening = new Opening(type, width, height);

        // Then
        assertThat(opening.getType()).isEqualTo(type);
        assertThat(opening.getWidth()).isEqualTo(width);
        assertThat(opening.getHeight()).isEqualTo(height);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenWidthIsZeroOrNegative() {
        // Given
        OpeningType type = OpeningType.DOOR;
        double invalidWidth = -0.5; // or 0.0
        double height = 2.0;

        // When / Then
        assertThatThrownBy(() -> new Opening(type, invalidWidth, height))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width must be positive");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenHeightIsZeroOrNegative() {
        // Given
        OpeningType type = OpeningType.WINDOW;
        double width = 1.5;
        double invalidHeight = 0.0; // or -1.0

        // When / Then
        assertThatThrownBy(() -> new Opening(type, width, invalidHeight))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height must be positive");
    }

    @Test
    void areaShouldReturnWidthMultipliedByHeight() {
        // Given
        Opening opening = new Opening(OpeningType.WINDOW, 1.5, 2.0);

        // When
        double calculatedArea = opening.area();

        // Then
        assertThat(calculatedArea).isEqualTo(1.5 * 2.0);
    }

    @Test
    void equalOpeningsShouldBeEqual() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1.5, 2.0);
        Opening opening2 = new Opening(OpeningType.WINDOW, 1.5, 2.0);

        // Then
        assertThat(opening1).isEqualTo(opening2);
        assertThat(opening1.hashCode()).isEqualTo(opening2.hashCode());
    }

    @Test
    void differentOpeningsShouldNotBeEqual() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1.5, 2.0);
        Opening opening2 = new Opening(OpeningType.DOOR, 1.5, 2.0);   // другой тип
        Opening opening3 = new Opening(OpeningType.WINDOW, 2.0, 2.0); // другая ширина
        Opening opening4 = new Opening(OpeningType.WINDOW, 1.5, 2.5); // другая высота

        // Then
        assertThat(opening1).isNotEqualTo(opening2);
        assertThat(opening1).isNotEqualTo(opening3);
        assertThat(opening1).isNotEqualTo(opening4);
    }

    @Test
    void hashCodeShouldBeConsistentWithEquals() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1.5, 2.0);
        Opening opening2 = new Opening(OpeningType.WINDOW, 1.5, 2.0);

        // When
        int hash1 = opening1.hashCode();
        int hash2 = opening2.hashCode();

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void toStringShouldContainTypeWidthAndHeight() {
        // Given
        OpeningType type = OpeningType.WINDOW;
        double width = 1.5;
        double height = 2.0;
        Opening opening = new Opening(type, width, height);

        // When
        String openingString = opening.toString();

        // Then
        assertThat(openingString).contains(type.toString())
                .contains(String.valueOf(width))
                .contains(String.valueOf(height));
    }
}
