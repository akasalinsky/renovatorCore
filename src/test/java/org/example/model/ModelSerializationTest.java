package org.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты сериализации/десериализации моделей")
class ModelSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        // Настройки как в FileUserProjectRepository
        mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Nested
    @DisplayName("Сериализация Opening")
    class OpeningSerializationTest {

        @Test
        @DisplayName("Должен корректно сериализовать Opening в JSON")
        void shouldSerializeOpening() throws Exception {
            Opening opening = new Opening(OpeningType.WINDOW, 1200, 1400);

            String json = mapper.writeValueAsString(opening);

            assertThat(json)
                    .contains("\"type\" : \"WINDOW\"")
                    .contains("\"width\" : 1200")
                    .contains("\"height\" : 1400");
        }

        @Test
        @DisplayName("Должен корректно десериализовать Opening из JSON")
        void shouldDeserializeOpening() throws Exception {
            String json = """
                    {
                      "type" : "DOOR",
                      "width" : 900,
                      "height" : 2100
                    }
                    """;

            Opening opening = mapper.readValue(json, Opening.class);

            assertThat(opening.getType()).isEqualTo(OpeningType.DOOR);
            assertThat(opening.getWidth()).isEqualTo(900);
            assertThat(opening.getHeight()).isEqualTo(2100);
            assertThat(opening.getArea()).isEqualTo(900 * 2100);
        }

        @Test
        @DisplayName("Round-trip: Opening должен сохраняться и восстанавливаться идентично")
        void shouldRoundTripOpening() throws Exception {
            Opening original = new Opening(OpeningType.WINDOW, 1500, 1200);

            String json = mapper.writeValueAsString(original);
            Opening deserialized = mapper.readValue(json, Opening.class);

            assertThat(deserialized).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("Сериализация WallOpening")
    class WallOpeningSerializationTest {

        @Test
        @DisplayName("Должен корректно сериализовать WallOpening (record) в JSON")
        void shouldSerializeWallOpening() throws Exception {
            Opening opening = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wallOpening = new WallOpening(opening, 500, 900);

            String json = mapper.writeValueAsString(wallOpening);

            assertThat(json)
                    .contains("\"opening\"")
                    .contains("\"distanceFromLeft\" : 500")
                    .contains("\"distanceFromFloor\" : 900");
        }

        @Test
        @DisplayName("Round-trip: WallOpening должен сохраняться и восстанавливаться идентично")
        void shouldRoundTripWallOpening() throws Exception {
            Opening opening = new Opening(OpeningType.DOOR, 900, 2100);
            WallOpening original = new WallOpening(opening, 300, 0);

            String json = mapper.writeValueAsString(original);
            WallOpening deserialized = mapper.readValue(json, WallOpening.class);

            assertThat(deserialized).isEqualTo(original);
            assertThat(deserialized.opening()).isEqualTo(opening);
            assertThat(deserialized.distanceFromLeft()).isEqualTo(300);
            assertThat(deserialized.distanceFromFloor()).isZero();
        }
    }

    @Nested
    @DisplayName("Сериализация Wall")
    class WallSerializationTest {

        @Test
        @DisplayName("Должен сериализовать Wall без проемов")
        void shouldSerializeWallWithoutOpenings() throws Exception {
            Wall wall = new Wall(3000, 2500, 0, List.of());

            String json = mapper.writeValueAsString(wall);

            assertThat(json)
                    .contains("\"length\" : 3000")
                    .contains("\"height\" : 2500")
                    .contains("\"wallOpenings\" : [ ]");
        }

        @Test
        @DisplayName("Должен сериализовать Wall с проемами")
        void shouldSerializeWallWithOpenings() throws Exception {
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wo1 = new WallOpening(window, 500, 900);

            Opening door = new Opening(OpeningType.DOOR, 900, 2100);
            WallOpening wo2 = new WallOpening(door, 100, 0);

            Wall wall = new Wall(4000, 2500, 0, List.of(wo1, wo2));

            String json = mapper.writeValueAsString(wall);

            assertThat(json)
                    .contains("\"length\" : 4000")
                    .contains("\"wallOpenings\"")
                    .contains("\"distanceFromLeft\" : 500")
                    .contains("\"distanceFromLeft\" : 100");
        }

        @Test
        @DisplayName("Round-trip: Wall с проемами должен сохраняться и восстанавливаться идентично")
        void shouldRoundTripWallWithOpenings() throws Exception {
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wo = new WallOpening(window, 500, 900);
            Wall original = new Wall(3000, 2500, 0, List.of(wo));

            String json = mapper.writeValueAsString(original);
            Wall deserialized = mapper.readValue(json, Wall.class);

            assertThat(deserialized).isEqualTo(original);
            assertThat(deserialized.getLength()).isEqualTo(3000);
            assertThat(deserialized.getHeight()).isEqualTo(2500);
            assertThat(deserialized.getWallOpenings()).hasSize(1);
            assertThat(deserialized.getWallOpenings().get(0)).isEqualTo(wo);
        }
    }

    @Nested
    @DisplayName("Сериализация Room")
    class RoomSerializationTest {

        @Test
        @DisplayName("Должен сериализовать Room с 4 стенами")
        void shouldSerializeRoom() throws Exception {
            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0, List.of()),
                    new Wall(4000, 2500, 1, List.of()),
                    new Wall(3000, 2500, 2, List.of()),
                    new Wall(4000, 2500, 3, List.of())
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Living Room", 2500, walls, angles);

            String json = mapper.writeValueAsString(room);

            assertThat(json)
                    .contains("\"name\" : \"Living Room\"")
                    .contains("\"height\" : 2500")
                    .contains("\"walls\"")
                    .contains("\"angles\" : [ 90, 90, 90, 90 ]");
        }

        @Test
        @DisplayName("Должен сериализовать Room с проемами на стенах")
        void shouldSerializeRoomWithOpenings() throws Exception {
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wo = new WallOpening(window, 500, 900);

            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0, List.of(wo)), // Северная стена с окном
                    new Wall(4000, 2500, 1, List.of()),
                    new Wall(3000, 2500, 2, List.of()),
                    new Wall(4000, 2500, 3, List.of())
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room room = new Room("Bedroom", 2500, walls, angles);

            String json = mapper.writeValueAsString(room);

            assertThat(json)
                    .contains("\"name\" : \"Bedroom\"")
                    .contains("\"distanceFromLeft\" : 500");
        }

        @Test
        @DisplayName("Round-trip: Room должен сохраняться и восстанавливаться идентично")
        void shouldRoundTripRoom() throws Exception {
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wo = new WallOpening(window, 500, 900);

            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0, List.of(wo)),
                    new Wall(4000, 2500, 1, List.of()),
                    new Wall(3000, 2500, 2, List.of()),
                    new Wall(4000, 2500, 3, List.of())
            );
            List<Integer> angles = List.of(90, 90, 90, 90);
            Room original = new Room("Kitchen", 2700, walls, angles);

            String json = mapper.writeValueAsString(original);
            Room deserialized = mapper.readValue(json, Room.class);

            assertThat(deserialized.getName()).isEqualTo("Kitchen");
            assertThat(deserialized.getHeight()).isEqualTo(2700);
            assertThat(deserialized.getWalls()).hasSize(4);
            assertThat(deserialized.getAngles()).containsExactly(90, 90, 90, 90);

            // Проверяем, что проем восстановился
            assertThat(deserialized.getWalls().get(0).getWallOpenings()).hasSize(1);
            assertThat(deserialized.getWalls().get(0).getWallOpenings().get(0)).isEqualTo(wo);
        }
    }

    @Nested
    @DisplayName("Сериализация UserProjects")
    class UserProjectsSerializationTest {

        @Test
        @DisplayName("Должен сериализовать UserProjects с несколькими комнатами")
        void shouldSerializeUserProjects() throws Exception {
            List<Wall> walls1 = List.of(
                    new Wall(3000, 2500, 0, List.of()),
                    new Wall(4000, 2500, 1, List.of()),
                    new Wall(3000, 2500, 2, List.of()),
                    new Wall(4000, 2500, 3, List.of())
            );
            Room room1 = new Room("Living Room", 2500, walls1, List.of(90, 90, 90, 90));

            List<Wall> walls2 = List.of(
                    new Wall(3500, 2700, 0, List.of()),
                    new Wall(4500, 2700, 1, List.of()),
                    new Wall(3500, 2700, 2, List.of()),
                    new Wall(4500, 2700, 3, List.of())
            );
            Room room2 = new Room("Bedroom", 2700, walls2, List.of(90, 90, 90, 90));

            UserProjects projects = new UserProjects(Locale.ENGLISH, List.of(room1, room2), 1);

            String json = mapper.writeValueAsString(projects);

            assertThat(json)
                    .contains("\"language\" : \"en\"")
                    .contains("\"rooms\"")
                    .contains("\"name\" : \"Living Room\"")
                    .contains("\"name\" : \"Bedroom\"")
                    .contains("\"activeRoomIndex\" : 1");
        }

        @Test
        @DisplayName("Должен сериализовать UserProjects с пустым списком комнат")
        void shouldSerializeEmptyUserProjects() throws Exception {
            UserProjects projects = new UserProjects(Locale.forLanguageTag("ru"), List.of(), 0);

            String json = mapper.writeValueAsString(projects);

            assertThat(json)
                    .contains("\"language\" : \"ru\"")
                    .contains("\"rooms\" : [ ]")
                    .contains("\"activeRoomIndex\" : 0");
        }

        @Test
        @DisplayName("Round-trip: UserProjects должен сохраняться и восстанавливаться идентично")
        void shouldRoundTripUserProjects() throws Exception {
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wo = new WallOpening(window, 500, 900);

            List<Wall> walls = List.of(
                    new Wall(3000, 2500, 0, List.of(wo)),
                    new Wall(4000, 2500, 1, List.of()),
                    new Wall(3000, 2500, 2, List.of()),
                    new Wall(4000, 2500, 3, List.of())
            );
            Room room = new Room("Kitchen", 2500, walls, List.of(90, 90, 90, 90));

            UserProjects original = new UserProjects(Locale.forLanguageTag("ru"), List.of(room), 0);

            String json = mapper.writeValueAsString(original);
            UserProjects deserialized = mapper.readValue(json, UserProjects.class);

            assertThat(deserialized.getLanguage()).isEqualTo(Locale.forLanguageTag("ru"));
            assertThat(deserialized.getRooms()).hasSize(1);
            assertThat(deserialized.getActiveRoomIndex()).isZero();

            Room deserializedRoom = deserialized.getRooms().get(0);
            assertThat(deserializedRoom.getName()).isEqualTo("Kitchen");
            assertThat(deserializedRoom.getHeight()).isEqualTo(2500);
            assertThat(deserializedRoom.getWalls()).hasSize(4);
            assertThat(deserializedRoom.getWalls().get(0).getWallOpenings()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Интеграционные тесты с ObjectMapper")
    class ObjectMapperIntegrationTest {

        @Test
        @DisplayName("Должен корректно обрабатывать pretty print (отступы)")
        void shouldUsePrettyPrint() throws Exception {
            Opening opening = new Opening(OpeningType.WINDOW, 1200, 1400);

            String json = mapper.writeValueAsString(opening);

            // Проверяем наличие отступов и переносов строк
            assertThat(json)
                    .contains("\n")
                    .contains("  ") // Отступы
                    .contains(": "); // Пробелы после двоеточия
        }

        /*@Test
        @DisplayName("Должен игнорировать неизвестные поля при десериализации")
        void shouldIgnoreUnknownFields() throws Exception {
            String json = """
                    {
                      "type" : "WINDOW",
                      "width" : 1200,
                      "height" : 1400,
                      "unknownField" : "someValue"
                    }
                    """;

            Opening opening = mapper.readValue(json, Opening.class);

            assertThat(opening.getType()).isEqualTo(OpeningType.WINDOW);
            assertThat(opening.getWidth()).isEqualTo(1200);
            assertThat(opening.getHeight()).isEqualTo(1400);
        }*/

        @Test
        @DisplayName("Должен корректно работать с Locale (en/ru)")
        void shouldHandleLocales() throws Exception {
            UserProjects enProjects = new UserProjects(Locale.ENGLISH, List.of(), 0);
            UserProjects ruProjects = new UserProjects(Locale.forLanguageTag("ru"), List.of(), 0);

            String enJson = mapper.writeValueAsString(enProjects);
            String ruJson = mapper.writeValueAsString(ruProjects);

            assertThat(enJson).contains("\"language\" : \"en\"");
            assertThat(ruJson).contains("\"language\" : \"ru\"");

            UserProjects enDeserialized = mapper.readValue(enJson, UserProjects.class);
            UserProjects ruDeserialized = mapper.readValue(ruJson, UserProjects.class);

            assertThat(enDeserialized.getLanguage()).isEqualTo(Locale.ENGLISH);
            assertThat(ruDeserialized.getLanguage()).isEqualTo(Locale.forLanguageTag("ru"));
        }
    }
}