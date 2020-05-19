package com.zylex.carbot;

import com.zylex.carbot.controller.logger.ConsoleLogger;
import com.zylex.carbot.model.Equipment;
import com.zylex.carbot.model.Model;
import com.zylex.carbot.repository.EquipmentRepository;
import com.zylex.carbot.repository.ModelRepository;
import com.zylex.carbot.service.parser.ParseProcessor;
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

import java.util.*;

@Component
public class Bot extends TelegramLongPollingBot {

//    @Value("${bot.name}")
    private String botName = "LadaCarBot";
//
//    @Value("${token}")
    private String token = System.getenv("TOKEN");

    private ModelRepository modelRepository;

    private EquipmentRepository equipmentRepository;

    private ParseProcessor parseProcessor;

    private View view;

    @Autowired
    public Bot(ModelRepository modelRepository,
               EquipmentRepository equipmentRepository,
               ParseProcessor parseProcessor,
               View view) {
        this.modelRepository = modelRepository;
        this.equipmentRepository = equipmentRepository;
        this.parseProcessor = parseProcessor;
        this.view = view;
    }

    public Bot() {
    }

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    private Long chatId;

    @Override
    public void onUpdateReceived(Update update) {
        Model model = modelRepository.findByName("VESTA SW CROSS");
        try {
            if (update.hasMessage()) {
                this.chatId = update.getMessage().getChatId();
                chooseEquipmentMessage(model);
            } else if (update.hasCallbackQuery()) {
                String equipmentName = update.getCallbackQuery().getData();
                Equipment equipment = equipmentRepository.findByName(equipmentName);

                execute(new SendMessage()
                        .setChatId(chatId)
                        .setText("Начинаю поиск автомобилей... \nПроцесс может занять несколько минут"));

                parseProcessor.parse(model);
                String output = "\n" + view.process(equipment);

                execute(new SendMessage()
                        .setText(output)
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            }
        } catch (TelegramApiException e) {
            ConsoleLogger.writeErrorMessage(e.getMessage(), e);
        }
    }

    private void chooseEquipmentMessage(Model model) throws TelegramApiException {
        List<Equipment> equipments = equipmentRepository.findByModel(model);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        for (Equipment equipment : equipments) {
            keyboardRowList.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(equipment.getName())
                    .setCallbackData(equipment.getName())));
        }
        keyboard.setKeyboard(keyboardRowList);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Выберите комплектацию");
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