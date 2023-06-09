package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.service.ShelterService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    Map<Long, String> shelterChoice = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ShelterService shelterService) {
        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
    }

    /**
     * Инициализация бота
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Процессор Апдетов
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.callbackQuery() != null) {
                shelterChoiceUpdateParser(update);
            } else {
                messageParser(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * ОБработка вхдящего сообщения
     */
    private void messageParser(Update update) {
        logger.info("Processing update: {}", update);
        long chatId = update.message().chat().id();
        String messageText = update.message().text();
        String replyString = null;
        switch (messageText) {
            case "/start":
                if (shelterChoice.containsKey(chatId))
                    telegramBot.execute(new SendMessage(chatId, "Welcome back, " + update.message().chat().firstName())
                            .replyMarkup(new ReplyKeyboardRemove())
                            .disableNotification(true));
                else
                    startBot(chatId, update.message().chat().firstName());
                choiceMessage(chatId);
                break;
            case "Get info about a shelter":
                replyString = shelterService.getGeneralInfo(shelterChoice.get(chatId));
                break;
            case "How to get an animal form the shelter":
                replyString = "Placeholder for 'How to get an animal form the shelter'";
                break;
            case "Get pet info":
                replyString = "Placeholder for 'Get pet info'";
                break;
            case "Call a volunteer":
                replyString = "Placeholder for 'Call a volunteer'";
                break;
            default:
                replyString = "Sorry, something went wrong try again.";
        }
        sendMessage(chatId, replyString);
    }

    /**
     * Обработка ответа на кнопку выбора типа приюта
     */
    private void shelterChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        String messageString;
        switch (update.callbackQuery().data()) {
            case "cat_shelters":
                shelterChoice.put(chatId, "cats");
                messageString = "You have selected Cat shelters.";
                break;
            case "dog_shelters":
                shelterChoice.put(chatId, "dogs");
                messageString = "You have selected Dog shelters.";
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
        sendMessage(chatId, messageString);
        Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Get info about a shelter", "How to get an animal form the shelter"},
                new String[]{"Get pet info", "Call a volunteer"})
                .resizeKeyboard(true)
                .selective(true);
        telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(replyKeyboardMarkup));
    }

    /**
     * Создание сообщения с кнопками по выбору типа приюта
     */
    private void choiceMessage(long chatId) {


        SendResponse response = telegramBot.execute(new SendMessage(chatId, "Please choose a type of shelter you're looking for")
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Cat").callbackData("cat_shelters")
                        , new InlineKeyboardButton("Dog").callbackData("dog_shelters")})));
    }

    /**
     * Сообщение на старте бота
     */
    private void startBot(long chatId, String userName) {
        sendMessage(chatId, "Hello, " + userName + "! \n" + botInfo);
    }

    /**
     * Отправка сообщения ботом
     */
    private void sendMessage(long chatId, String message) {
        if (message == null)
            return;
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (response.isOk()) {
            logger.info("Message: {} sent", message);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }
}