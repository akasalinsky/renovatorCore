package org.example.bot;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final OkHttpTelegramClient telegramClient;
    private final BotService botService;

    public MyTelegramBot() {
        this.botService = new BotService();
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
            //if (response.equals(""))

            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), response);
            if(response.startsWith("<pre>")){sendMessage.setParseMode("HTML");}

            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка отправки сообщения: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}