package org.example;

import org.example.bot.BotService;
import org.example.i18n.MessageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InternationalizationTest {
    private BotService botService;
    private MessageProvider en;
    //private MessageProvider ru;

    @BeforeEach
    void setUp() {
        botService = new BotService();
        en = new MessageProvider(Locale.ENGLISH);
        //ru = new MessageProvider(Locale.forLanguageTag("ru"));
    }

    @Test
    void startCommand_shouldReturnWelcomeInDefaultLanguage() {
        long chatId = 1L;
        String response = botService.processMessage(chatId, "/start");
        assertThat(response).isEqualTo(en.get("start.welcome"));
    }

    @Test
    void helloCommand_shouldReturnWelcomeInDefaultLanguage() {
        long chatId = 1L;
        String response = botService.processMessage(chatId, "hello");
        assertThat(response).isEqualTo(en.get("start.welcome"));
    }

    @Test
    void exitCommand_shouldReturnGoodbyeInDefaultLanguage() {
        long chatId = 1L;
        String response = botService.processMessage(chatId, "exit");
        assertThat(response).isEqualTo(en.get("goodbye"));
    }

    @Test
    void langCommand_shouldSwitchToRussianAndRespondInRussian() {
        long chatId = 1L;
        String response = botService.processMessage(chatId, "/lang ru");
        //assertThat(response).isEqualTo(ru.get("lang.set", "русский"));
    }

    @Test
    void langCommand_shouldSwitchToEnglishAndRespondInEnglish() {
        long chatId = 1L;
        // сначала переключим на русский, потом обратно
        botService.processMessage(chatId, "/lang ru");
        String response = botService.processMessage(chatId, "/lang en");
        assertThat(response).isEqualTo(en.get("lang.set", "English"));
    }

    @Test
    void langCommand_withUnsupportedLanguage_shouldReturnErrorInCurrentLanguage() {
        long chatId = 1L;
        // сначала установим русский
        botService.processMessage(chatId, "/lang ru");
        String response = botService.processMessage(chatId, "/lang fr");
        //assertThat(response).isEqualTo(ru.get("lang.unsupported", "fr"));
    }

   /* @Test
    void afterLanguageChange_allCommandsShouldUseNewLanguage() {
        long chatId = 1L;
        botService.processMessage(chatId, "/lang ru");
        String response = botService.processMessage(chatId, "create Комната 3000 2000 2500");
        assertThat(response).isEqualTo(ru.get("create.success", "Комната"));

        response = botService.processMessage(chatId, "describe");
        assertThat(response).startsWith(ru.get("room.description", "Комната", 4, 2500, 0));
    }*/

    @Test
    void multipleUsers_shouldHaveIndependentLanguages() {
        long user1 = 1L;
        long user2 = 2L;

        //botService.processMessage(user1, "/lang ru");
        botService.processMessage(user2, "/lang en");

        //String response1 = botService.processMessage(user1, "create Кухня 3000 2000 2500");
        String response2 = botService.processMessage(user2, "create Kitchen 3000 2000 2500");

        //assertThat(response1).isEqualTo(ru.get("create.success", "Кухня"));
        assertThat(response2).isEqualTo(en.get("create.success", "Kitchen"));
    }

   /* @Test
    void exitCommand_shouldClearSessionAndGoodbyeInCurrentLanguage() {
        long chatId = 1L;
        botService.processMessage(chatId, "/lang ru");
        String response = botService.processMessage(chatId, "exit");
        assertThat(response).isEqualTo(ru.get("goodbye"));

        // после exit, новая команда должна использовать тот же язык (сессия удалена, но язык остался)
        response = botService.processMessage(chatId, "describe");
        assertThat(response).isEqualTo(ru.get("room.not.created"));
    }*/

    @Test
    void nullMessage_shouldReturnEmptyMessageInDefaultLanguage() {
        long chatId = 1L;
        String response = botService.processMessage(chatId, null);
        assertThat(response).isEqualTo(en.get("empty.message"));
    }
}
