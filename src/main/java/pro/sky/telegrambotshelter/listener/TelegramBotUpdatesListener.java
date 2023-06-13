package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
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
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.service.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    final Keyboard STANDARD_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            new String[]{"Get info about a shelter", "How to get an animal form the shelter"},
            new String[]{"Send report", "Call a volunteer"})
            .resizeKeyboard(true)
            .selective(true);
    final Keyboard VOLUNTEER_KEYBOARD_MARKUP = new ReplyKeyboardMarkup(
            "Set user pet", "Check reports")
            .resizeKeyboard(true)
            .selective(true);

    private Map<Long, String> shelterChoice = new HashMap<>();

    private Long lastUserIdToAdopted;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    private final UserService userService;

    private final PetService petService;

    private final AdoptedCatsService adoptedCatsService;

    private final AdoptedDogsService adoptedDogsService;

    private final String VOLUNTEER_NAME = "VOLUNTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      ShelterService shelterService,
                                      UserService userService,
                                      PetService petService,
                                      AdoptedCatsService adoptedCatsService,
                                      AdoptedDogsService adoptedDogsService) {

        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.userService = userService;
        this.petService = petService;
        this.adoptedCatsService = adoptedCatsService;
        this.adoptedDogsService = adoptedDogsService;

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
                else if (update.callbackQuery().data().startsWith("uc")
                        || update.callbackQuery().data().startsWith("ud"))
                    setIdUserToAdopted(update);
                else if (update.callbackQuery().data().startsWith("c")
                        || update.callbackQuery().data().startsWith("d"))
                    setIdPetToAdopted(update);

            } else if (update.message().contact() != null) {
                contactReceiving(update);
            } else if (shelterService.getShelterByIdVolunteer(update.message().chat().id()) != null) {
                volunteerMessageParser(update, shelterService.getShelterByIdVolunteer(update.message().chat().id()));
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
                replyString = "Placeholder for 'Send report'";
                sendMessage(chatId, replyString);
                break;
            case "Call a volunteer":
                SendContact sendContact = new SendContact(chatId, VOLUNTEER_PHONE_NUMBER, VOLUNTEER_NAME).vcard("Волонтёр приюта Александр")
                        .allowSendingWithoutReply(true);
                telegramBot.execute(sendContact);

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
                break;
            case "st1_contact_receiving":
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Click the button to send contact info.")
                        .replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Send contact").requestContact(true))));
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
        telegramBot.execute(new EditMessageText(chatId, messageId, messageString).replyMarkup(update.callbackQuery().message().replyMarkup()));
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
     * Обработка входящего сообщения для волонтера
     */
    private void volunteerMessageParser(Update update, Shelter shelter) {

        logger.info("Processing update: {}", update);

        long chatId = update.message().chat().id();

        String messageText = update.message().text();
        String userName = update.message().chat().firstName();
        String shelterType = shelter.getShelterType();
        String replyString;


        switch (messageText) {
            case "/start":

                telegramBot.execute(new SendMessage(chatId, "Hello volunteer " + shelterType + " " + userName
                        + " you work at a shelter called: " + shelter.getGeneralInfo()));

                BotCommand[] commandsArr = new BotCommand[]{
                        new BotCommand("/start", "Restart the bot"),
                };

                SetMyCommands commands = new SetMyCommands(commandsArr);
                commands.scope(new BotCommandScopeDefault());
                BaseResponse response = telegramBot.execute(commands); // NEEDS CHECKING

                telegramBot.execute(new SendMessage(chatId, "Please, choose an option from the menu")
                        .replyMarkup(VOLUNTEER_KEYBOARD_MARKUP));

                break;

            case "Set user pet":

                replyString = "Choice user";

                InlineKeyboardMarkup inlineKeyboardMarkup = buttonsUsersChoiceByShelterType(shelterType);

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
     * */

    private InlineKeyboardMarkup buttonsUsersChoiceByShelterType(String shelterType) {

        List<Long> idUsers;
        List<User> usersWithNoPet = new ArrayList<>();

        switch (shelterType) {

            case "cats":

                idUsers = adoptedCatsService.getAllIdUser();

                if (idUsers == null) {
                    return new InlineKeyboardMarkup();
                }

                for (Long id : idUsers) {
                    if (adoptedCatsService.getByIdUser(id).getIdPet() == null) {
                        usersWithNoPet.add(userService.getUserById(id).get());
                    }
                }

                return buttonChoiceForVolunteerUsers(usersWithNoPet, "uc");

            case "dogs":

                idUsers = adoptedDogsService.getAllIdUser();

                if (idUsers == null) {
                    return new InlineKeyboardMarkup();
                }

                for (Long id : idUsers) {
                    if (adoptedDogsService.findByIdUser(id).getIdPet() == null) {
                        usersWithNoPet.add(userService.getUserById(id).get());
                    }
                }

                return buttonChoiceForVolunteerUsers(usersWithNoPet, "ud");

            default:

                return null;

        }

    }

    /**
     * генерация кнопок для выбора питомцев приюта которых отдает волонтер
     * */
    private InlineKeyboardMarkup buttonsPetsChoiceByShelterType(String shelterType, Long idShelter) {

        List<Pet> petsByIdShelter;
        List<Pet> petsWithNoUser = new ArrayList<>();

        switch (shelterType) {
            case "cats":

                petsByIdShelter = (List<Pet>) petService.getAllPetByShelters(idShelter);

                if (petsByIdShelter == null) {
                    return new InlineKeyboardMarkup();
                }

                for (Pet pet : petsByIdShelter) {
                    if (pet.getUser() == null) {
                        petsWithNoUser.add(pet);
                    }
                }

                return buttonChoiceForVolunteerPets(petsWithNoUser, "c");

            case "dogs":

                petsByIdShelter = (List<Pet>) petService.getAllPetByShelters(idShelter);

                if (petsByIdShelter == null) {
                    return new InlineKeyboardMarkup();
                }

                for (Pet pet : petsByIdShelter) {
                    if (pet.getUser() == null) {
                        petsWithNoUser.add(pet);
                    }
                }

                return buttonChoiceForVolunteerPets(petsWithNoUser, "d");

            default:

                return null;

        }

    }

    /**
     * установка связи Id пользователя в одну из таблиц adopted_cats или adopted_dogs
     * */
    private void setIdUserToAdopted(Update update) {

        String messageDataText = update.callbackQuery().data().substring(0, 2);

        Long idUser = Long.valueOf(update.callbackQuery().data().substring(2));
        Long chatId = update.callbackQuery().message().chat().id();
        Long idShelter = shelterService.getShelterByIdVolunteer(chatId).getId();

        String messageText = "Choice pet";

        InlineKeyboardMarkup inlineKeyboardMarkup;

        switch (messageDataText) {

            case "uc":

                lastUserIdToAdopted = idUser;

                AdoptedCats adoptedCats = adoptedCatsService.getByIdUser(idUser);
                adoptedCatsService.update(adoptedCats);

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

                lastUserIdToAdopted = idUser;

                AdoptedDogs adoptedDogs = adoptedDogsService.findByIdUser(idUser);
                adoptedDogsService.update(adoptedDogs);

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

                break;

        }

    }

    /**
     * установка связи Id питомца к Id пользователю в одну из таблиц adopted_cats или adopted_dogs,
     * с утановлением начала срока испытательно периода, его окончания и дня последнего отчета
     * */
    private void setIdPetToAdopted(Update update) {

        String messageDataText = update.callbackQuery().data().substring(0, 1);
        String messageText;

        Long idPet = Long.parseLong(update.callbackQuery().data().substring(1));
        Long chatId = update.callbackQuery().message().chat().id();

        Integer messageId = update.callbackQuery().message().messageId();

        LocalDateTime localDateTime = LocalDateTime.now();

        switch (messageDataText) {

            case "c":

                AdoptedCats adoptedCats = adoptedCatsService.getByIdUser(lastUserIdToAdopted);

                adoptedCats.setIdPet(idPet);
                adoptedCats.setPeriodStart(localDateTime);
                adoptedCats.setPeriodEnd(localDateTime.plusDays(30));
                adoptedCats.setLastReportDate(localDateTime);

                adoptedCatsService.update(adoptedCats);

                Pet petCat = petService.getPetById(idPet);
                petCat.setUser(userService.getUserById(adoptedCats.getIdUser()).get());
                petService.editPetByVolunteer(petCat);

                messageText = "You linked user " + userService.getUserById(adoptedCats.getIdUser()).get().getName()
                        + " to " + petService.getPetById(adoptedCats.getIdPet()).getName() + " pet";

                break;

            case "d":

                AdoptedDogs adoptedDogs = adoptedDogsService.findByIdUser(lastUserIdToAdopted);

                adoptedDogs.setIdPet(idPet);
                adoptedDogs.setPeriodStart(localDateTime);
                adoptedDogs.setPeriodEnd(localDateTime.plusDays(30));
                adoptedDogs.setPeriodEnd(localDateTime);

                adoptedDogsService.update(adoptedDogs);

                Pet petDog = petService.getPetById(idPet);
                petDog.setUser(userService.getUserById(adoptedDogs.getIdUser()).get());
                petService.editPetByVolunteer(petDog);

                messageText = "You linked user " + userService.getUserById(adoptedDogs.getIdUser()).get().getName()
                        + " to " + petService.getPetById(adoptedDogs.getIdPet()).getName() + " pet";

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
     * */
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
     * */
    private InlineKeyboardMarkup buttonChoiceForVolunteerPets(List<Pet> petsWithNoUser, String petsChoice) {

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[petsWithNoUser.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + petsWithNoUser.get(i).getName())
                    .callbackData(petsChoice + petsWithNoUser.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

}