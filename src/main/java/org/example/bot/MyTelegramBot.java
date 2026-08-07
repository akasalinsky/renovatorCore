package org.example.bot;

import org.example.repository.FileUserProjectRepository;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final OkHttpTelegramClient telegramClient;
    private final BotService botService;
    private final String JSON_DIR = "./user_data";

    public MyTelegramBot() {
        try {
        this.botService = new BotService(new FileUserProjectRepository(JSON_DIR));
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать хранилище данных: " + e.getMessage(), e);
        }

        String botToken = botService.getBotToken();

        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN не найден в .env файле");
        }

        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            String response = botService.processMessage(chatId, text);

            if (response != null && response.startsWith("IMAGE:")) {
                String base64Image = response.substring(6);
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                // Отправляем как фото
                SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), new InputFile(new ByteArrayInputStream(imageBytes), "plan.png"));
                try {
                    telegramClient.execute(sendPhoto);
                } catch (TelegramApiException e) {
                    System.err.println("Ошибка отправки фото: " + e.getMessage());
                    e.printStackTrace();
                }
                }
            else {
                SendMessage sendMessage = new SendMessage(String.valueOf(chatId), response);

                if (response.startsWith("<pre>")) {
                    sendMessage.setParseMode("HTML");
                }

                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    System.err.println("Ошибка отправки сообщения: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


}