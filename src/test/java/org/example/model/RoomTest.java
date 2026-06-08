package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {


    @Test
    void shouldCreateRoomWithCorrectParameters() {
        // Given
        String name = "Тестовая комната";
        double length = 3.0;
        double width = 4.0;
        double height = 2.5;
        double openingsArea = 0.0;

        // When
        Room room = new Room(name, length, width, height, openingsArea);

        // Then
        assertThat(room.getLength()).isEqualTo(length);
        assertThat(room.getWidth()).isEqualTo(width);
        assertThat(room.getHeight()).isEqualTo(height);
        assertThat(room.getOpeningsArea()).isEqualTo(openingsArea);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.0})
    void shouldThrowIllegalArgumentExceptionWhenLengthIsZeroOrNegative(double invalidLength) {

        assertThatThrownBy(() ->
                new Room("Test Room", invalidLength, 4.0, 2.5, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Length must be positive");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.0})
    void shouldThrowIllegalArgumentExceptionWhenWidthIsZeroOrNegative(double invalidWidth) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3.0, invalidWidth, 2.5, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width must be positive");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.0})
    void shouldThrowIllegalArgumentExceptionWhenHeightIsZeroOrNegative(double invalidHeight) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3.0, 2.5, invalidHeight, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height must be positive");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -100.0})
    void shouldThrowIllegalArgumentExceptionWhenOpeningsAreaIsZeroOrNegative(double invalidOpeningsArea) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3.0, 2.5, 2.5, invalidOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot be negative");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 10.0, 34.9999, 35.0}) // Включая случай с ровно 35
    void shouldAllowCreationWhenOpeningsAreaIsWithinValidRange(double openingsArea) {
        // Given
        double length = 3.0;
        double width = 4.0;
        double height = 2.5;
        // Total wall area = 2 * (length + width) * height = 2 * (3 + 4) * 2.5 = 35

        // When / Then
        // Ожидаем, что исключение НЕ будет выброшено
        assertDoesNotThrow(() ->
                new Room("Test Room", length, width, height, openingsArea));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOpeningsAreaExceedsTotalWallArea() {
        // Given
        double length = 3.0;
        double width = 4.0;
        double height = 2.5;
        double invalidOpeningsArea = 35.0001;
        // Total wall area = 2 * (length + width) * height = 2 * (3 + 4) * 2.5 = 35

        // When / Then
        assertThatThrownBy(() ->
                new Room("Test Room", length, width, height, invalidOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot exceed total wall area");
    }

    @ParameterizedTest
    @MethodSource("floorAreaTestData")
    void shouldCalculateFloorAreaCorrectly(double length, double width, double expectedFloorArea) {
        // Given
        double height = 2.5;
        double openingsArea = 0.0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getFloorArea()).isEqualTo(expectedFloorArea);
    }

    static Stream<Arguments> floorAreaTestData() {
        return Stream.of(
                Arguments.of(3.0, 4.0, 12.0), // 3 * 4 = 12
                Arguments.of(5.0, 5.0, 25.0), // 5 * 5 = 25
                Arguments.of(2.0, 6.0, 12.0), // 2 * 6 = 12
                Arguments.of(10.0, 1.5, 15.0) // 10 * 1.5 = 15
        );
    }

    @ParameterizedTest
    @MethodSource("volumeTestData")
    void shouldCalculateVolumeCorrectly(double length, double width, double height, double expectedVolume) {
        // Given
        double openingsArea = 0.0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getVolume()).isEqualTo(expectedVolume);
    }

    static Stream<Arguments> volumeTestData() {
        return Stream.of(
                Arguments.of(3.0, 4.0, 2.5, 30.0), // 3 * 4 * 2.5 = 30
                Arguments.of(1.0, 1.0, 1.0, 1.0)   // 1 * 1 * 1 = 1
        );
    }

    @ParameterizedTest
    @MethodSource("totalWallAreaTestData")
    void shouldCalculateTotalWallAreaCorrectly(double length, double width, double height, double expectedTotalWallArea) {
        // Given
        double openingsArea = 0.0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getTotalWallArea()).isEqualTo(expectedTotalWallArea);
    }

    static Stream<Arguments> totalWallAreaTestData() {
        return Stream.of(
                Arguments.of(3.0, 4.0, 2.5, 35.0), // 2 * (3 + 4) * 2.5 = 35
                Arguments.of(1.0, 1.0, 1.0, 4.0)   // 2 * (1 + 1) * 1 = 4
        );
    }

    @ParameterizedTest
    @MethodSource("netWallAreaTestData")
    void shouldCalculateNetWallAreaCorrectly(double length, double width, double height, double openingsArea, double expectedNetWallArea) {
        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getNetWallArea()).isEqualTo(expectedNetWallArea);
    }

    static Stream<Arguments> netWallAreaTestData() {
        double length = 3.0;
        double width = 4.0;
        double height = 2.5;
        double totalWallArea = 2 * (length + width) * height; // 35.0

        return Stream.of(
                Arguments.of(length, width, height, 5.0, 30.0),      // 35 - 5 = 30
                Arguments.of(length, width, height, 0.0, 35.0),     // 35 - 0 = 35 (totalWallArea)
                Arguments.of(length, width, height, 35.0, 0.0)      // 35 - 35 = 0
        );
    }

    @Test
    void shouldReturnNewRoomWithUpdatedOpeningsAreaAndKeepOriginalUnchanged() {
        // Given
        Room originalRoom = new Room("Test Room", 3.0, 4.0, 2.5, 5.0); // проёмы = 5

        // When
        Room updatedRoom = originalRoom.withOpeningsArea(10.0);

        // Then
        assertThat(updatedRoom.getOpeningsArea()).isEqualTo(10.0);
        assertThat(originalRoom.getOpeningsArea()).isEqualTo(5.0); // оригинальный объект не изменился
        assertThat(updatedRoom.getName()).isEqualTo(originalRoom.getName()); // имя не изменилось
        assertThat(updatedRoom.getLength()).isEqualTo(originalRoom.getLength()); // длина не изменилась
        assertThat(updatedRoom.getWidth()).isEqualTo(originalRoom.getWidth()); // ширина не изменилась
        assertThat(updatedRoom.getHeight()).isEqualTo(originalRoom.getHeight()); // высота не изменилась
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNewOpeningsAreaExceedsTotalWallAreaAndKeepOriginalUnchanged() {
        // Given
        Room originalRoom = new Room("Test Room", 3.0, 4.0, 2.5, 5.0); // проёмы = 5, площадь стен = 35
        double invalidNewOpeningsArea = 35.0001; // больше, чем площадь стен

        // When / Then
        assertThatThrownBy(() -> originalRoom.withOpeningsArea(invalidNewOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot exceed total wall area");

        // Ensure original object is unchanged
        assertThat(originalRoom.getOpeningsArea()).isEqualTo(5.0);
    }

    @Test
    void describeShouldContainAllDimensionValues() {
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, 5.0);

        String description = room.describe();

        assertThat(description).isNotEmpty()
                .contains("3.0")
                .contains("4.0")
                .contains("2.5")
                .contains("5.0");
    }

    @Test
    void describeShouldContainAllDimensionValuesWithOpeningList() {
        List<Opening> openings = List.of(new Opening(OpeningType.DOOR, 0.8, 2.2), new Opening(OpeningType.WINDOW, 1.0, 1.0));
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, openings);

        String description = room.describe();

        assertThat(description).isNotEmpty()
                .contains("3.0")
                .contains("4.0")
                .contains("2.5")
                .contains("2.76");
    }

    @Test
    void wallpaperRollsShouldCalculateCorrectNumberOfRolls() {
        // Given
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, 5.0); // netWallArea = 30.0
        double rollWidth = 1.06;
        double rollLength = 10.0;

        // When
        int rollsNeeded = room.getWallpaperRolls(rollWidth, rollLength);

        // Then
        assertThat(rollsNeeded).isEqualTo(3);
    }

    @Test
    void wallpaperRollsShouldCalculateCorrectNumberOfRollsWithStandartRoolSize() {
        // Given
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, 5.0); // netWallArea = 30.0
                // When
        int rollsNeeded = room.getWallpaperRolls();

        // Then
        assertThat(rollsNeeded).isEqualTo(3);
    }

    @Test
    void laminatePlanksShouldCalculateCorrectNumberOfPlanks() {
        // Given
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, 5.0);
        double plankWidth = 0.16;
        double plankLength = 1.286;

        // When
        int rollsNeeded = room.getlaminatePlank(plankWidth, plankLength);

        // Then
        assertThat(rollsNeeded).isEqualTo(59);
    }

    @Test
    void laminatePlanksShouldCalculateCorrectNumberOfPlanksWithStandartPlankSize() {
        // Given
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, 5.0);

        // When
        int rollsNeeded = room.getlaminatePlank();

        // Then
        assertThat(rollsNeeded).isEqualTo(59);
    }

    @Test
    void linomeumShouldCalculateCorrectRoolLength() {
        // Given
        Room room = new Room("Test Room", 1.0, 6.0, 2.5, 5.0); // netWallArea = 30.0
        double rollWidth = 7.0;

        // When
        double rollsNeeded = room.getLinomeumRollLength(rollWidth);

        // Then
        assertThat(rollsNeeded).isEqualTo(1.0);
    }

    @Test
    void shouldCreateRoomWithListOfOpenings() {
        // Given
        List<Opening> openings = Arrays.asList(
                new Opening(OpeningType.WINDOW, 1.5, 1.0), // условный пример, координаты не указаны
                new Opening(OpeningType.DOOR, 0.9, 2.0)
        );

        // When
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, openings);

        // Then
        assertThat(room.getOpenings()).hasSize(2)
                .containsExactly(openings.get(0), openings.get(1)); // порядок важен
    }

    @Test
    void shouldReplaceNullOpeningsListWithEmptyList() {
        // When
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, null);

        // Then
        assertThat(room.getOpenings()).isEmpty();
    }

    @Test
    void shouldReturnImmutableOpeningsList() {
        // Given
        List<Opening> openings = Collections.singletonList(new Opening(OpeningType.WINDOW, 1.5, 1.0));
        Room room = new Room("Test Room", 3.0, 4.0, 2.5, openings);
        List<Opening> retrievedList = room.getOpenings();

        // When / Then
        assertThatThrownBy(() -> retrievedList.add(new Opening(OpeningType.DOOR, 0.9, 2.0)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}

