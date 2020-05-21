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

//    private String botName = "TestLadaCarBot";
//
//    @Value("${token}")
//    private String token;

    private String botName = "LadaCarBot";

    private String token = System.getenv("TOKEN");

    private ModelRepository modelRepository;

    private EquipmentRepository equipmentRepository;

    private ParsingTimeRepository parsingTimeRepository;

    private View view;

    private final String EQUIPMENT_TAG = "equipment:";

    private final String COLOR_TAG = "color:";

    private final String SERAPATOR = "_";

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
                Long chatId = update.getMessage().getChatId();
                chooseEquipmentMessage(model, chatId);
            } else if (update.hasCallbackQuery()) {
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                String callbackQuery = update.getCallbackQuery().getData();
                // callbackQuery example: "equipment:1|color:Ледниковый"
                if (!callbackQuery.contains(COLOR_TAG)) {
                    Long equipmentId = Long.parseLong(callbackQuery.replace(EQUIPMENT_TAG, ""));
                    chooseColor(chatId, equipmentId);
                } else {
                    String[] queryParts = callbackQuery.split(SERAPATOR);
                    Long equipmentId = Long.parseLong(queryParts[0].replace(EQUIPMENT_TAG, ""));
                    String colorName = queryParts[1].replace(COLOR_TAG, "");

                    Equipment equipment = equipmentRepository.findById(equipmentId).get();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String output = "\n" + view.buildColorOutput(equipment, colorName);
                    output += "\nОбновлено в " + parsingTimeRepository.findFirstByOrderByDateTimeDesc().
                            getDateTime()
                            .plusHours(3)
                            .format(formatter);
                    sendMessage(output, chatId);
                }
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

    private void sendMessage(String text, Long chatId) throws TelegramApiException {
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
        message.setText(text);
        execute(message);
    }

    private void chooseEquipmentMessage(Model model, Long chatId) throws TelegramApiException {
        List<Equipment> equipments = equipmentRepository.findByModel(model);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        for (Equipment equipment : equipments) {
            keyboardRowList.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(equipment.getName())
                    .setCallbackData("equipment:" + equipment.getId())));
        }
        keyboard.setKeyboard(keyboardRowList);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Выберите комплектацию автомобиля " + model.getName());
        message.setReplyMarkup(keyboard);
        execute(message);
    }

    private void chooseColor(Long chatId, Long equipmentId) throws TelegramApiException {
        Map<String, String> colors = view.getColors();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRowList = new ArrayList<>();
        String callbackQuery = EQUIPMENT_TAG + equipmentId + SERAPATOR + COLOR_TAG;
        for (String colorName : colors.keySet()) {
            keyboardRowList.add(Collections.singletonList(new InlineKeyboardButton()
                    .setText(colorName + " (" + colors.get(colorName) + ")")
                    .setCallbackData(callbackQuery + colorName)));
        }
        keyboard.setKeyboard(keyboardRowList);

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Выберите цвет");
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