package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.service.parser.ParseProcessor;
import com.zylex.carbot.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    private final static Logger LOG = LoggerFactory.getLogger(Bot.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${token}")
    private String token;

    private ModelRepository modelRepository;

    private ParseProcessor parseProcessor;

    private View view;

    @Autowired
    public Bot(ModelRepository modelRepository,
               ParseProcessor parseProcessor,
               View view) {
        this.modelRepository = modelRepository;
        this.parseProcessor = parseProcessor;
        this.view = view;
    }

    public Bot() {
    }

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        sendMsg(update.getMessage().getChatId().toString(), message);
    }

    public synchronized void sendMsg(String chatId, String s) {
        try {
            SendMessage message = new SendMessage();
            message.enableMarkdown(true);
            message.setChatId(chatId);
            message.setText("Начинаю поиск автомобилей... \nПроцесс может занять несколько минут");
            execute(message);

            String output = "";
            Model model = modelRepository.findByName("VESTA SW CROSS");

            message.setText("Модель получена");
            execute(message);

            parseProcessor.parse(model);
            output = view.process(model);
            message.setText("\n" + output);
            execute(message);
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e);
            ConsoleLogger.writeErrorMessage(e.getMessage(), e);
        }
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}