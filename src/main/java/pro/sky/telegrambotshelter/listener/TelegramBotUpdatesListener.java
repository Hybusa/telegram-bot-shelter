package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Report;
import pro.sky.telegrambotshelter.enums.ButtonCommands;
import pro.sky.telegrambotshelter.enums.Phrases;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.ShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    private final Keyboard STANDARD_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            new String[]{"Get info about a shelter", "How to get an animal form the shelter"},
            new String[]{"Send report", "Call a volunteer"})
            .resizeKeyboard(true)
            .selective(true);

    private Map<Long, String> shelterChoice;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    private final UserService userService;

    private final Report currentReport = new Report();
    private final String VOLUNTEER_NAME = "VOLONTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";
    private final Long VOLUNTEER_CHAT_ID = 213L;//todo поменять id

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
        BotCommand[] commandsArr = new BotCommand[]{
                new BotCommand("/start", Phrases.RESTART_THE_BOT.toString())
        };
        SetMyCommands commands = new SetMyCommands(commandsArr);
        commands.scope(new BotCommandScopeDefault());
        telegramBot.execute(commands);
    }

    /**
     * Процессор Апдейтов
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
            } else if (update.message() != null) {
                if (update.message().contact() != null)
                    contactReceiving(update);
                else
                    messageParser(update);
            } else if (update.myChatMember() != null) {
                if (update.myChatMember().newChatMember().status() == ChatMember.Status.kicked) {
                    userService.deleteUsersByChatId(update.message().chat().id());
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * метод для обработки входящих контактов и ответа на это сообщение
     */
    private void contactReceiving(Update update) {
        //TODO Saving contact data to the DBs

        SendMessage contactReceivingResponse = new SendMessage(update.message().from().id(),
                Phrases.CONTACT_RECEIVED.toString())
                .replyMarkup(STANDARD_KEYBOARD_MARKUP);

        SendResponse response = telegramBot.execute(contactReceivingResponse);

        if (response.isOk())
            logger.info(Phrases.RESPONSE_STATUS.toString(), response);
        else
            logger.error(Phrases.ERROR_SENDING.toString() + response.errorCode());
    }

    /**
     * Stage3 обработка реакции пользователя после сформированного им отчета
     */
    private void stage3ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        String messageString = "";

        switch (update.callbackQuery().data()) {
            case "st3_send_report":
                sendReportVolunteer();
                break;
            case "st3_cancel":
                currentReport.setNullFields();
                messageString = "Report canceled";
                break;
            default:
                messageString = "smth went wrong";
        }
        if(!messageString.isEmpty())
            sendMessage(chatId, messageString);
        telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP));
     }

    /**
     * ОБработка вхдящего сообщения
     */
    private void messageParser(Update update) {
        logger.info(Phrases.PROCESSING_UPDATE.toString(), update);
        long chatId = update.message().chat().id();
        String messageText = update.message().text();
        String userName = update.message().chat().firstName();
        String replyString;
        Keyboard inlineKeyboardMarkup;

        switch (messageText) {
            case "/start":
                if (shelterChoice.containsKey(chatId)) {
                    restartBot(chatId, userName);
                } else {
                    userService.save(new User(userName, chatId));
                    shelterChoice.put(chatId, null);
                    startBot(chatId, userName);
                }

                choiceMessage(
                        chatId,
                        Phrases.SHELTER_CHOICE_MESSAGE.toString(),
                        new InlineKeyboardMarkup(new InlineKeyboardButton("Cat")
                                .callbackData(ButtonCommands.STAGE_0_CATS.toString()),
                                new InlineKeyboardButton("Dog")
                                        .callbackData(ButtonCommands.STAGE_0_DOGS.toString()))
                );
                break;
            case "Get info about a shelter":
                replyString = "Hello, "
                        + userName
                        + "\nWhat would you like to know about a shelter.\n"
                        + shelterService.getGeneralInfo(shelterChoice.get(chatId));
                inlineKeyboardMarkup = new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Info about the shelter")
                                .callbackData("st1_shelter_info")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Shelter schedule")
                                        .callbackData("st1_shelter_schedule")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Shelter address")
                                        .callbackData("st1_shelter_address")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("How to get to the shelter")
                                        .callbackData("st1_shelter_path")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Security contacts to get a pass")
                                        .callbackData("st1_shelter_security_pass")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Shelter safety requirements")
                                        .callbackData("st1_shelter_safety_requirements")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Call a volunteer")
                                        .callbackData("st1_call_a_volunteer")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Send my contact")
                                        .callbackData("st1_contact_receiving")});


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

                if (shelterChoiceString.equals("dogs")) {
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
                inlineKeyboardMarkup = new InlineKeyboardMarkup(
                        new InlineKeyboardButton("Everything is right. SendReport")
                                .callbackData("st3_send_report")
                );
                choiceMessage(update.message().chat().id(), "REPORT",inlineKeyboardMarkup);
                break;
            case "Call a volunteer":
                SendContact sendContact = new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME).vcard("Волонтёр приюта Александр")
                        .allowSendingWithoutReply(true);
                SendResponse contactResponse = telegramBot.execute(sendContact);

                if (contactResponse.isOk()) {
                    logger.info("Message: {} sent", sendContact);
                } else {
                    logger.error("Error sending. Code: " + contactResponse.errorCode());
                }
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
     * Перезапуск бота, или возвращающийся пользволтель
     */
    private void restartBot(long chatId, String userName) {
        SendMessage message = new SendMessage(chatId, "Welcome back, " + userName)
                .replyMarkup(new ReplyKeyboardRemove())
                .disableNotification(true);
        SendResponse response = telegramBot.execute(message);
        if (response.isOk())
            logger.info("Response is {}", response);
        else
            logger.error("Error : " + response.errorCode());
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
                        new User(update.callbackQuery().from().firstName(), chatId), "cats"
                );
                messageString = "You have selected Cat shelters.";
                break;
            case "st0_dog_shelters":
                shelterChoice.put(chatId, "dogs");
                userService.updateShelterChoiceByChatId(
                        new User(update.callbackQuery().from().firstName(), chatId), "dogs"
                );
                messageString = "You have selected Dog shelters.";
                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
        sendMessage(chatId, messageString);


        SendMessage sendMenuMessage = new SendMessage(chatId, "Please, choose an option from the menu")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP);
        SendResponse response = telegramBot.execute(sendMenuMessage);

        if (response.isOk())
            logger.info("Response is {}", response);
        else
            logger.error("Error : " + response.errorCode());
    }

    /**
     * Stage1 choice message parser
     */
    private void stage1ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();
        String shelterChoiceString = shelterChoice.get(chatId);
        String messageString = "Shelter Menu";
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

                if (contact.isOk())
                    logger.info("Response is {}", contact);
                else
                    logger.error("Error : " + contact.errorCode());

                break;
            case "st1_contact_receiving":
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Click the button to send contact info.")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Send contact").requestContact(true))));

                if (response.isOk())
                    logger.info("Response is {}", response);
                else
                    logger.error("Error : " + response.errorCode());

                break;
            default:
                messageString = "smth went wrong";
        }
       telegramBot.execute(new EditMessageText(chatId, messageId, messageString).replyMarkup(update.callbackQuery().message().replyMarkup()));
    }

    /**
     * Stage2 choice message parser
     */

    private void stage2ChoiceUpdateParser(Update update) {
        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();
        String shelterChoiceString = shelterChoice.get(chatId);
        String messageString = "Adoption Menu";
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
                //TODO DISABILITY FROM DB
                messageString = "Disability button pressed";
                break;
            case "st2_cynologist_recommendations":
                messageString = shelterService.getCynologistRecommendations(shelterChoiceString);
                break;
            case "st2_list_of_cynologists":
                //TODO DB STUFF
                messageString = "List of cynologists button pressed";
                break;
            case "st2_why_we_can_deny":
                messageString = shelterService.getWhyWeCanDeny(shelterChoiceString);
                break;
            case "st2_call_a_volunteer":
                SendResponse contact = telegramBot.execute(new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME)
                        .allowSendingWithoutReply(true));

                if (contact.isOk())
                    logger.info(Phrases.RESPONSE_STATUS.toString(), contact);
                else
                    logger.error(Phrases.RESPONSE_STATUS.toString() + contact.errorCode());

                break;
            case "st2_contact_receiving":
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Click the button to send contact info.")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Send contact").requestContact(true))));

                if (response.isOk())
                    logger.info(Phrases.RESPONSE_STATUS.toString(), response);
                else
                    logger.error("Error sending. Code: " + response.errorCode());

                break;
            default:
                messageString = "smth went wrong";
        }
        telegramBot.execute(new EditMessageText(chatId, messageId, messageString).replyMarkup(update.callbackQuery().message().replyMarkup()));
    }

    /**
     * Создание сообщения с кнопками по выбору типа приюта
     */
    private void choiceMessage(long chatId, String message, Keyboard inlineKeyboardMarkups) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message)
                .replyMarkup(inlineKeyboardMarkups));

        if (response.isOk())
            logger.info(Phrases.RESPONSE_STATUS.toString(), response);
        else
            logger.error(Phrases.ERROR_SENDING.toString() + response.errorCode());
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
        if (response.isOk())
            logger.info("Message: {} sent", message);
        else
            logger.error(Phrases.ERROR_SENDING.toString() + response.errorCode());
    }

    /**
     * Метод делает активным отчет, т.е. дальнейшие сообщения являются данными отчета
     */
    private void createReport(Long chatId) {
        String message = "Attach a photo";
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (response.isOk()) {
            logger.info("Message: {} sent", message);
            currentReport.nextStep();
            currentReport.setActive(true);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    /**
     * Метод проверяет идет ли процесс отправки данных отчета
     */
    private boolean isReportMessage() {
        return currentReport.isActive();
    }

    /**
     * Метод формирует отчет(заполняет объект Report) из пошагово введеных пользователем сообщений
     */
    private void parseReport(Update update) {

        long chatId = update.message().chat().id();
        String messageString = "";

        if (currentReport.isActive()) {
            String currentText = update.message().text();
            switch (currentReport.getCurrentStep()) {
                case Photo:
                    if (update.message().photo() == null) {
                        messageString = "You should attach a photo";
                    } else {
                        currentReport.setPhotos(List.of(update.message().photo()));
                        currentReport.nextStep();
                        messageString = "Write a description of current animal's health";
                    }
                    break;

                case Health:
                    if (currentText == null) {
                        messageString = "You should write a description of current animal's health";
                    } else if (currentText.length() <= 50) {
                        messageString = "Your description of health is not full. Write more";
                    } else {
                        currentReport.setHealth(currentText);
                        currentReport.nextStep();
                        messageString = "Write a description of a diet";
                    }
                    break;

                case Diet:
                    if (currentText == null) {
                        messageString = "You should write a diet";
                    } else if (currentText.length() <= 70) {
                        messageString = "Your description of diet is not full. Write more";
                    } else {
                        currentReport.setDiet(currentText);
                        currentReport.nextStep();
                        messageString = "Describe how animal adapts";
                    }
                    break;
                case Adaptation:
                    if (currentText == null) {
                        messageString = "You should describe how animal adapts";
                    } else if (currentText.length() <= 30) {
                        messageString = "Your description of adaptation is not full. Write more";
                    } else {
                        currentReport.setAdaptation(currentText);
                        currentReport.nextStep();
                        messageString = "Describe if animal behaves differently";
                    }
                    break;

                case Changes:
                    if (currentText == null) {
                        messageString = "You should describe if animal behaves differently or write 'Ok'";
                    } else {
                        //сохраняем последнее сообщения для отчета и делаем прием отчета неактивным
                        currentReport.setChanges(currentText);
                        currentReport.setActive(false);
                        currentReport.setUserName(update.message().from().username());
                        currentReport.setFullName("First name: " + update.message().from().firstName() + ", Last Name: " + update.message().from().lastName());
                        currentReport.setChatId(chatId);
                        currentReport.nextStep();

                        //формируем отчет и отправляем одним сообщением пользователю
                        sendReportUser();
                    }
                    break;
                default:
                    messageString = "smth went wrong. Try again. Push button 'Send report'";
            }
            if(!messageString.isEmpty()){
                sendMessage(chatId, messageString);
            }
        }
    }

    /**
     * Метод формирует отчет в одно сообщение и отправляет пользователю для проверки
     */
    private void sendReportUser() {

        if(!currentReport.isActive()) {
            //получаем фото для отчета
            String f_id = getPhoto();
            SendPhoto reportMessage = new SendPhoto(currentReport.getChatId(), f_id);

            //добавляем описание к отчету для пользователя
            reportMessage.caption(currentReport.doTextReport());
            SendResponse response = telegramBot.execute(reportMessage);

            //пользователь получил отчет
            if(response.isOk()){
                currentReport.setMessageId(response.message().messageId());
                choiceMessage(currentReport.getChatId(), "It's your report. Choose next step", new InlineKeyboardMarkup(new InlineKeyboardButton("Send").callbackData("st3_send_report"),
                        new InlineKeyboardButton("Cancel").callbackData("st3_cancel")));
            }
        }
        else
            sendMessage(currentReport.getChatId(), "Report didn't create. Try again");
    }

    /**
     * Метод формирует отчет в одно сообщение, отправляет волонтеру и сохраняет информацию о дате отправки
     */
    private void sendReportVolunteer() {

        if(!currentReport.isActive()) {
            //получаем фото для отчета
            String f_id = getPhoto();
            SendPhoto reportMessage = new SendPhoto(VOLUNTEER_CHAT_ID, f_id);

            //добавляем описание к отчету полное для волонтера
            reportMessage.caption(currentReport.doFullTextReport());
            SendResponse response = telegramBot.execute(reportMessage);

            //информируем пользователя, что ответ отправлен
            sendMessage(currentReport.getChatId(), "Report was send");
            //обнуляем поля
            currentReport.setNullFields();

            //todo Запись даты в базу как последняя отправка
            //curentReport.getChatId()
            //LocalDateTime localDateTime = LocalDateTime.now();
        }
        else
            sendMessage(currentReport.getChatId(), "Report didn't create. Try again");
    }

    /**
     * Метод возвращает данные фотографии строкой
     */
    private String getPhoto(){
        return currentReport.getPhotos().stream()
                .sorted(Comparator.comparing(PhotoSize::fileSize).reversed())
                .findFirst()
                .orElse(null).fileId();
    }
}