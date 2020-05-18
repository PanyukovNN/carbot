package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.service.parser.ParseProcessor;
import com.zylex.carbot.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

public class Bot extends TelegramLongPollingBot {

    private final static Logger LOG = LoggerFactory.getLogger(Bot.class);

    public Bot() {
    }

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        sendMsg(update.getMessage().getChatId().toString(), message);
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимот отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String s) {
        try {
            SendMessage message = new SendMessage();
            message.enableMarkdown(true);
            message.setChatId(chatId);
            message.setText("Начинаю поиск автомобилей... \nПроцесс может занять несколько минут");
            execute(message);

            String output = "";
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CarbotApplication.class)) {
//                message.setText("Приложение полностью инициализировано");
//                execute(message);

                Model model = context.getBean(ModelRepository.class).findByName("VESTA SW CROSS");

//                message.setText("Модель получена");
//                execute(message);

                context.getBean(ParseProcessor.class).parse(model);
                output = context.getBean(View.class).process(model);
                message.setText("\n" + output);
                execute(message);
            } catch (Exception e) {
                ConsoleLogger.writeErrorMessage(e.getMessage(), e);
            }
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e);
        }
    }


    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "LadaCarBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "1240387001:AAGLFmgJ7CFkaZDniXPRBi72QaWFbdf2buE";
    }
}