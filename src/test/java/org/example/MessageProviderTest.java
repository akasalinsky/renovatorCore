package org.example;

import org.example.i18n.MessageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageProviderTest {

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
            assertThat(messageProviderEn.get("start.welcome")).isEqualTo("Welcome! Use 'create' to start building your room.");
            assertThat(messageProviderEn.get("create.success")).isEqualTo("Room ''{0}'' created.");
            assertThat(messageProviderEn.get("floor.area")).isEqualTo("Floor area: {0} m²");
            assertThat(messageProviderEn.get("unknown.command")).isEqualTo("Unknown command: {0}");
            assertThat(messageProviderEn.get("goodbye")).isEqualTo("Goodbye!");
        }
    }

    @Nested
    class ParameterSubstitutionTests {
        @Test
        void shouldSubstituteParametersInEnglishMessage() {
            String expected = "Room 'Hall' created.";
            String actual = messageProviderEn.get("create.success", "Hall");
            assertThat(actual).isEqualTo(expected);
        }
/*
        @Test
        void shouldSubstituteParametersInRussianMessage() {
            String expected = "Комната 'Hall' создана.";
            String actual = messageProviderRu.get("create.success", "Hall");
            assertThat(actual).isEqualTo(expected);
        }*/
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