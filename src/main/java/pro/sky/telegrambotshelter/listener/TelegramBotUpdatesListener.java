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
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.AdoptedCatsService;
import pro.sky.telegrambotshelter.service.PetService;
import pro.sky.telegrambotshelter.service.ShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            new String[]{"Set user pet", "Check reports"})
            .resizeKeyboard(true)
            .selective(true);

    Map<Long, String> shelterChoice = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final ShelterService shelterService;

    private final UserService userService;

    private final PetService petService;

    private final AdoptedCatsService adoptedCatsService;

    private final String VOLUNTEER_NAME = "VOLUNTEER_PLACEHOLDER";
    private final String VOLUNTEER_PHONE_NUMBER = "+00000000000";

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      ShelterService shelterService,
                                      UserService userService,
                                      PetService petService,
                                      AdoptedCatsService adoptedCatsService) {

        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.userService = userService;
        this.petService = petService;
        this.adoptedCatsService = adoptedCatsService;

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
                else if (update.callbackQuery().data().startsWith("uc"))
                    setIdUserToAdoptedCats(update);
                else if (update.callbackQuery().data().startsWith("c"))
                    setIdPetToAdoptedCats(update);

            } else if (update.message().contact() != null) {
                contactReceiving(update);
            } else if (shelterService.getVolunteerChatId("cats") == update.message().chat().id()
                    || shelterService.getVolunteerChatId("dogs") == update.message().chat().id()) {
                volunteerMessageParser(update);
            } else {
                messageParser(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void contactReceiving(Update update) {
        //Saving contact data to the DBs

        telegramBot.execute(new SendMessage(update.message().from().id(), "Thank you. Our volunteer will contact you!")
                .replyMarkup(STANDARD_KEYBOARD_MARKUP));

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
                if (shelterChoice.containsKey(chatId)) {

                    telegramBot.execute(new SendMessage(chatId, "Welcome back, " + userName)
                            .replyMarkup(new ReplyKeyboardRemove())
                            .disableNotification(true));

                } else {
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

    private void volunteerMessageParser(Update update) {

        logger.info("Processing update: {}", update);
        long chatId = update.message().chat().id();
        String messageText = update.message().text();
        String userName = update.message().chat().firstName();
        String shelterType;
        String replyString;

        if (shelterService.getVolunteerChatId("cats") == update.message().chat().id()) {
            shelterType = "cats";
        } else {
            shelterType = "dogs";
        }

        switch (messageText) {
            case "/start":

                telegramBot.execute(new SendMessage(chatId, "Hello volunteer " + shelterType + " " + userName)
                        .replyMarkup(new ReplyKeyboardRemove())
                        .disableNotification(true));


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

    private InlineKeyboardMarkup buttonsUsersChoiceByShelterType(String shelterType) {

        List<User> users = new ArrayList<>(userService.getUserByShelterType(shelterType));
        List<User> usersNotPet = new ArrayList<>();


        for (User user : users) {
            if (adoptedCatsService.findByIdUser(user.getId()) == null) {
                usersNotPet.add(user);
            }
        }

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[usersNotPet.size()][1];


        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + usersNotPet.get(i).getName())
                    .callbackData("uc" + usersNotPet.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    private InlineKeyboardMarkup buttonsPetsChoiceByShelterType(String shelterType) {

        long shelter = (shelterType.equals("cats")) ? 1 : 0;

        List<Pet> pets = new ArrayList<>(petService.getAllPetByShelters(shelter));
        List<Pet> petsNotUser = new ArrayList<>();

        for (Pet pet : pets) {
            if (adoptedCatsService.findByIdPet(pet.getId()) == null) {
                petsNotUser.add(pet);
            }
        }

        InlineKeyboardButton[][] inlineKeyboardButtons = new InlineKeyboardButton[petsNotUser.size()][1];

        for (int i = 0; i < inlineKeyboardButtons.length; i++) {

            inlineKeyboardButtons[i][0] = new InlineKeyboardButton((i + 1) + ". " + petsNotUser.get(i).getName())
                    .callbackData("c" + petsNotUser.get(i).getId());

        }

        return new InlineKeyboardMarkup(inlineKeyboardButtons);

    }

    private void setIdUserToAdoptedCats(Update update) {

        Long chatId = update.callbackQuery().from().id();
        Long idUser = Long.parseLong(update.callbackQuery().data().substring(2));

        adoptedCatsService.save(new AdoptedCats(null,
                userService.getUserById(idUser).get().getId(),
                null,
                null,
                null));


        String messageText = "Choice pet";

        InlineKeyboardMarkup inlineKeyboardMarkup = buttonsPetsChoiceByShelterType("cats");

        if (inlineKeyboardMarkup.inlineKeyboard().length == 0) {

            adoptedCatsService.deleteAdoptedCats(adoptedCatsService.findLastAdoptedCats());

            telegramBot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
            telegramBot.execute(new SendMessage(chatId, "All cats are in good hands!"));

        } else {

            telegramBot.execute(new EditMessageText(chatId
                    , update.callbackQuery().message().messageId(), messageText)
                    .replyMarkup(buttonsPetsChoiceByShelterType("cats")));

        }

    }

    private void setIdPetToAdoptedCats(Update update) {

        Long idCat = Long.parseLong(update.callbackQuery().data().substring(1));

        LocalDateTime localDateTime = LocalDateTime.now();

        AdoptedCats adoptedCats = adoptedCatsService.findLastAdoptedCats();

        if (adoptedCats != null) {

            adoptedCats.setIdPet(idCat);
            adoptedCats.setPeriodStart(localDateTime);
            adoptedCats.setPeriodEnd(localDateTime.plusDays(30));
            adoptedCats.setLastReportDate(localDateTime);

        }

        adoptedCatsService.save(adoptedCats);

        Pet pet = petService.getPetById(idCat);
        pet.setUser(userService.getUserById(adoptedCats.getIdUser()).get());
        petService.editPetByVolunteer(pet);

        Long chatId = update.callbackQuery().from().id();
        Integer messageId = update.callbackQuery().message().messageId();

        String userName = userService.getUserById(adoptedCats.getIdUser()).get().getName();
        String petName = petService.getPetById(adoptedCats.getIdPet()).getName();

        String messageText = "You linked user " + userName + " to " + petName + " pet";

        telegramBot.execute(new DeleteMessage(chatId, messageId));
        telegramBot.execute(new SendMessage(chatId, messageText));

    }

}