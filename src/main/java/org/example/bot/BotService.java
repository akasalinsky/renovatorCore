package org.example.bot;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.cli.RoomCli;
import org.example.i18n.MessageProvider;
import org.example.model.Room;
import org.example.model.UserProjects;
import org.example.repository.UserProjectRepository;

import java.util.*;

public class BotService {

    private final Map<Long, RoomCli> userSessions = new HashMap<>();
    private final Map<Long, Locale> userLocales = new HashMap<>();
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ru");
    private final String botToken;
    private final Long chatId;
    private final UserProjectRepository repository;
    private final Map<Long, UserProjects> loadedProjects = new HashMap<>();

    public BotService(UserProjectRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
        Dotenv dotenv = Dotenv.configure().load();

        this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        String chatIdStr = dotenv.get("TELEGRAM_CHAT_ID");

        if (this.botToken == null || this.botToken.isEmpty()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN не найден в .env файле");
        }

        if (chatIdStr == null || chatIdStr.isEmpty()) {
            throw new IllegalStateException("TELEGRAM_CHAT_ID не найден в .env файле");
        }

        try {
            this.chatId = Long.parseLong(chatIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("TELEGRAM_CHAT_ID должен быть числом", e);
        }
    }

    public String processMessage(long chatId, String text) {
        UserProjects projects = loadedProjects.computeIfAbsent(chatId, id -> {
            return repository.findByChatId(id)
                    .orElse(new UserProjects(DEFAULT_LOCALE, List.of(), 0));
        });
        Locale locale = projects.locale();
        MessageProvider messageProvider = new MessageProvider(locale);

        if (text == null) {
            return messageProvider.get("empty.message");
        }

        String trimmedText = text.trim();

        if (trimmedText.startsWith("/lang ")) {
            String langCode = trimmedText.substring(6).trim().toLowerCase();
            Locale newLocale = switch (langCode) {
                case "ru" -> new Locale("ru");
                case "en" -> Locale.ENGLISH;
                default -> null;
            };
            if (newLocale == null) {
                return messageProvider.get("lang.unsupported", langCode);
            }

            RoomCli oldCli = userSessions.get(chatId);
            Room currentRoom = (oldCli != null) ? oldCli.getCurrentRoom() : null;

            RoomCli newCli = new RoomCli(newLocale);
            if (currentRoom != null) {
                newCli.setCurrentRoom(currentRoom);
            }

            userSessions.put(chatId, newCli);
            userLocales.put(chatId, newLocale);

            UserProjects updateProjects = loadedProjects.get(chatId);
            if (updateProjects != null) {
                updateProjects = updateProjects.withLanguage(newLocale);
                loadedProjects.put(chatId, updateProjects);
                repository.save(chatId, updateProjects);
            }

            MessageProvider newMsgProvider = new MessageProvider(newLocale);
            return newMsgProvider.get("lang.set", newLocale.getDisplayName(newLocale));
        }

        if ("/start".equalsIgnoreCase(trimmedText) || "hello".equalsIgnoreCase(trimmedText)) {
            return messageProvider.get("start.welcome");
        }

        if ("exit".equalsIgnoreCase(trimmedText)) {
            userSessions.remove(chatId);
            loadedProjects.remove(chatId);
            return messageProvider.get("goodbye");
        }

        RoomCli cli = userSessions.computeIfAbsent(chatId, k -> {
            RoomCli newCli = new RoomCli(locale);
            if (!projects.rooms().isEmpty()) {
                Room activeRoom = projects.rooms().get(projects.activeRoomIndex());
                newCli.setCurrentRoom(activeRoom);
            }
            return newCli;
        });

        Room oldRoom = cli.getCurrentRoom();

        String response = cli.execute(text);

        syncRoomToProjects(chatId, cli, projects, text);

        return response;
    }

    private void syncRoomToProjects(long chatId, RoomCli cli, UserProjects projects, String command) {
        Room currentRoom = cli.getCurrentRoom();

        if (command.startsWith("create ") || command.startsWith("создать ")) {
            List<Room> rooms = new ArrayList<>(projects.rooms());
            rooms.add(currentRoom);
            projects = projects.withRooms(rooms);
            projects = projects.withActiveRoomIndex(rooms.size() - 1);
        }
        else if (command.startsWith("opening ") || command.startsWith("проем ")) {
            List<Room> rooms = new ArrayList<>(projects.rooms());
            int activeIndex = projects.activeRoomIndex();
            if (activeIndex < rooms.size()) {
                rooms.set(activeIndex, currentRoom);
                projects = projects.withRooms(rooms);
            }
        }
        else if (command.startsWith("/lang ")) {
            String langCode = command.substring(6).trim();
            Locale newLocale = Locale.forLanguageTag(langCode);
            projects = projects.withLanguage(newLocale);
        }

        loadedProjects.put(chatId, projects);
        repository.save(chatId, projects);
    }

    public String getBotToken() {
        return botToken;
    }

    public Long getChatId() {
        return chatId;
    }
}