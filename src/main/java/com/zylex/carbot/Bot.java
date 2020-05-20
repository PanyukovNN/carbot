package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Equipment;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.repository.EquipmentRepository;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.repository.ParsingTimeRepository;
import com.zylex.carbot.view.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Bot extends TelegramLongPollingBot {

    private String botName = "LadaCarBot";

//    @Value("${token}")
//    private String token;
    private String token = System.getenv("TOKEN");

    private ModelRepository modelRepository;

    private EquipmentRepository equipmentRepository;

    private ParsingTimeRepository parsingTimeRepository;

    private View view;

    private Update update;

    @Autowired
    public Bot(ModelRepository modelRepository,
               EquipmentRepository equipmentRepository,
               ParsingTimeRepository parsingTimeRepository,
               View view) {
        this.modelRepository = modelRepository;
        this.equipmentRepository = equipmentRepository;
        this.parsingTimeRepository = parsingTimeRepository;
        this.view = view;
    }

    public Bot() {
    }

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Model model = modelRepository.findByName("VESTA SW CROSS");
        try {
            if (update.hasMessage()) {
                this.update = update;
                chooseEquipmentMessage(model);
            } else if (update.hasCallbackQuery()) {
                Long equipmentId = Long.parseLong(update.getCallbackQuery().getData());
                Equipment equipment = equipmentRepository.findById(equipmentId).orElse(new Equipment());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String output = "Обновлено в " + parsingTimeRepository.findFirstByOrderByDateTimeDesc().getDateTime().format(formatter);
                output += "\n" + view.process(equipment);
                sendMessage(output);
            }
        } catch (TelegramApiException e) {
            ConsoleLogger.writeErrorMessage(e.getMessage(), e);
            try {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("При отправке сообщения возникла ошибка");
                execute(message);
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
                ConsoleLogger.writeErrorMessage(e.getMessage(), e);
            }
        }
    }

    private void sendMessage(String text) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.disableWebPagePreview();
        if (text.length() > 4096) {
            while (text.length() > 4096) {
                String t = text.substring(0, 4095);
                t = t.substring(0, t.lastIndexOf("\n"));
                text = text.substring(t.length());
                message.setText(t);
                execute(message);
            }
        }
        execute(message);
    }

    private void chooseEquipmentMessage(Model model) throws TelegramApiException {
        List<Equipment> equipments = equipmentRepository.findByModel(model);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        for (Equipment equipment : equipments) {
            keyboardRowList.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(equipment.getName())
                    .setCallbackData(String.valueOf(equipment.getId()))));
        }
        keyboard.setKeyboard(keyboardRowList);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(update.getMessage().getChatId());
        message.setText("Выберите комплектацию автомобиля " + model.getName());
        message.setReplyMarkup(keyboard);
        execute(message);
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