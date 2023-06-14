package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Report;
import pro.sky.telegrambotshelter.model.ReportSteps;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.ShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    final Keyboard STANDARD_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            new String[]{"Get info about a shelter", "How to get an animal form the shelter"},
            new String[]{"Send report", "Call a volunteer"})
            .resizeKeyboard(true)
            .selective(true);

    Map<Long, String> shelterChoice = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    private final UserService userService;

    private final Report curentReport = new Report();
    private final String VOLUNTEER_NAME = "VOLONTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";

    private final Long VOLUNTEER_CHAT_ID = 5348835264L;//todo мой айди
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
        shelterChoice = userService.getMapUsersChatIdWithChoice();
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

            } else if(update.message().contact()!=null) {
                contactReceiving(update);
            } else if (update.message().photo()!=null){
                parseReport(update);
            } else {
                messageParser(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * метод для обработки входящих контактов и ответа на это сообщение
     */
    private void contactReceiving(Update update) {
        //Saving contact data to the DBs

        telegramBot.execute(new SendMessage(update.message().from().id(), "Thank you. Our volunteer will contact you!")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP));

    }

    private void stage3ChoiceUpdateParser(Update update) {
    //todo доработать действия при выборе меню
        Long chatId = update.callbackQuery().from().id();
        String messageString;
        switch (update.callbackQuery().data()) {
            case "st3_send_report":
                messageString = "Report sent";
                break;
            case "st3_cancel":
                messageString = "Report canceled. Use menu for repeat";
                break;
            default:
                messageString = "smth went wrong";
        }
        sendMessage(chatId, messageString);
        telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP));
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
        Keyboard inlineKeyboardMarkup = new InlineKeyboardMarkup();

        if(messageText == null){
            replyString = "Sorry, something went wrong try again.";
            sendMessage(chatId, replyString);
            return;
        }

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
                BotCommand[] commandsArr = new BotCommand[]{
                        new BotCommand("/start", "Restart the bot"),
                };
                SetMyCommands commands = new SetMyCommands(commandsArr);
                commands.scope(new BotCommandScopeDefault());
                BaseResponse response = telegramBot.execute(commands); // NEEDS CHECKING

                choiceMessage(
                        chatId,
                        "Please choose a type of shelter you're looking for",
                        new InlineKeyboardMarkup(new InlineKeyboardButton("Cat").callbackData("st0_cat_shelters"),
                                new InlineKeyboardButton("Dog").callbackData("st0_dog_shelters"))
                );
                break;
            case "Get info about a shelter":
                replyString = "Hello, "
                        + userName
                        + "\nWhat would you like to know about a shelter.\n"
                        + shelterService.getGeneralInfo(shelterChoice.get(chatId));
                inlineKeyboardMarkup = new InlineKeyboardMarkup(new InlineKeyboardButton[][]
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
                String shelterChoiceString = shelterChoice.get(chatId);
                replyString = "Hello, "
                        + userName
                        + "\nWhat do you want to know about the adoption process.\n"
                        + shelterService.getGeneralInfo(shelterChoiceString);

                List<InlineKeyboardButton[]> inlineKeyboardButtonsList = new ArrayList<>();
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("How to meet your pet.").callbackData("st2_meeting_recommendations")});
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("What documents you will need.").callbackData("st2_document_list")});
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Recommendations for home (young pet)").callbackData("st2_home_recommendations_young")});
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Recommendations for home (old)").callbackData("st2_home_recommendations_old")});
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Recommendations for home (disability)").callbackData("st2_home_recommendations_disability")});

                if(shelterChoiceString.equals("dogs")){
                    inlineKeyboardButtonsList.add(
                            new InlineKeyboardButton[]{
                                    new InlineKeyboardButton("Cynologist recommendations").callbackData("st2_cynologist_recommendations")});
                    inlineKeyboardButtonsList.add(
                            new InlineKeyboardButton[]{
                                    new InlineKeyboardButton("List of recommended cynologists").callbackData("st2_list_of_cynologists")});
                }
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Why we can deny adoption.").callbackData("st2_why_we_can_deny")});
                inlineKeyboardButtonsList.add(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Send my contact").callbackData("st2_contact_receiving")});
                inlineKeyboardButtonsList.add(
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Call a volunteer").callbackData("st2_call_a_volunteer")});

                InlineKeyboardButton[][] inlineKeyboardButtonsArr = new InlineKeyboardButton[inlineKeyboardButtonsList.size()][1];
                inlineKeyboardButtonsList.toArray(inlineKeyboardButtonsArr);

                inlineKeyboardMarkup = new InlineKeyboardMarkup(inlineKeyboardButtonsArr);

                choiceMessage(chatId, replyString, inlineKeyboardMarkup);

                break;
            case "Send report":
                replyString = "Placeholder for 'Send report'";
                sendMessage(chatId, replyString);
                createReport(chatId);
                break;
            case "Call a volunteer":
                SendContact sendContact = new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME).vcard("Волонтёр приюта Александр")
                        .allowSendingWithoutReply(true);
                telegramBot.execute(sendContact);

                break;
            default:
                if(isReportMessage())
                    parseReport(update);
                else{
                    replyString = "Sorry, something went wrong try again.";
                    sendMessage(chatId, replyString);
                }
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
                userService.updateShelterChoiceByChatId(
                        new User(update.callbackQuery().from().firstName(),chatId),"cats"
                );
                messageString = "You have selected Cat shelters.";
                break;
            case "st0_dog_shelters":
                shelterChoice.put(chatId, "dogs");
                userService.updateShelterChoiceByChatId(
                        new User(update.callbackQuery().from().firstName(),chatId),"dogs"
                );
                messageString = "You have selected Dog shelters.";
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
        sendMessage(chatId, messageString);

        telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP));
    }

    /**
     * Stage1 choice message parser
     */
    private void stage1ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();
        String shelterChoiceString = shelterChoice.get(chatId);
        String messageString= "Shelter Menu";
        switch (update.callbackQuery().data()) {
            case "st1_shelter_info":
                messageString = shelterService.getGeneralInfo(shelterChoiceString);
                break;
            case "st1_shelter_schedule":
                messageString = shelterService.getSchedule(shelterChoiceString);
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
                SendResponse contact = telegramBot.execute(new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME)
                        .allowSendingWithoutReply(true));
                break;
            case "st1_contact_receiving":
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Click the button to send contact info.")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Send contact").requestContact(true))));
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new EditMessageText(chatId, messageId,messageString).replyMarkup(update.callbackQuery().message().replyMarkup()));
    }

    /**
     * Stage2 choice message parser
     */

    private void stage2ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();
        String shelterChoiceString = shelterChoice.get(chatId);
        String messageString= "Adoption Menu";
        switch (update.callbackQuery().data()) {
            case "st2_meeting_recommendations":
                messageString = shelterService.getMeetingRecommendation(shelterChoiceString);
                break;
            case "st2_document_list":
                messageString = shelterService.getDocumentsList(shelterChoiceString);
                break;
            case "st2_home_recommendations_young":
                messageString = shelterService.getHomeRecommendationsYoung(shelterChoiceString);
                break;
            case "st2_home_recommendations_old":
                messageString = shelterService.getHomeRecommendationsOld(shelterChoiceString);
                break;
            case "st2_home_recommendations_disability":
                messageString = "Disability button pressed";
                break;
            case "st2_cynologist_recommendations":
                messageString = shelterService.getCynologistRecommendations(shelterChoiceString);
                break;
            case "st2_list_of_cynologists":
                messageString = "List of cynologists button pressed";
                break;
            case "st2_why_we_can_deny":
                messageString = shelterService.getWhyWeCanDeny(shelterChoiceString);
                break;
            case "st2_call_a_volunteer":
                SendResponse contact = telegramBot.execute(new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME)
                        .allowSendingWithoutReply(true));
                break;
            case "st2_contact_receiving":
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Click the button to send contact info.")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Send contact").requestContact(true))));
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new EditMessageText(chatId, messageId,messageString).replyMarkup(update.callbackQuery().message().replyMarkup()));
    }

    /**
     * Создание сообщения с кнопками по выбору типа приюта
     */
    private void choiceMessage(long chatId, String message, Keyboard inlineKeyboardMarkups) {
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

    /**
     * Метод делает активным отчет, т.е. дальнейшие сообщения являются данными отчета
     */
    private void createReport(Long chatId) {
        String message = "Attach a photo";
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (response.isOk()) {
            logger.info("Message: {} sent", message);
            curentReport.nextStep();
            curentReport.setActive(true);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    /**
     * Метод проверяет идет ли процесс отправки данных отчета
     */
    private boolean isReportMessage() {
        return curentReport.isActive();
    }

    /**
     * Метод формирует отчет(заполняет объект Report) из пошагово введеных пользователем сообщений
     */
    private void parseReport(Update update) {

        long chatId = update.message().chat().id();
        String messageString = "";

        if (curentReport.isActive()) {
            String currentText = update.message().text();
            switch (curentReport.getCurrentStep()) {
                case Photo:
                    if (update.message().photo() == null) {
                        messageString = "You should attach a photo";
                    } else {
                        curentReport.setPhotos(List.of(update.message().photo()));
                        curentReport.nextStep();
                        messageString = "Write a description of current animal's health";
                    }
                    break;
                case Health:
                    if (currentText == null) {
                        messageString = "You should write a description of current animal's health";
                    } else if (currentText.length() <= 50) {
                        messageString = "Your description of health is not full. Write more";
                    } else {
                        curentReport.setHealth(currentText);
                        curentReport.nextStep();
                        messageString = "Write a description of a diet";
                    }
                    break;
                case Diet:
                    if (currentText == null) {
                        messageString = "You should write a diet";
                    } else if (currentText.length() <= 70) {
                        messageString = "Your description of diet is not full. Write more";
                    } else {
                        curentReport.setDiet(currentText);
                        curentReport.nextStep();
                        messageString = "Describe how animal adapts";
                    }
                    break;
                case Adaptation:
                    if (currentText == null) {
                        messageString = "You should describe how animal adapts";
                    } else if (currentText.length() <= 30) {
                        messageString = "Your description of adaptation is not full. Write more";
                    } else {
                        curentReport.setAdaptation(currentText);
                        curentReport.nextStep();
                        messageString = "Describe if animal behaves differently";
                    }
                    break;
                case Changes:
                    if (currentText == null) {
                        messageString = "You should describe if animal behaves differently or write 'Ok'";
                    } else {
                        //сохраняем последнее сообщения для отчета и делаем прием отчета неактивным
                        curentReport.setChanges(currentText);
                        curentReport.setActive(false);
                        curentReport.nextStep();

                        //формируем отчет и отправляем одним сообщением пользователю
                        sendReport(chatId);
                    }
                    break;
                default:
                    SendResponse response = telegramBot.execute(new SendMessage(chatId, "smth went wrong. Try again. Push button 'Send report'"));
            }
            if(!messageString.isEmpty()){
                sendMessage(chatId, messageString);
            }
        }
    }

    /**
     * Метод формирует отчет в одно сообщение и отправляет пользователю
     */
    private void sendReport(long chatId) {

        if(!curentReport.isActive()) {
            //получаем фото для отчета
            String f_id = curentReport.getPhotos().stream()
                    .sorted(Comparator.comparing(PhotoSize::fileSize).reversed())
                    .findFirst()
                    .orElse(null).fileId();
            SendPhoto reportMessage = new SendPhoto(chatId, f_id);

            //добавляем описание к отчету
            reportMessage.caption(curentReport.doTextReport());
            SendResponse response = telegramBot.execute(reportMessage);

            //todo тестовый форвард
            if(response.isOk()){
                ForwardMessage forwardMessage = new ForwardMessage(VOLUNTEER_CHAT_ID, chatId, response.message().messageId());
                telegramBot.execute(forwardMessage);

            }

            //todo деструктор тестовый
            //curentReport.setNullFields();

            //todo Запись даты в базу как последняя отправка

            choiceMessage(chatId, "Show your report. Choose next step", new InlineKeyboardMarkup(new InlineKeyboardButton("Send").callbackData("st3_send_report"),
                    new InlineKeyboardButton("Cancel").callbackData("st3_cancel")));

        }
        else
            sendMessage(chatId, "Report didn't create. Try again");
    }
}