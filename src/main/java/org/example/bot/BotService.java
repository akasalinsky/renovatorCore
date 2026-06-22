package org.example.bot;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.cli.RoomCli;
import org.example.i18n.MessageProvider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BotService {

    private final Map<Long, RoomCli> userSessions = new HashMap<>();
    private final Map<Long, Locale> userLocales = new HashMap<>();
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ru");
    private final String botToken;
    private final Long chatId;
    private MessageProvider messageProvider;


    public BotService() {
        // Загружаем переменные окружения из файла .env
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
        Dotenv dotenv = Dotenv.configure().load();
        Locale locale = userLocales.getOrDefault(chatId, DEFAULT_LOCALE);
        messageProvider = new MessageProvider(locale);

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
            userLocales.put(chatId, newLocale);
            // Пересоздаём сессию с новым языком
            userSessions.remove(chatId);
            userSessions.put(chatId, new RoomCli(newLocale));
            messageProvider = new MessageProvider(newLocale);
            return messageProvider.get("lang.set", newLocale.getDisplayName(newLocale)); // потом из MessageProvider
        }

        // Check for welcome commands
        if ("/start".equalsIgnoreCase(trimmedText) || "hello".equalsIgnoreCase(trimmedText)) {
            return messageProvider.get("start.welcome");
        }

        // Check for exit command
        if ("exit".equalsIgnoreCase(trimmedText)) {
            userSessions.remove(chatId);
            return messageProvider.get("goodbye");
        }



        // Get or create RoomCli instance for the user
        RoomCli cli = userSessions.computeIfAbsent(chatId, k -> new RoomCli(locale));

        // Process the command using the user's RoomCli instance
        return cli.execute(trimmedText);
    }

    public String getBotToken() {
        return botToken;
    }

    public Long getChatId() {
        return chatId;
    }
}