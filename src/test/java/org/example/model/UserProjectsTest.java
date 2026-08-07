package org.example.model;

import org.example.repository.FileUserProjectRepository;
import org.example.repository.UserProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;


@DisplayName("Тесты для модели UserProjects")
class UserProjectsTest {
    String name1 = "Test Room1";
    String name2 = "Test Room2";

    int height = 2500;
    List<Wall> walls1 = List.of(
            new Wall(3000, height, 0),
            new Wall(4000, height, 1),
            new Wall(3000, height, 2),
            new Wall(4000, height,3)
    );
    List<Wall> walls2 = List.of(
            new Wall(4000, height, 0),
            new Wall(5000, height, 1),
            new Wall(4000, height, 2),
            new Wall(5000, height, 3)
    );
    List<Integer> angles = List.of(90, 90, 90, 90);

    // When
    Room room1 = new Room(name1, height, walls1, angles);
    Room room2 = new Room(name2, height, walls2, angles);

    private final List<Room> rooms = List.of(room1, room2);

    @Test
    @DisplayName("Должен успешно создаваться с корректными данными")
    void shouldCreateWithValidData() {
        UserProjects projects = new UserProjects(Locale.forLanguageTag("ru"), rooms, 1);

        assertThat(projects.locale()).isEqualTo(Locale.forLanguageTag("ru"));
        assertThat(projects.rooms()).containsExactlyElementsOf(rooms);
        assertThat(projects.activeRoomIndex()).isEqualTo(1);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если rooms равен null")
    void shouldThrowWhenRoomsIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UserProjects(Locale.ENGLISH, null, 0))
                .withMessageContaining("rooms");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если activeRoomIndex вне границ списка")
    void shouldThrowWhenActiveIndexOutOfBounds() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UserProjects(Locale.ENGLISH, rooms, 5))
                .withMessageContaining("activeRoomIndex");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UserProjects(Locale.ENGLISH, rooms, -1))
                .withMessageContaining("activeRoomIndex");
    }

    @Test
    @DisplayName("Должен игнорировать переданный индекс и ставить 0, если список комнат пуст")
    void shouldIgnoreActiveIndexWhenRoomsEmpty() {
        // Передаем индекс 99, но список пуст. Ожидаем, что исключений не будет, а индекс станет 0.
        UserProjects projects = new UserProjects(Locale.ENGLISH, List.of(), 99);

        assertThat(projects.rooms()).isEmpty();
        assertThat(projects.activeRoomIndex()).isZero();
    }

    @Test
    @DisplayName("Должен создавать иммутабельные копии через with-методы")
    void shouldCreateNewInstanceWithWithMethods() {
        UserProjects original = new UserProjects(Locale.ENGLISH, rooms, 0);

        UserProjects withLanguage = original.withLanguage(Locale.forLanguageTag("ru"));
        UserProjects withRooms = original.withRooms(List.of(new Room("room3", height, walls1, angles)));
        UserProjects withIndex = original.withActiveRoomIndex(1);

        // Проверяем, что оригинал не изменился
        assertThat(original.locale()).isEqualTo(Locale.ENGLISH);
        assertThat(original.activeRoomIndex()).isZero();

        // Проверяем новые объекты
        assertThat(withLanguage.locale()).isEqualTo(Locale.forLanguageTag("ru"));
        assertThat(withRooms.rooms()).hasSize(1);
        assertThat(withIndex.activeRoomIndex()).isEqualTo(1);
    }

    /*@Test
    @DisplayName("Должен защищать внутреннее состояние (список комнат иммутабелен)")
    void shouldProtectInternalState() {
        List<Room> mutableRooms = new java.util.ArrayList<>(rooms);
        UserProjects projects = new UserProjects(Locale.ENGLISH, mutableRooms, 0);

        mutableRooms.add(new Room("room_hacker", height, walls1, angles));

        assertThat(projects.getRooms()).hasSize(2); // Изменения во внешнем списке не повлияли
        assertThat(projects.getRooms()).isUnmodifiable(); // И вернуть изменяемый список тоже нельзя
    }*/
}

@DisplayName("Тесты для FileUserProjectRepositoryTest")
class FileUserProjectRepositoryTest {

