package org.example.repository;

import org.example.model.Opening;
import org.example.model.OpeningType;
import org.example.model.Room;
import org.example.model.UserProjects;
import org.example.model.Wall;
import org.example.model.WallOpening;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для FileUserProjectRepository")
class FileUserProjectRepositoryTest {

    @TempDir
    Path tempDir;

    private FileUserProjectRepository repository;
    private final long chatId1 = 12345L;
    private final long chatId2 = 67890L;
    private UserProjects projects1;
    private UserProjects projects2;

    @BeforeEach
    void setUp() {
        repository = new FileUserProjectRepository(tempDir.toString(), chatId1);

        // Создаем тестовые данные
        Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
        WallOpening wo = new WallOpening(window, 500, 900);

        List<Wall> walls1 = List.of(
                new Wall(3000, 2500, 1, List.of(wo)),
                new Wall(4000, 2500, 2, List.of()),
                new Wall(3000, 2500, 3, List.of()),
                new Wall(4000, 2500, 4, List.of())
        );
        Room room1 = new Room("Living Room", 2500, walls1, List.of(90, 90, 90, 90));
        projects1 = new UserProjects(Locale.ENGLISH, List.of(room1), 0);

        List<Wall> walls2 = List.of(
                new Wall(3500, 2700, 1, List.of()),
                new Wall(4500, 2700, 2, List.of()),
                new Wall(3500, 2700, 3, List.of()),
                new Wall(4500, 2700, 4, List.of())
        );
        Room room2 = new Room("Bedroom", 2700, walls2, List.of(90, 90, 90, 90));
        projects2 = new UserProjects(Locale.forLanguageTag("ru"), List.of(room2), 0);
    }

    @Test
    @DisplayName("Должен сохранять и загружать проекты (round-trip)")
    void shouldSaveAndLoad() {
        repository.save(chatId1, projects1);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getLanguage()).isEqualTo(Locale.ENGLISH);
        assertThat(loaded.get().getRooms()).hasSize(1);
        assertThat(loaded.get().getRooms().get(0).getName()).isEqualTo("Living Room");
        assertThat(loaded.get().getActiveRoomIndex()).isZero();
    }

    @Test
    @DisplayName("Должен возвращать Optional.empty() для несуществующего chatId")
    void shouldReturnEmptyForNonExistentChatId() {
        Optional<UserProjects> loaded = repository.findByChatId(99999L);

        assertThat(loaded).isEmpty();
    }

    @Test
    @DisplayName("Должен перезаписывать файл при повторном сохранении")
    void shouldOverwriteFileOnSave() {
        repository.save(chatId1, projects1);

        UserProjects updatedProjects = projects1.withLanguage(Locale.forLanguageTag("ru"));
        repository.save(chatId1, updatedProjects);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getLanguage()).isEqualTo(Locale.forLanguageTag("ru"));
    }

    @Test
    @DisplayName("Должен удалять файл при вызове delete")
    void shouldDeleteFile() {
        repository.save(chatId1, projects1);
        Path file = tempDir.resolve("chat_" + chatId1 + ".json");
        assertThat(file).exists();

        repository.delete(chatId1);

        assertThat(file).doesNotExist();
        assertThat(repository.findByChatId(chatId1)).isEmpty();
    }

    /*@Test
    @DisplayName("Должен возвращать Optional.empty() при битом JSON (не падать)")
    void shouldReturnEmptyForCorruptedJson() {
        Path file = tempDir.resolve("chat_" + chatId1 + ".json");
        Files.writeString(file, "this is not a valid json {{{");

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);

        assertThat(loaded).isEmpty();
    }*/

    @Test
    @DisplayName("Должен создавать директорию, если её нет")
    void shouldCreateDirectoryIfNotExists() {
        Path newDir = tempDir.resolve("new_storage");
        FileUserProjectRepository newRepo = new FileUserProjectRepository(newDir.toString());
        assertThat(newDir).doesNotExist();

        newRepo.save(chatId1, projects1);

        assertThat(newDir).exists();
        assertThat(newDir.resolve("chat_" + chatId1 + ".json")).exists();
    }

    @Test
    @DisplayName("Данные для разных chatId должны быть независимы")
    void shouldBeIndependentForDifferentChatIds() {
        repository.save(chatId1, projects1);
        repository.save(chatId2, projects2);

        repository.delete(chatId1);

        assertThat(repository.findByChatId(chatId1)).isEmpty();
        assertThat(repository.findByChatId(chatId2)).isPresent();
        assertThat(repository.findByChatId(chatId2).get().getLanguage()).isEqualTo(Locale.forLanguageTag("ru"));
    }

    @Test
    @DisplayName("Должен сохранять JSON с отступами (pretty print)")
    void shouldSaveWithPrettyPrint() throws IOException {
        repository.save(chatId1, projects1);

        Path file = tempDir.resolve("chat_" + chatId1 + ".json");
        String content = Files.readString(file);

        assertThat(content)
                .contains("\n")
                .contains("  ") // Отступы
                .contains(": "); // Пробелы после двоеточия
    }

    @Test
    @DisplayName("Должен корректно сохранять и загружать комнаты с проемами")
    void shouldSaveAndLoadRoomsWithOpenings() {
        repository.save(chatId1, projects1);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);

        assertThat(loaded).isPresent();
        Room loadedRoom = loaded.get().getRooms().get(0);
        assertThat(loadedRoom.getWalls().get(0).getWallOpenings()).hasSize(1);

        WallOpening loadedOpening = loadedRoom.getWalls().get(0).getWallOpenings().get(0);
        assertThat(loadedOpening.opening().getType()).isEqualTo(OpeningType.WINDOW);
        assertThat(loadedOpening.opening().getWidth()).isEqualTo(1200);
        assertThat(loadedOpening.distanceFromLeft()).isEqualTo(500);
    }

    @Test
    @DisplayName("Должен корректно сохранять и загружать пустой список комнат")
    void shouldSaveAndLoadEmptyRooms() {
        UserProjects emptyProjects = new UserProjects(Locale.ENGLISH, List.of(), 0);

        repository.save(chatId1, emptyProjects);

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getRooms()).isEmpty();
        assertThat(loaded.get().getActiveRoomIndex()).isZero();
    }

    @Test
    @DisplayName("delete не должен падать, если файл не существует")
    void shouldNotFailWhenDeletingNonExistentFile() {
        Path file = tempDir.resolve("chat_" + chatId1 + ".json");
        assertThat(file).doesNotExist();

        repository.delete(chatId1); // Не должно выбросить исключение

        assertThat(repository.findByChatId(chatId1)).isEmpty();
    }

    @Test
    @DisplayName("Должен создавать директорию, если её нет")
    void shouldCreateDirectoryIfNotExists() {
        Path newDir = tempDir.resolve("new_storage");
        FileUserProjectRepository newRepo = new FileUserProjectRepository(newDir.toString());

        newRepo.save(chatId1, projects1);

        assertThat(newDir).exists();
    }

    @Test
    @DisplayName("Должен возвращать Optional.empty() при битом JSON")
    void shouldReturnEmptyForCorruptedJson() throws IOException {
        Path file = tempDir.resolve("chat_" + chatId1 + ".json");
        Files.writeString(file, "this is not a valid json {{{");

        Optional<UserProjects> loaded = repository.findByChatId(chatId1);
        assertThat(loaded).isEmpty();
    }
}