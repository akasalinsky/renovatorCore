package org.example.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageProvider {
    private final ResourceBundle resourceBundle;

    public MessageProvider(Locale locale) {
            this.resourceBundle = ResourceBundle.getBundle("messages", locale, UTF8Control.INSTANCE);
        }

        public String get(String key) {
            try {
                return resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                throw new IllegalArgumentException("Key '" + key + "' not found in resource bundle.", e);
            }
        }

        public String get(String key, Object... args) {
            String template = get(key); // Используем первый метод для получения строки
            // Подставляем аргументы в шаблон
            return MessageFormat.format(template, args);
        }
}