    private UserProjectRepository repository;
    private final long chatId1 = 12345L;
    private final long chatId2 = 67890L;
    private UserProjects projects1;
    private UserProjects projects2;

    @TempDir  // ← Добавляем временную директорию
    Path tempDir;

    @BeforeEach
    void setUp() {
        int height = 2500;
        List<Wall> walls1 = List.of(
                new Wall(3000, height, 0),
                new Wall(4000, height, 1),
                new Wall(3000, height, 2),
                new Wall(4000, height, 3)
        );
        List<Wall> walls2 = List.of(
                new Wall(4000, height, 0),
                new Wall(5000, height, 1),
                new Wall(4000, height, 2),
                new Wall(5000, height, 3)
        );
        List<Integer> angles = List.of(90, 90, 90, 90);
        repository = new FileUserProjectRepository(tempDir.toString());
        projects1 = new UserProjects(Locale.ENGLISH, List.of(new Room("r1", height, walls1, angles)), 0);
        projects2 = new UserProjects(Locale.forLanguageTag("ru"), List.of(new Room("r2", height, walls2, angles)), 0);
    }

    @Test
    @DisplayName("Должен сохранять и загружать проекты по chatId")
    void shouldSaveAndLoad() {
        repository.save(chatId1, projects1);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);

        assertThat(loaded).isPresent().contains(projects1);
    }

    @Test
    @DisplayName("Должен возвращать Optional.empty() для несуществующего chatId")
    void shouldReturnEmptyForNonExistentChatId() {
        Optional<UserProjects> loaded = repository.findByChatId(99999L);

        assertThat(loaded).isEmpty();
    }

    @Test
    @DisplayName("Должен перезаписывать данные при повторном сохранении")
    void shouldOverwriteOnSave() {
        repository.save(chatId1, projects1);

        UserProjects updatedProjects = projects1.withLanguage(Locale.US);
        repository.save(chatId1, updatedProjects);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);
        assertThat(loaded).isPresent().contains(updatedProjects);
    }

    @Test
    @DisplayName("Должен удалять данные по chatId")
    void shouldDelete() {
        repository.save(chatId1, projects1);

        repository.delete(chatId1);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);
        assertThat(loaded).isEmpty();
    }

    @Test
    @DisplayName("Данные для разных chatId должны быть независимы")
    void shouldBeIndependentForDifferentChatIds() {
        repository.save(chatId1, projects1);
        repository.save(chatId2, projects2);

        repository.delete(chatId1);

        assertThat(repository.findByChatId(chatId1)).isEmpty();
        assertThat(repository.findByChatId(chatId2)).isPresent().contains(projects2);
    }
    @Test
    @DisplayName("Должен выбрасывать исключение, если язык не поддерживается (не en и не ru)")
    void shouldThrowWhenLanguageIsNotSupported() {
        List<Room> validRooms = List.of(new Room("room1", 2500, List.of(new Wall(4000, 2500, 0),
                new Wall(5000, 2500, 1), new Wall(4000, 2500, 2), new Wall(5000, 2500, 3)), List.of(90, 90, 90, 90)));
        Locale unsupportedLocale = Locale.FRENCH; // Можно также проверить Locale.GERMAN, Locale.CHINESE и т.д.

        assertThatIllegalArgumentException()
                 .isThrownBy(() -> new UserProjects(unsupportedLocale, validRooms, 0))
                .withMessageContaining("unsupported language")
                .withMessageContaining(unsupportedLocale.getLanguage()); // Проверяем, что в сообщении указан сам язык
    }

    @Test
    @DisplayName("Должен успешно принимать русский язык (ru)")
    void shouldAcceptRussianLanguage() {
        List<Room> validRooms = List.of(new Room("room1", 2500, List.of(new Wall(4000, 2500, 0),
                new Wall(5000, 2500, 1), new Wall(4000, 2500, 2), new Wall(5000, 2500, 3)), List.of(90, 90, 90, 90)));
        Locale russianLocale = Locale.forLanguageTag("ru");

        UserProjects projects = new UserProjects(russianLocale, validRooms, 0);

        assertThat(projects.locale()).isEqualTo(russianLocale);
    }
}