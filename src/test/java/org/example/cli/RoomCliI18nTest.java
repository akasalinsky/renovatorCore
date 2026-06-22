package org.example.cli;

import org.example.i18n.MessageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("RoomCli i18n tests")
class RoomCliI18nTest {

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
        assertThat(resultEn).matches(en.get("wallpaper.rolls", "\\d+")); // using regex, but we can check contains

       ruCli.execute("create Зал 4000 3000 2500");
       String resultRu = ruCli.execute("wallpaper 530 10000");
       //assertThat(resultRu).matches(ru.get("wallpaper.rolls", "\\d+"));
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
    @DisplayName("Plan command tests")
    class PlanCommandTests {

        @Test
        @DisplayName("Should return localized usage when scale is not a number")
        void planCommand_withInvalidScale_shouldReturnLocalizedUsage() {
            // given
            enCli.execute("create Hall 4000 3000 2500");
            ruCli.execute("create Зал 4000 3000 2500");

            // when
            String resultEn = enCli.execute("plan invalid");
            String resultRu = ruCli.execute("plan нечисло");

            // then
            assertThat(resultEn).isEqualTo(en.get("plan.usage"));
            assertThat(resultRu).isEqualTo(ru.get("plan.usage"));
        }

        @Test
        @DisplayName("Should return room not created when no room exists")
        void planCommand_withoutRoom_shouldReturnRoomNotCreated() {
            // given: no room created

            // when
            String resultEn = enCli.execute("plan");
            String resultRu = ruCli.execute("plan");

            // then
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