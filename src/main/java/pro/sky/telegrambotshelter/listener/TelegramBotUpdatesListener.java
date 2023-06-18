package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.enums.ButtonCommands;
import pro.sky.telegrambotshelter.enums.Phrases;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.scheduler.ReportsScheduler;
import pro.sky.telegrambotshelter.service.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    private final Keyboard STANDARD_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            new String[]{"Get info about a shelter", "How to get an animal form the shelter"},
            new String[]{"Send report", "Call a volunteer"})
            .resizeKeyboard(true)
            .selective(true);

    private final Keyboard VOLUNTEER_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            new String[]{"Set user pet", "Add user to shelter"},
            new String[]{"Add to user additional time to the user trial period"},
            new String[]{"Set user that he failed probation", "Check reports"})
            .resizeKeyboard(true)
            .selective(true);

    private Map<Long, String> shelterChoice;

    private Map<Long, Shelter> shelters;

    private Map<Long, AdoptedCats> adoptedCatsMap;

    private Map<Long, AdoptedDogs> adoptedDogsMap;

    private Map<Long, User> usersIdUserMap;

    private Map<Long, Pet> idPetMap;

    private Long lastUserIdToAdopted;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    private final UserService userService;

    private final PetService petService;

    private final AdoptedCatsService adoptedCatsService;

    private final AdoptedDogsService adoptedDogsService;

    private final ReportsScheduler reportsScheduler;

    private final String VOLUNTEER_NAME = "VOLONTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      ShelterService shelterService,
                                      UserService userService,
                                      PetService petService,
                                      AdoptedCatsService adoptedCatsService,
                                      AdoptedDogsService adoptedDogsService,
                                      ReportsScheduler reportsScheduler) {

        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.userService = userService;
        this.petService = petService;
        this.adoptedCatsService = adoptedCatsService;
        this.adoptedDogsService = adoptedDogsService;
        this.reportsScheduler = reportsScheduler;


    }

    /**
     * Инициализация бота
     */
    @PostConstruct
    public void init() {

        usersIdUserMap = userService.getAllByIdNameMap();
        shelters = shelterService.getAllSheltersToMap();
        idPetMap = petService.getAllPetsMapIdPet();
        adoptedCatsMap = adoptedCatsService.getAllAdoptedCatsToMap();
        adoptedDogsMap = adoptedDogsService.getAllAdoptedDogsToMap();

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
            logger.info(Phrases.PROCESSING_UPDATE.toString(), update.toString());

            if (update.callbackQuery() != null) {
                if (update.callbackQuery().data().startsWith("st0"))
                    shelterChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st1"))
                    stage1ChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st2"))
                    stage2ChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("st3"))
                    stage3ChoiceUpdateParser(update);
                else if (update.callbackQuery().data().startsWith("uc")
                        || update.callbackQuery().data().startsWith("ud"))
                    setIdUserToAdopted(update);
                else if (update.callbackQuery().data().startsWith("cat")
                        || update.callbackQuery().data().startsWith("dog"))
                    setIdPetToAdopted(update);
                else if (update.callbackQuery().data().startsWith("1udc")
                        || update.callbackQuery().data().startsWith("1udd"))
                    failedUser(update);
                else if (update.callbackQuery().data().startsWith("add"))
                    addUserToShelter(update);
                else if(update.callbackQuery().data().startsWith("time"))
                    addAdditionalTimeToUsers(update);
                else if (update.callbackQuery().data().startsWith("days"))
                    addAdditionalTimeToAdoptedPet(update);

            } else if (update.message() != null) {
                if (update.message().contact() != null){
                    contactReceiving(update);

                } else if (shelters.containsKey(update.message().chat().id())) {
                    volunteerMessageParser(update);
                }

                else
                    messageParser(update);
            } else if (update.myChatMember() != null) {
                if (update.myChatMember().newChatMember().status() == ChatMember.Status.kicked) {
                    userService.deleteUsersByChatId(update.message().chat().id());
                }

            }

            reports();

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

    private void stage3ChoiceUpdateParser(Update update) {
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

                    User user = new User(userName, chatId);

                    userService.save(user);
                    usersIdUserMap.put(user.getId(), user);
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
                choiceMessage(update.message().chat().id(), "REPORT", inlineKeyboardMarkup);
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
                replyString = "Sorry, something went wrong try again.";
                sendMessage(chatId, replyString);
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
     * Обработка входящего сообщения для волонтера
     */
    private void volunteerMessageParser(Update update) {

        logger.info("Processing update: {}", update);

        Long chatId = update.message().chat().id();

        Shelter shelter = shelters.get(chatId);

        String messageText = update.message().text();
        String userName = update.message().chat().firstName();
        String shelterType = shelter.getShelterType();
        String replyString;

        InlineKeyboardMarkup inlineKeyboardMarkup;

        List<User> tempUserIdMap = new ArrayList<>();

        switch (messageText) {
            case "/start":

                telegramBot.execute(new SendMessage(chatId, "Hello volunteer " + shelterType + " " + userName
                        + " you work at a shelter called: " + shelter.getGeneralInfo()));

                telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                        .replyMarkup(VOLUNTEER_KEYBOARD_MARKUP));

                break;

            case "Set user pet":

                replyString = "Choice user";

                inlineKeyboardMarkup = buttonsUsersChoiceByShelterType(shelterType, shelter);

                if (inlineKeyboardMarkup.inlineKeyboard().length == 0) {
                    telegramBot.execute(new SendMessage(chatId, "Dont have any users :("));
                } else {
                    telegramBot.execute(new SendMessage(chatId, replyString)
                            .replyMarkup(inlineKeyboardMarkup));
                }

                break;


            case "Add user to shelter":

                replyString = "Choice user to add to the shelter";

                usersIdUserMap.forEach((k, v) -> {

                    if (v.getShelterTypeChoice().equals(shelter.getShelterType())
                            && !v.isFailed() && v.getShelter() == null) {
                        tempUserIdMap.add(v);
                    }

                });

                if (tempUserIdMap.isEmpty()) {
                    telegramBot.execute(new SendMessage(chatId, "Sorry no new users :("));
                } else {

                    inlineKeyboardMarkup = buttonChoiceForVolunteerToAddUser(tempUserIdMap);
                    telegramBot.execute(new SendMessage(chatId, replyString)
                            .replyMarkup(inlineKeyboardMarkup));

                }

                break;

            case "Add to user additional time to the user trial period":

                replyString = "Choice user to add additional time";

                usersIdUserMap.forEach((k, v) -> {

                    if (v.getShelter() != null) {
                        if (v.getShelter().equals(shelter) && (adoptedCatsMap.get(v.getId()) != null ||
                                adoptedDogsMap.get(v.getId()) != null)) {
                            tempUserIdMap.add(v);
                        }
                    }

                });

                if (tempUserIdMap.isEmpty()) {
                    telegramBot.execute(new SendMessage(chatId, "Sorry no users who take a " +
                            shelterType + " :("));
                } else {

                    inlineKeyboardMarkup = buttonChoiceForVolunteerToAdditionalTimeUsers(tempUserIdMap,
                            shelterType.substring(0, 1));
                    telegramBot.execute(new SendMessage(chatId, replyString)
                            .replyMarkup(inlineKeyboardMarkup));

                }



                break;

            case "Set user that he failed probation":

                replyString = "Choice user who failed";

                List<User> usersShelter = shelter.getUsers();

                inlineKeyboardMarkup = buttonChoiceForVolunteerWhoFailed(usersShelter,
                        shelterType, "1ud" + shelterType.charAt(0));

                if (inlineKeyboardMarkup.inlineKeyboard().length == 0) {
                    telegramBot.execute(new SendMessage(chatId, "Dont have any users :("));
                } else {
                    telegramBot.execute(new SendMessage(chatId, replyString)
                            .replyMarkup(inlineKeyboardMarkup));
                }

                break;

            case "Check reports":
                replyString = "Placeholder for 'Check reports'";
                sendMessage(chatId, replyString);
                break;

            default:
                replyString = "Sorry, something went wrong try again.";
                sendMessage(chatId, replyString);

        }

    }

    /**
     * генерация кнопок для выбора пользователей которые хотят взять питомцев
     */

    private InlineKeyboardMarkup buttonsUsersChoiceByShelterType(String shelterType, Shelter shelter) {

        List<User> usersWithNoPet = new ArrayList<>();
        Set<User> tmpUser = new HashSet<>();
        List<User> shelterUsers = shelter.getUsers();


        switch (shelterType) {

            case "cats":

                shelterUsers.stream().filter(user -> !user.isFailed() && !adoptedCatsMap.containsKey(user.getId()))
                        .forEach(tmpUser::add);

                idPetMap.forEach((k, v) -> tmpUser.remove(v.getUser()));

                usersWithNoPet.addAll(tmpUser);

                return buttonChoiceForVolunteerUsers(usersWithNoPet, "uc");

            case "dogs":

                shelterUsers.stream().filter(user -> !user.isFailed() && !adoptedDogsMap.containsKey(user.getId()))
                        .forEach(tmpUser::add);

                idPetMap.forEach((k, v) -> tmpUser.remove(v.getUser()));

                usersWithNoPet.addAll(tmpUser);

                return buttonChoiceForVolunteerUsers(usersWithNoPet, "ud");

            default:

                return null;

        }

    }

    /**
     * генерация кнопок для выбора питомцев приюта которых отдает волонтер
     */
    private InlineKeyboardMarkup buttonsPetsChoiceByShelterType(String shelterType, Long idShelter) {

        List<Pet> petsWithNoUser = new ArrayList<>();

        switch (shelterType) {
            case "cats":

                List<Pet> catsPetsByIdShelter = new ArrayList<>();
                idPetMap.forEach((k, v) -> {
                    if (v.getShelter().getId().equals(idShelter)) {
                        catsPetsByIdShelter.add(v);
                    }
                });

                if (catsPetsByIdShelter.isEmpty()) {
                    return new InlineKeyboardMarkup();
                }

                for (Pet pet : catsPetsByIdShelter) {
                    if (pet.getUser() == null) {
                        petsWithNoUser.add(pet);
                    }
                }

                return buttonChoiceForVolunteerPets(petsWithNoUser, "cat");

            case "dogs":

                List<Pet> dogsPetsByIdShelter = new ArrayList<>();
                idPetMap.forEach((k, v) -> {
                    if (v.getShelter().getId().equals(idShelter)) {
                        dogsPetsByIdShelter.add(v);
                    }
                });

                if (dogsPetsByIdShelter.isEmpty()) {
                    return new InlineKeyboardMarkup();
                }

                for (Pet pet : dogsPetsByIdShelter) {
                    if (pet.getUser() == null) {
                        petsWithNoUser.add(pet);
                    }
                }

                return buttonChoiceForVolunteerPets(petsWithNoUser, "dog");

            default:

                return null;

        }

    }

    /**
     * установка связи Id пользователя в одну из таблиц adopted_cats или adopted_dogs
     */
    private void setIdUserToAdopted(Update update) {

        String messageDataText = update.callbackQuery().data().substring(0, 2);

        Long chatId = update.callbackQuery().message().chat().id();
        Long idShelter = shelters.get(chatId).getId();
        lastUserIdToAdopted = Long.valueOf(update.callbackQuery().data().substring(2));

        String messageText = "Choice pet";

        InlineKeyboardMarkup inlineKeyboardMarkup;

        switch (messageDataText) {

            case "uc":

                inlineKeyboardMarkup = buttonsPetsChoiceByShelterType("cats", idShelter);

                if (inlineKeyboardMarkup.inlineKeyboard().length == 0) {

                    telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
                    telegramBot.execute(new SendMessage(chatId, "All cats are in good hands!"));

                } else {

                    telegramBot.execute(new EditMessageText(chatId
                            , update.callbackQuery().message().messageId(), messageText)
                            .replyMarkup(inlineKeyboardMarkup));

                }

                break;

            case "ud":

                inlineKeyboardMarkup = buttonsPetsChoiceByShelterType("dogs", idShelter);

                if (inlineKeyboardMarkup.inlineKeyboard().length == 0) {

                    telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
                    telegramBot.execute(new SendMessage(chatId, "All dogs are in good hands!"));

                } else {

                    telegramBot.execute(new EditMessageText(chatId
                            , update.callbackQuery().message().messageId(), messageText)
                            .replyMarkup(inlineKeyboardMarkup));

                }

                break;

            default:

                telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
                telegramBot.execute(new SendMessage(chatId, "Something wrong happening!"));

                break;

        }

    }

    /**
     * установка связи Id питомца к Id пользователю в одну из таблиц adopted_cats или adopted_dogs,
     * с утановлением начала срока испытательно периода, его окончания и дня последнего отчета
     */
    private void setIdPetToAdopted(Update update) {

        String messageDataText = update.callbackQuery().data().substring(0, 3);
        String messageText;

        Long idPet = Long.parseLong(update.callbackQuery().data().substring(3));
        Long chatId = update.callbackQuery().message().chat().id();

        Integer messageId = update.callbackQuery().message().messageId();

        LocalDateTime localDateTime = LocalDateTime.now();

        switch (messageDataText) {

            case "cat":

                AdoptedCats adoptedCats = new AdoptedCats();

                adoptedCats.setIdPet(idPet);
                adoptedCats.setIdUser(lastUserIdToAdopted);
                adoptedCats.setPeriodStart(localDateTime);
                adoptedCats.setPeriodEnd(localDateTime.plusDays(30));
                adoptedCats.setLastReportDate(localDateTime);

                adoptedCatsService.update(adoptedCats);
                adoptedCatsMap.put(lastUserIdToAdopted, adoptedCats);

                Pet petCat = idPetMap.get(idPet);
                petCat.setUser(usersIdUserMap.get(lastUserIdToAdopted));
                petService.editPetByVolunteer(petCat);
                idPetMap.put(petCat.getId(), petCat);

                messageText = "You linked user " + usersIdUserMap.get(adoptedCats.getIdUser()).getName()
                        + " to " + petCat.getName() + " pet";

                break;

            case "dog":

                AdoptedDogs adoptedDogs = new AdoptedDogs();

                adoptedDogs.setIdPet(idPet);
                adoptedDogs.setIdUser(lastUserIdToAdopted);
                adoptedDogs.setPeriodStart(localDateTime);
                adoptedDogs.setPeriodEnd(localDateTime.plusDays(30));
                adoptedDogs.setLastReportDate(localDateTime);

                adoptedDogsService.update(adoptedDogs);
                adoptedDogsMap.put(lastUserIdToAdopted, adoptedDogs);

                Pet petDog = idPetMap.get(idPet);
                petDog.setUser(usersIdUserMap.get(lastUserIdToAdopted));
                petService.editPetByVolunteer(petDog);
                idPetMap.put(petDog.getShelter().getId(), petDog);

                messageText = "You linked user " + usersIdUserMap.get(adoptedDogs.getIdUser()).getName()
                        + " to " + petDog.getName() + " pet";

                break;

            default:

                messageText = "Something wrong happening!";

                break;

        }

        telegramBot.execute(new DeleteMessage(chatId, messageId));
        telegramBot.execute(new SendMessage(chatId, messageText));

    }


    /**
     * генерация конопок пользователей в зависимости кого он выбрал в качестве питомца
     */
    private InlineKeyboardMarkup buttonChoiceForVolunteerUsers(List<User> usersWithNoPet, String usersChoice) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[usersWithNoPet.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + usersWithNoPet.get(i).getName())
                    .callbackData(usersChoice + usersWithNoPet.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    /**
     * генерация конопок питомцев в зависимости от типа питомца ("cats" или "dogs")
     */
    private InlineKeyboardMarkup buttonChoiceForVolunteerPets(List<Pet> petsWithNoUser, String petsChoice) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[petsWithNoUser.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + petsWithNoUser.get(i).getName())
                    .callbackData(petsChoice + petsWithNoUser.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    /**
     * генерация конопок пользователей для выбора того кто провалил испытательный период
     */

    private InlineKeyboardMarkup buttonChoiceForVolunteerWhoFailed(List<User> users, String shelter, String data) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[users.size()][1];

        switch (shelter) {

            case "cats":

                for (int i = 0; i < inlineKeyboardButtons.length; i++) {

                    if(adoptedCatsMap.get(users.get(i).getId()) != null)

                        inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) +
                                ". " + users.get(i).getName() + " have cat " +
                                idPetMap.get(adoptedCatsMap.get(users.get(i).getId()).getIdPet()).getName())
                                .callbackData(data + users.get(i).getId());

                    else inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) +
                            ". " + users.get(i).getName() + " dont have a cat")
                            .callbackData(data + users.get(i).getId());
                }

                break;
            case "dogs":

                for (int i = 0; i < inlineKeyboardButtons.length; i++) {

                    if(adoptedDogsMap.get(users.get(i).getId()) != null)

                        inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) +
                                ". " + users.get(i).getName() + " have dog " +
                                idPetMap.get(adoptedDogsMap.get(users.get(i).getId()).getIdPet()).getName())
                                .callbackData(data + users.get(i).getId());

                    else inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) +
                            ". " + users.get(i).getName() + "dont have a dog")
                            .callbackData(data + users.get(i).getId());
                }

                break;
        }


        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    /**
     * генерация конопок пользователей для выбора волонтером в качестве потенциального усыновителя
     */
    private InlineKeyboardMarkup buttonChoiceForVolunteerToAddUser(List<User> users) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[users.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + users.get(i).getName())
                    .callbackData("add" + users.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    /**
     * генерация конопок пользователей для выбора волонтером, для добавления дополнительного испытательного периода
     */
    private InlineKeyboardMarkup buttonChoiceForVolunteerToAdditionalTimeUsers(List<User> users, String data) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[users.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + users.get(i).getName())
                    .callbackData("time" + data + users.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    /**
     * удаление записи AdoptedCat или AdoptedDog, для пользователей кто не прошел испытательный период,
     * а также установка записи у пользователя что он провалил испытательный срок
     */
    private void failedUser(Update update) {

        String messageDataText = update.callbackQuery().data().substring(3, 4);

        Long idUser = Long.valueOf(update.callbackQuery().data().substring(4));
        Long chatId = update.callbackQuery().message().chat().id();

        Integer messageId = update.callbackQuery().message().messageId();

        User user = usersIdUserMap.get(idUser);

        user.setFailed(true);
        usersIdUserMap.put(idUser, user);
        userService.save(user);

        List<User> usersList = shelters.get(chatId).getUsers();
        usersList.remove(user);
        shelters.get(chatId).setUsers(usersList);
        shelterService.deleteUserFromShelter(user);

        switch (messageDataText) {
            case "c":

                AdoptedCats adoptedCats = adoptedCatsMap.get(idUser);

                if (adoptedCats != null) {

                    Pet pet = idPetMap.get(adoptedCats.getIdPet());
                    pet.setUser(null);
                    idPetMap.put(adoptedCats.getIdPet(), pet);
                    petService.editPetByVolunteer(pet);

                    adoptedCatsService.delete(adoptedCats);
                    adoptedCatsMap.remove(idUser);

                }

                break;

            case "d":

                AdoptedDogs adoptedDogs = adoptedDogsMap.get(idUser);

                if (adoptedDogs != null) {

                    Pet pet = idPetMap.get(adoptedDogs.getIdPet());
                    pet.setUser(null);
                    idPetMap.put(adoptedDogs.getIdPet(), pet);
                    petService.editPetByVolunteer(pet);

                    adoptedDogsService.delete(adoptedDogs);
                    adoptedDogsMap.remove(idUser);

                }

                break;

            default:

        }

        telegramBot.execute(new DeleteMessage(chatId, messageId));
        telegramBot.execute(new SendMessage(chatId, "DELETE"));

        telegramBot.execute(new SendMessage(user.getChatId(), "YOU ARE FAILED"));

    }

    /**
     * кнопки для выбора количества дней, для дальнейшего добавления испытательного периода
     * */

    private void addAdditionalTimeToUsers (Update update) {

        Long chatId = update.callbackQuery().message().chat().id();
        lastUserIdToAdopted = Long.valueOf(update.callbackQuery().data().substring(5));

        String messageText = "Choice how much to add";

        telegramBot.execute(new EditMessageText(chatId
                , update.callbackQuery().message().messageId(), messageText)
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton("1. 14 days")
                                .callbackData("days_14")},
                        new InlineKeyboardButton[]{
                                new InlineKeyboardButton("2. 30 days")
                                        .callbackData("days_30")})));


    }

    /**
     * добавление времени к испытательному периоду
     */

    private void addAdditionalTimeToAdoptedPet(Update update) {

        String messageDataText = update.callbackQuery().data();
        String messageText = "You are add";

        Long chatId = update.callbackQuery().message().chat().id();

        Integer messageId = update.callbackQuery().message().messageId();



        switch (messageDataText) {

            case "days_14":

                messageText += " 14 days";

                if (usersIdUserMap.get(lastUserIdToAdopted).getShelter().getShelterType().equals("cats")) {

                    AdoptedCats adoptedCats = adoptedCatsMap.get(lastUserIdToAdopted);

                    adoptedCats.setPeriodEnd(LocalDateTime.now().minusDays(14));
                    adoptedCatsMap.put(lastUserIdToAdopted, adoptedCats);
                    adoptedCatsService.update(adoptedCats);

                } else {

                    AdoptedDogs adoptedDogs = adoptedDogsMap.get(lastUserIdToAdopted);

                    adoptedDogs.setPeriodEnd(LocalDateTime.now().minusDays(14));
                    adoptedDogsMap.put(lastUserIdToAdopted, adoptedDogs);
                    adoptedDogsService.update(adoptedDogs);

                }

                break;

            case "days_30":

                messageText += " 30 days";

                if (usersIdUserMap.get(lastUserIdToAdopted).getShelter().getShelterType().equals("cats")) {

                    AdoptedCats adoptedCats = adoptedCatsMap.get(lastUserIdToAdopted);

                    adoptedCats.setPeriodEnd(LocalDateTime.now().minusDays(30));
                    adoptedCatsMap.put(lastUserIdToAdopted, adoptedCats);
                    adoptedCatsService.update(adoptedCats);

                } else {

                    AdoptedDogs adoptedDogs = adoptedDogsMap.get(lastUserIdToAdopted);

                    adoptedDogs.setPeriodEnd(LocalDateTime.now().minusDays(30));
                    adoptedDogsMap.put(lastUserIdToAdopted, adoptedDogs);
                    adoptedDogsService.update(adoptedDogs);

                }

                break;


        }

        telegramBot.execute(new DeleteMessage(chatId, messageId));
        telegramBot.execute(new SendMessage(chatId, messageText));

        telegramBot.execute(new SendMessage(usersIdUserMap.get(lastUserIdToAdopted).getChatId(),
                "You are additionally assigned a trial period by a volunteer"));

    }

    /**
     * добавление пользователя в приют в качестве потенциального усыновителя
     */
    private void addUserToShelter(Update update) {

        Long idUser = Long.valueOf(update.callbackQuery().data().substring(3));
        Long chatId = update.callbackQuery().message().chat().id();

        Integer messageId = update.callbackQuery().message().messageId();

        User user = usersIdUserMap.get(idUser);

        user.setShelter(shelters.get(chatId));
        usersIdUserMap.put(idUser, user);

        List<User> usersList = shelters.get(chatId).getUsers();
        usersList.add(user);
        shelters.get(chatId).setUsers(usersList);

        shelterService.addUserToShelter(user);
        userService.save(user);

        telegramBot.execute(new DeleteMessage(chatId, messageId));
        telegramBot.execute(new SendMessage(chatId, "User " + user.getName() + " is add to shelter"));

        telegramBot.execute(new SendMessage(user.getChatId(), "You have been added to the shelter as" +
                " an adopter of pets by volunteer. Congratulation!!!"));

    }

    /**
     * Scheduler для отправки сообщений пользователям и волонтерам
     */
    @Scheduled(cron = "${interval-scheduled-report}")
    private void reports() {

        logger.info("Start at {}", LocalDateTime.now());

        List<SendMessage> sendMessages = reportsScheduler.lastReportDateToUserScheduler();

        if (sendMessages != null) {

            logger.info("Start send message to users");

            for (SendMessage sendMessage : sendMessages) {
                telegramBot.execute(sendMessage);
            }

        }

        sendMessages = reportsScheduler.lastReportDateToVolunteerScheduler();

        if (sendMessages != null) {

            logger.info("Start send message to volunteers");

            for (SendMessage sendMessage : sendMessages) {
                telegramBot.execute(sendMessage);
            }

        }

        sendMessages = reportsScheduler.endOfTrialPeriod();

        if (sendMessages != null) {

            logger.info("Start send message to users, who end trial period");

            for (SendMessage sendMessage : sendMessages) {
                telegramBot.execute(sendMessage);
            }

        }

    }

}