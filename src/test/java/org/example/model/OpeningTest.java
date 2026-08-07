package org.example.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OpeningTest {

    @Test
    void shouldCreateOpeningWithCorrectParameters() {
        // Given
        OpeningType type = OpeningType.WINDOW;
        int width = 1500;
        int height = 2000;

        // When
        Opening opening = new Opening(type, width, height);

        // Then
        assertThat(opening.type()).isEqualTo(type);
        assertThat(opening.width()).isEqualTo(width);
        assertThat(opening.height()).isEqualTo(height);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenWidthIsZeroOrNegative() {
        // Given
        OpeningType type = OpeningType.DOOR;
        int invalidWidth = -500; // or 0.0
        int height = 2000;

        // When / Then
        assertThatThrownBy(() -> new Opening(type, invalidWidth, height))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot be negative");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenHeightIsZeroOrNegative() {
        // Given
        OpeningType type = OpeningType.WINDOW;
        int width = 1500;
        int invalidHeight = -1000;

        // When / Then
        assertThatThrownBy(() -> new Opening(type, width, invalidHeight))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot be negative");
    }

    @Test
    void areaShouldReturnWidthMultipliedByHeight() {
        // Given
        Opening opening = new Opening(OpeningType.WINDOW, 1500, 2000);

        // When
        int calculatedArea = opening.getArea();

        // Then
        assertThat(calculatedArea).isEqualTo(1500 * 2000);
    }

    @Test
    void equalOpeningsShouldBeEqual() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1500, 2000);
        Opening opening2 = new Opening(OpeningType.WINDOW, 1500, 2000);

        // Then
        assertThat(opening1).isEqualTo(opening2);
        assertThat(opening1.hashCode()).isEqualTo(opening2.hashCode());
    }

    @Test
    void differentOpeningsShouldNotBeEqual() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1500, 2000);
        Opening opening2 = new Opening(OpeningType.DOOR, 1500, 2000);   // другой тип
        Opening opening3 = new Opening(OpeningType.WINDOW, 2000, 2000); // другая ширина
        Opening opening4 = new Opening(OpeningType.WINDOW, 1500, 2500); // другая высота

        // Then
        assertThat(opening1).isNotEqualTo(opening2);
        assertThat(opening1).isNotEqualTo(opening3);
        assertThat(opening1).isNotEqualTo(opening4);
    }

    @Test
    void hashCodeShouldBeConsistentWithEquals() {
        // Given
        Opening opening1 = new Opening(OpeningType.WINDOW, 1500, 2000);
        Opening opening2 = new Opening(OpeningType.WINDOW, 1500, 2000);

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
        int width = 1500;
        int height = 2000;
        Opening opening = new Opening(type, width, height);

        // When
        String openingString = opening.toString();

        // Then
        assertThat(openingString).contains(type.toString())
                .contains(String.valueOf(width))
                .contains(String.valueOf(height));
    }
}
