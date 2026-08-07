package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.bot.MyTelegramBot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN не найден в .env файле");
        }
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            MyTelegramBot myBot = new MyTelegramBot();
            System.out.println("Запуск бота...");
            botsApplication.registerBot(botToken, myBot);
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}