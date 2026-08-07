package org.example;

import org.example.bot.BotService;
import org.example.cli.RoomCli;
import org.example.i18n.MessageProvider;
import org.example.repository.FileUserProjectRepository;
import org.example.repository.UserProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InternationalizationTest {

    @Nested
    class BotServiceTests {

        private BotService botService;
        private MessageProvider en;
        private MessageProvider ru;
        private UserProjectRepository repository;


        @BeforeEach
        void setUp() {
            this.repository = new FileUserProjectRepository("path");
            botService = new BotService(repository);
            en = new MessageProvider(Locale.ENGLISH);
            ru = new MessageProvider(Locale.forLanguageTag("ru"));
        }

        @Test
        void afterLanguageChange_roomShouldBePreserved() {
            long chatId = 1L;

            botService.processMessage(chatId, "create Комната 4000 3000 2700");

            String descBefore = botService.processMessage(chatId, "describe");
            assertThat(descBefore).contains("Комната");

            String langResponse = botService.processMessage(chatId, "/lang en");
            assertThat(langResponse).isEqualTo(en.get("lang.set", "English"));

            String descAfter = botService.processMessage(chatId, "describe");
            assertThat(descAfter).contains("Комната");
        }

        @Test
        void startCommand_shouldReturnWelcomeInDefaultLanguage() {
            long chatId = 1L;
            String response = botService.processMessage(chatId, "/start");
            assertThat(response).isEqualTo(ru.get("start.welcome"));
        }

        @Test
        void helloCommand_shouldReturnWelcomeInDefaultLanguage() {
            long chatId = 1L;
            String response = botService.processMessage(chatId, "hello");
            assertThat(response).isEqualTo(ru.get("start.welcome"));
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
            assertThat(response).isEqualTo(ru.get("lang.set", "русский"));
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
            assertThat(response).isEqualTo(ru.get("lang.unsupported", "fr"));
        }

        @Test
        void afterLanguageChange_allCommandsShouldUseNewLanguage() {
            long chatId = 1L;
            botService.processMessage(chatId, "/lang ru");
            String response = botService.processMessage(chatId, "create Комната 3000 2000 2500");
            assertThat(response).isEqualTo(ru.get("create.success", "Комната"));

            response = botService.processMessage(chatId, "describe");
            assertThat(response).startsWith(ru.get("room.description", "Комната", 4, 2500, 0));
        }

        @Test
        void multipleUsers_shouldHaveIndependentLanguages() {
            long user1 = 1L;
            long user2 = 2L;

            botService.processMessage(user1, "/lang ru");
            botService.processMessage(user2, "/lang en");

            String response1 = botService.processMessage(user1, "create Кухня 3000 2000 2500");
            String response2 = botService.processMessage(user2, "create Kitchen 3000 2000 2500");

            assertThat(response1).isEqualTo(ru.get("create.success", "Кухня"));
            assertThat(response2).isEqualTo(en.get("create.success", "Kitchen"));
        }

        @Test
        void exitCommand_shouldClearSessionAndGoodbyeInCurrentLanguage() {
            long chatId = 1L;
            botService.processMessage(chatId, "/lang ru");
            String response = botService.processMessage(chatId, "exit");
            assertThat(response).isEqualTo(ru.get("goodbye"));

            // после exit, новая команда должна использовать тот же язык (сессия удалена, но язык остался)
            response = botService.processMessage(chatId, "describe");
            assertThat(response).isEqualTo(ru.get("room.not.created"));
        }

        @Test
        void nullMessage_shouldReturnEmptyMessageInDefaultLanguage() {
            long chatId = 1L;
            String response = botService.processMessage(chatId, null);
            assertThat(response).isEqualTo(en.get("empty.message"));
        }
    }

    @Nested
    class MessageProviderTests {

        private MessageProvider messageProviderEn;
        private MessageProvider messageProviderRu;

        @BeforeEach
        void setUp() {
            messageProviderEn = new MessageProvider(Locale.ENGLISH);
            messageProviderRu = new MessageProvider(Locale.forLanguageTag("ru"));
        }

        @Nested
        class EnglishLocaleTests {
            @Test
            void shouldReturnCorrectEnglishMessages() {
                assertThat(messageProviderEn.get("start.welcome")).isEqualTo("""
                Welcome! I'm Room Planner Bot.
                
                I can help you calculate floor area, wall surfaces, and wallpaper rolls for your room.
                
                Available commands:
                • create <name> <length> <width> <height> — create a new room (dimensions in mm, openings in m²)
                • describe — show room description and areas
                • plan [scale] — draw an ASCII floor plan (default scale 500 mm/char)
                • area — show floor area
                • wallpaper <roll width mm> <roll length mm> — calculate number of wallpaper rolls
                • lang en|ru — switch language
                • help — show this instruction again
                • exit — exit (in bot, session resets; in CLI, program terminates)
                
                You can switch language at any time by typing /lang ru or /lang en.""");
                assertThat(messageProviderEn.get("create.success")).isEqualTo("Room {0} created.");
                assertThat(messageProviderEn.get("floor.area")).isEqualTo("Floor area: {0} m²");
                assertThat(messageProviderEn.get("unknown.command")).isEqualTo("Unknown command: {0}");
                assertThat(messageProviderEn.get("goodbye")).isEqualTo("Goodbye!");
            }
        }

        @Nested
        class ParameterSubstitutionTests {
            @Test
            void shouldSubstituteParametersInEnglishMessage() {
                String expected = "Room Hall created.";
                String actual = messageProviderEn.get("create.success", "Hall");
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void shouldSubstituteParametersInRussianMessage() {
                String expected = "Комната Hall создана.";
                String actual = messageProviderRu.get("create.success", "Hall");
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class MissingKeyTests {
            @Test
            void shouldThrowExceptionForMissingKey() {
                assertThatThrownBy(() -> messageProviderEn.get("non.existent.key"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("non.existent.key");
            }
        }
    }

    @Nested
    class RoomCliI18nTests {

        private MessageProvider en;
        private MessageProvider ru;
        private RoomCli enCli;
        private RoomCli ruCli;

        @BeforeEach
        void setUp() {
            en = new MessageProvider(Locale.ENGLISH);
            ru = new MessageProvider(Locale.forLanguageTag("ru"));
            enCli = new RoomCli(Locale.ENGLISH);
            ruCli = new RoomCli(Locale.forLanguageTag("ru"));
        }

        @Test
        void createCommand_shouldReturnLocalizedSuccess() {
            String resultEn = enCli.execute("create Hall 4000 3000 2500");
            assertThat(resultEn).isEqualTo(en.get("create.success", "Hall"));

            String resultRu = ruCli.execute("create Зал 4000 3000 2500");
            assertThat(resultRu).isEqualTo(ru.get("create.success", "Зал"));
        }

        @Test
        void createCommand_withMissingArgs_shouldReturnLocalizedUsage() {
            String resultEn = enCli.execute("create Hall 4000");
            assertThat(resultEn).isEqualTo(en.get("create.usage"));

            String resultRu = ruCli.execute("create Зал 4000");
            assertThat(resultRu).isEqualTo(ru.get("create.usage"));
        }

        @Test
        void describeCommand_withoutRoom_shouldReturnLocalizedError() {
            assertThat(enCli.execute("describe")).isEqualTo(en.get("room.not.created"));
            assertThat(ruCli.execute("describe")).isEqualTo(ru.get("room.not.created"));
        }

        @Test
        void describeCommand_withRoom_shouldReturnLocalizedDescription() {
            enCli.execute("create Hall 4000 3000 2700");
            String resultEn = enCli.execute("describe");
            assertThat(resultEn).isEqualTo(en.get("room.description", "Hall", 4, 2700, 0));

            ruCli.execute("create Зал 4000 3000 2700");
            String resultRu = ruCli.execute("describe");
            assertThat(resultRu).isEqualTo(ru.get("room.description", "Зал", 4, 2700, 0));
        }

        @Test
        void areaCommand_shouldReturnLocalizedFloorArea() {
            enCli.execute("create Hall 4000 3000 2500");
            String resultEn = enCli.execute("area");
            assertThat(resultEn).isEqualTo(en.get("floor.area", "12.00"));

            ruCli.execute("create Зал 4000 3000 2500");
            String resultRu = ruCli.execute("area");
            assertThat(resultRu).isEqualTo(ru.get("floor.area", "12.00"));
        }

        @Test
        void wallpaperCommand_shouldReturnLocalizedRollCount() {
            enCli.execute("create Hall 4000 3000 2500");
            String resultEn = enCli.execute("wallpaper 530 10000");
            assertThat(resultEn).matches(en.get("wallpaper.rolls", "\\d+"));

            ruCli.execute("create Зал 4000 3000 2500");
            String resultRu = ruCli.execute("wallpaper 530 10000");
            String expectedPattern = ru.get("wallpaper.rolls").replace("{0}", "\\d+");
            assertThat(resultRu).matches(expectedPattern);
        }

        @Test
        void wallpaperCommand_withMissingArgs_shouldReturnLocalizedUsage() {
            enCli.execute("create Hall 4000 3000 2500");
            assertThat(enCli.execute("wallpaper 530")).isEqualTo(en.get("wallpaper.usage"));

            ruCli.execute("create Зал 4000 3000 2500");
            assertThat(ruCli.execute("wallpaper 530")).isEqualTo(ru.get("wallpaper.usage"));
        }

        @Nested
        class PlanCommandTests {

            @Test
            void planCommand_withInvalidScale_shouldReturnLocalizedUsage() {
                enCli.execute("create Hall 4000 3000 2500");
                ruCli.execute("create Зал 4000 3000 2500");

                String resultEn = enCli.execute("plan invalid");
                String resultRu = ruCli.execute("plan нечисло");

                assertThat(resultEn).isEqualTo(en.get("plan.usage"));
                assertThat(resultRu).isEqualTo(ru.get("plan.usage"));
            }

            @Test
            void planCommand_withoutRoom_shouldReturnRoomNotCreated() {
                String resultEn = enCli.execute("plan");
                String resultRu = ruCli.execute("plan");

                assertThat(resultEn).isEqualTo(en.get("room.not.created"));
                assertThat(resultRu).isEqualTo(ru.get("room.not.created"));
            }
        }

        @Test
        void exitCommand_shouldReturnLocalizedGoodbye() {
            assertThat(enCli.execute("exit")).isEqualTo(en.get("goodbye"));
            assertThat(ruCli.execute("exit")).isEqualTo(ru.get("goodbye"));
        }

        @Test
        void unknownCommand_shouldReturnLocalizedUnknown() {
            String cmd = "foo bar";
            assertThat(enCli.execute(cmd)).isEqualTo(en.get("unknown.command", cmd));
            assertThat(ruCli.execute(cmd)).isEqualTo(ru.get("unknown.command", cmd));
        }

        @Test
        void emptyCommand_shouldReturnLocalizedEmptyMessage() {
            assertThat(enCli.execute("")).isEqualTo(en.get("empty.message"));
            assertThat(ruCli.execute("")).isEqualTo(ru.get("empty.message"));
            assertThat(enCli.execute("   ")).isEqualTo(en.get("empty.message"));
        }
    }
}