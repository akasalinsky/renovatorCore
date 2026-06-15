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
        int length = 3000;
        int width = 4000;
        int height = 2500;
        int openingsArea = 0;

        // When
        Room room = new Room(name, length, width, height, openingsArea);

        // Then
        assertThat(room.getLength()).isEqualTo(length);
        assertThat(room.getWidth()).isEqualTo(width);
        assertThat(room.getHeight()).isEqualTo(height);
        assertThat(room.getOpeningsArea()).isEqualTo(openingsArea);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100, -100000})
    void shouldThrowIllegalArgumentExceptionWhenLengthIsZeroOrNegative(int invalidLength) {

        assertThatThrownBy(() ->
                new Room("Test Room", invalidLength, 4000, 2500, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Length must be positive");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100000})
    void shouldThrowIllegalArgumentExceptionWhenWidthIsZeroOrNegative(int invalidWidth) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3000, invalidWidth, 2500, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Width must be positive");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1000, -100000})
    void shouldThrowIllegalArgumentExceptionWhenHeightIsZeroOrNegative(int invalidHeight) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3000, 2500, invalidHeight, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Height must be positive");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1000, -100000})
    void shouldThrowIllegalArgumentExceptionWhenOpeningsAreaIsZeroOrNegative(int invalidOpeningsArea) {
        assertThatThrownBy(() ->
                new Room("Test Room", 3000, 2500, 2500, invalidOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot be negative");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10000, 34999, 35000}) // Включая случай с ровно 35
    void shouldAllowCreationWhenOpeningsAreaIsWithinValidRange(int openingsArea) {
        // Given
        int length = 3000;
        int width = 4000;
        int height = 2500;
        // Total wall area = 2 * (length + width) * height = 2 * (3 + 4) * 2.5 = 35

        // When / Then
        // Ожидаем, что исключение НЕ будет выброшено
        assertDoesNotThrow(() ->
                new Room("Test Room", length, width, height, openingsArea));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOpeningsAreaExceedsTotalWallArea() {
        // Given
        int length = 3000;
        int width = 4000;
        int height = 2500;
        int invalidOpeningsArea = 36000000;
        // Total wall area = 2 * (length + width) * height = 2 * (3 + 4) * 2.5 = 35

        // When / Then
        assertThatThrownBy(() ->
                new Room("Test Room", length, width, height, invalidOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot exceed total wall area");
    }

    @ParameterizedTest
    @MethodSource("floorAreaTestData")
    void shouldCalculateFloorAreaCorrectly(int length, int width, int expectedFloorArea) {
        // Given
        int height = 2500;
        int openingsArea = 0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getFloorArea()).isEqualTo(expectedFloorArea);
    }

    static Stream<Arguments> floorAreaTestData() {
        return Stream.of(
                Arguments.of(3000, 4000, 12000000), // 3 * 4 = 12
                Arguments.of(5000, 5000, 25000000), // 5 * 5 = 25
                Arguments.of(2000, 6000, 12000000), // 2 * 6 = 12
                Arguments.of(10000, 1500, 15000000) // 10 * 1.5 = 15
        );
    }

    /*@ParameterizedTest
    @MethodSource("volumeTestData")
    void shouldCalculateVolumeCorrectly(int length, int width, int height, long expectedVolume) {
        // Given
        int openingsArea = 0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getVolume()).isEqualTo(expectedVolume);
    }

    static Stream<Arguments> volumeTestData() {
        return Stream.of(
                Arguments.of(3000, 4000, 2500, 30000000000), // 3 * 4 * 2.5 = 30
                Arguments.of(1000, 1000, 1000, 1000000000)   // 1 * 1 * 1 = 1
        );
    }*/

    @ParameterizedTest
    @MethodSource("totalWallAreaTestData")
    void shouldCalculateTotalWallAreaCorrectly(int length, int width, int height, int expectedTotalWallArea) {
        // Given
        int openingsArea = 0;

        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getTotalWallArea()).isEqualTo(expectedTotalWallArea);
    }

    static Stream<Arguments> totalWallAreaTestData() {
        return Stream.of(
                Arguments.of(3000, 4000, 2500, 35000000), // 2 * (3 + 4) * 2.5 = 35
                Arguments.of(1000, 1000, 1000, 4000000)   // 2 * (1 + 1) * 1 = 4
        );
    }

    @ParameterizedTest
    @MethodSource("netWallAreaTestData")
    void shouldCalculateNetWallAreaCorrectly(int length, int width, int height, int openingsArea, int expectedNetWallArea) {
        // When
        Room room = new Room("Test Room", length, width, height, openingsArea);

        // Then
        assertThat(room.getNetWallArea()).isEqualTo(expectedNetWallArea);
    }

    static Stream<Arguments> netWallAreaTestData() {
        int length = 3000;
        int width = 4000;
        int height = 2500;
        int totalWallArea = 2 * (length + width) * height; // 35.0

        return Stream.of(
                Arguments.of(length, width, height, 5000000, 30000000),      // 35 - 5 = 30
                Arguments.of(length, width, height, 0, 35000000),     // 35 - 0 = 35 (totalWallArea)
                Arguments.of(length, width, height, 35000000, 0)      // 35 - 35 = 0
        );
    }

    @Test
    void shouldReturnNewRoomWithUpdatedOpeningsAreaAndKeepOriginalUnchanged() {
        // Given
        Room originalRoom = new Room("Test Room", 3000, 4000, 2500, 5000); // проёмы = 5

        // When
        Room updatedRoom = originalRoom.withOpeningsArea(10000);

        // Then
        assertThat(updatedRoom.getOpeningsArea()).isEqualTo(10000);
        assertThat(originalRoom.getOpeningsArea()).isEqualTo(5000); // оригинальный объект не изменился
        assertThat(updatedRoom.getName()).isEqualTo(originalRoom.getName()); // имя не изменилось
        assertThat(updatedRoom.getLength()).isEqualTo(originalRoom.getLength()); // длина не изменилась
        assertThat(updatedRoom.getWidth()).isEqualTo(originalRoom.getWidth()); // ширина не изменилась
        assertThat(updatedRoom.getHeight()).isEqualTo(originalRoom.getHeight()); // высота не изменилась
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNewOpeningsAreaExceedsTotalWallAreaAndKeepOriginalUnchanged() {
        // Given
        Room originalRoom = new Room("Test Room", 3000, 4000, 2500, 5000); // проёмы = 5, площадь стен = 35
        int invalidNewOpeningsArea = 35000001; // больше, чем площадь стен

        // When / Then
        assertThatThrownBy(() -> originalRoom.withOpeningsArea(invalidNewOpeningsArea))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Openings area cannot exceed total wall area");

        // Ensure original object is unchanged
        assertThat(originalRoom.getOpeningsArea()).isEqualTo(5000);
    }

    @Test
    void describeShouldContainAllDimensionValues() {
        Room room = new Room("Test Room", 3000, 4000, 2500, 5000);

        String description = room.describe();

        assertThat(description).isNotEmpty()
                .contains("3000")
                .contains("4000")
                .contains("2500")
                .contains("5000");
    }

    @Test
    void describeShouldContainAllDimensionValuesWithOpeningList() {
        List<Opening> openings = List.of(new Opening(OpeningType.DOOR, 800, 2200), new Opening(OpeningType.WINDOW, 1000, 1000));
        Room room = new Room("Test Room", 3000, 4000, 2500, openings);

        String description = room.describe();

        assertThat(description).isNotEmpty()
                .contains("3000")
                .contains("4000")
                .contains("2500")
                .contains("2760");
    }

    @Test
    void wallpaperRollsShouldCalculateCorrectNumberOfRolls() {
        // Given
        Room room = new Room("Test Room", 3000, 4000, 2500, 5000); // netWallArea = 30.0
        int rollWidth = 1060;
        int rollLength = 10000;

        // When
        int rollsNeeded = room.getWallpaperRolls(rollWidth, rollLength);

        // Then
        assertThat(rollsNeeded).isEqualTo(3);
    }

    @Test
    void wallpaperRollsShouldCalculateCorrectNumberOfRollsWithStandartRoolSize() {
        // Given
        Room room = new Room("Test Room", 3000, 4000, 2500, 5000); // netWallArea = 30.0
                // When
        int rollsNeeded = room.getWallpaperRolls();

        // Then
        assertThat(rollsNeeded).isEqualTo(3);
    }

    @Test
    void laminatePlanksShouldCalculateCorrectNumberOfPlanks() {
        // Given
        Room room = new Room("Test Room", 3000, 4000, 2500, 5000000);
        int plankWidth = 160;
        int plankLength = 1286;

        // When
        int rollsNeeded = room.getlaminatePlank(plankWidth, plankLength);

        // Then
        assertThat(rollsNeeded).isEqualTo(59);
    }

    @Test
    void laminatePlanksShouldCalculateCorrectNumberOfPlanksWithStandartPlankSize() {
        // Given
        Room room = new Room("Test Room", 3000, 4000, 2500, 5000000);

        // When
        int rollsNeeded = room.getlaminatePlank();

        // Then
        assertThat(rollsNeeded).isEqualTo(59);
    }

    @Test
    void linomeumShouldCalculateCorrectRoolLength() {
        // Given
        Room room = new Room("Test Room", 1000, 6000, 2500, 5000000); // netWallArea = 30.0
        int rollWidth = 7000;

        // When
        int rollsNeeded = room.getLinomeumRollLength(rollWidth);

        // Then
        assertThat(rollsNeeded).isEqualTo(1000);
    }

    @Test
    void shouldCreateRoomWithListOfOpenings() {
        // Given
        List<Opening> openings = Arrays.asList(
                new Opening(OpeningType.WINDOW, 1500, 1000), // условный пример, координаты не указаны
                new Opening(OpeningType.DOOR, 900, 2000)
        );

        // When
        Room room = new Room("Test Room", 3000, 4000, 2500, openings);

        // Then
        assertThat(room.getOpenings()).hasSize(2)
                .containsExactly(openings.get(0), openings.get(1)); // порядок важен
    }

    @Test
    void shouldReplaceNullOpeningsListWithEmptyList() {
        // When
        Room room = new Room("Test Room", 3000, 4000, 2500, null);

        // Then
        assertThat(room.getOpenings()).isEmpty();
    }

    @Test
    void shouldReturnImmutableOpeningsList() {
        // Given
        List<Opening> openings = Collections.singletonList(new Opening(OpeningType.WINDOW, 1500, 1000));
        Room room = new Room("Test Room", 3000, 4000, 2500, openings);
        List<Opening> retrievedList = room.getOpenings();

        // When / Then
        assertThatThrownBy(() -> retrievedList.add(new Opening(OpeningType.DOOR, 900, 2000)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void renderPlanShouldReturnCorrectAsciiRepresentation() {
        // Given
        Room room = new Room("Test Room", 3000, 3000, 2500, 0); // length=4, width=3

        // When
        String plan = room.renderPlan();

        // Then
        String expectedPlan = "+---+\n" +
                "|   |\n" +
                "|   |\n" +
                "|   |\n" +
                "+---+\n";
        assertThat(plan).isEqualTo(expectedPlan);
    }

}

