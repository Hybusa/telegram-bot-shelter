package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendContact;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.ShelterService;
import pro.sky.telegrambotshelter.service.UserService;

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

    private final UserService userService;

    private final String VOLUNTEER_NAME = "VOLONTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";
    public TelegramBotUpdatesListener(TelegramBot telegramBot, ShelterService shelterService, UserService userService) {
        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.userService = userService;
    }

    /**
     * Инициализация бота
     */
    @PostConstruct
    public void init() {
        shelterChoice = userService.getMapUsersChatId();
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Процессор Апдетов
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.callbackQuery() != null) {
                if (update.callbackQuery().data().startsWith("st0"))
                    shelterChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st1"))
                    stage1ChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st2"))
                    stage2ChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st3"))
                    stage3ChoiceUpdateParser(update);

            } else {
                messageParser(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void stage3ChoiceUpdateParser(Update update) {
    }

    private void stage2ChoiceUpdateParser(Update update) {

    }


    /**
     * ОБработка вхдящего сообщения
     */
    private void messageParser(Update update) {
        logger.info("Processing update: {}", update);
        long chatId = update.message().chat().id();
        String messageText = update.message().text();
        String userName = update.message().chat().firstName();
        String replyString;
        switch (messageText) {
            case "/start":
                if (shelterChoice.containsKey(chatId))
                    telegramBot.execute(new SendMessage(chatId, "Welcome back, " + userName)
                            .replyMarkup(new ReplyKeyboardRemove())
                            .disableNotification(true));
                else {
                    userService.save(new User(userName, chatId));
                    startBot(chatId, userName);
                }
                choiceMessage(chatId,
                        "Please choose a type of shelter you're looking for",
                        new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Cat").callbackData("st0_cat_shelters"),
                                new InlineKeyboardButton("Dog").callbackData("st0_dog_shelters")}));
                break;
            case "Get info about a shelter":

                replyString = "Hello, "
                        + userName
                        + "\nWhat would you like to know about a shelter.\n"
                        + shelterService.getGeneralInfo("cats");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(new InlineKeyboardButton[][]
                        {
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Info about the shelter").callbackData("st1_shelter_info")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Shelter schedule").callbackData("st1_shelter_schedule")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Shelter address").callbackData("st1_shelter_address")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("How to get to the shelter").callbackData("st1_shelter_path")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Security contacts to get a pass").callbackData("st1_shelter_security_pass")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Shelter safety requirements").callbackData("st1_shelter_safety_requirements")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Call a volunteer").callbackData("st1_call_a_volunteer")},
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("Send my contact").callbackData("st1_contact_receiving")}
                        });

                choiceMessage(chatId, replyString, inlineKeyboardMarkup);
                break;
            case "How to get an animal form the shelter":
                replyString = "Placeholder for 'How to get an animal form the shelter'";
                sendMessage(chatId, replyString);
                break;
            case "Send report":
                replyString = "Placeholder for 'Send report'";
                sendMessage(chatId, replyString);
                break;
            case "Call a volunteer":
                SendResponse contact = telegramBot.execute(new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME).vcard("Волонтёр приюта Александр")
                        .allowSendingWithoutReply(true));
                break;
            default:
                replyString = "Sorry, something went wrong try again.";
                sendMessage(chatId, replyString);
        }

    }

    /**
     * Обработка ответа на кнопку выбора типа приюта
     */
    private void shelterChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        String messageString;
        switch (update.callbackQuery().data()) {
            case "st0_cat_shelters":
                shelterChoice.put(chatId, "cats");
                messageString = "You have selected Cat shelters.";
                break;
            case "st0_dog_shelters":
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
                new String[]{"Send report", "Call a volunteer"})
                .resizeKeyboard(true)
                .selective(true);
        telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(replyKeyboardMarkup));
    }

    /**
     * Stage1 choice message parser
     */
    private void stage1ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();
        String shelterChoiceString = "cats";
        String messageString;
        switch (update.callbackQuery().data()) {
            case "st1_shelter_info":
                messageString = shelterService.getGeneralInfo(shelterChoiceString);
                break;
            case "st1_shelter_address":
                messageString = shelterService.getAddress(shelterChoiceString);
                break;
            case "st1_shelter_path":
                messageString = shelterService.getHowToGet(shelterChoiceString);
                break;
            case "st1_shelter_security_pass":
                messageString = shelterService.getSecurityAndPass(shelterChoiceString);
                break;
            case "st1_shelter_safety_requirements":
                messageString = shelterService.getSafety(shelterChoiceString);
                break;
            case "st1_call_a_volunteer":
                messageString = "Call a volunteer placeholder!";
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new EditMessageText(chatId, messageId,"messageString").replyMarkup(update.callbackQuery().message().replyMarkup()));
        //sendMessage(chatId, messageString);
    }

    /**
     * Создание сообщения с кнопками по выбору типа приюта
     */
    private void choiceMessage(long chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkups) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message)
                .replyMarkup(inlineKeyboardMarkups));
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