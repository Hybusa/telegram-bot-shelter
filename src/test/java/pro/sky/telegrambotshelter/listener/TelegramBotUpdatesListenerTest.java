package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.scheduler.ReportsScheduler;
import pro.sky.telegrambotshelter.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {TelegramBotUpdatesListener.class})
@ExtendWith(SpringExtension.class)
class TelegramBotUpdatesListenerTest {

    @MockBean
    private ShelterService shelterService;

    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @MockBean
    private UserService userService;

    @MockBean
    private PetService petService;

    @MockBean
    private ContactsForCatsShelterService contactsForCatsShelterService;
    @MockBean
    private ContactsForDogsShelterService contactsForDogsShelterService;

    @MockBean
    private AdoptedCatsService adoptedCatsService;

    @MockBean
    private AdoptedDogsService adoptedDogsService;

    @MockBean
    private ReportsScheduler reportsScheduler;

    @Value("${telegram.bot.info}")
    String botInfo;


    Map<Long, String> userMap = new HashMap<>();
    Map<Long, pro.sky.telegrambotshelter.model.User> userIdMap = new HashMap<>();
    List<Update> updates;
    String name = "Jane";
    Long chatId = 123L;

    pro.sky.telegrambotshelter.model.User user = new pro.sky.telegrambotshelter.model.User(name, chatId);

    Long userId = 0L;

    int messageId = 223;

    @BeforeEach
    void init() {
        userMap.put(chatId, "TestString");
        user.setId(userId);
        userIdMap.put(userId,user);
        when(userService.getMapUsersChatIdWithChoice()).thenReturn(userMap);
        when(userService.getAllByIdNameMap()).thenReturn(userIdMap);

        telegramBotUpdatesListener.init();

        updates = new ArrayList<>();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStartNewUser() {
        // Arrange


        String messageText = "/start";
        String returnMessageText = "Hello, " + name + "! \n" + botInfo;

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(chatId);
        when(chat.firstName()).thenReturn(name);
        when(userService.getMapUsersChatIdWithChoice()).thenReturn(new HashMap<>());

        Message returnMessage = mock(Message.class);
        when(returnMessage.text()).thenReturn(returnMessageText);
        when(returnMessage.chat()).thenReturn(chat);

        Message message = mock(Message.class);
        when(message.text()).thenReturn(messageText);
        when(message.chat()).thenReturn(chat);

        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);
        // Act

        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot, atLeast(2)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse, atLeast(2)).isOk();
        verify(message, atLeastOnce()).chat();
        verify(message, atLeastOnce()).text();
        verify(chat, atLeastOnce()).id();
        verify(chat, atLeastOnce()).firstName();
        verify(userService, atMost(1)).save(Mockito.any(pro.sky.telegrambotshelter.model.User.class));
    }


    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStartRecurringUser() {
        // Arrange
        String messageText = "/start";

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(chatId);
        when(chat.firstName()).thenReturn(name);

        Message returnMessage = mock(Message.class);
        when(returnMessage.chat()).thenReturn(chat);

        Message message = mock(Message.class);
        when(message.text()).thenReturn(messageText);
        when(message.chat()).thenReturn(chat);

        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);
        when(sendResponse.message()).thenReturn(returnMessage);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot, atMost(2)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse, atLeast(2)).isOk();
        verify(message, atLeastOnce()).chat();
        verify(message, atLeastOnce()).text();
        verify(chat, atLeastOnce()).id();
        verify(chat, atLeastOnce()).firstName();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage0DogsCommand() {
        // Arrange
        List<Update> updates = new ArrayList<>();
        Long chatId = 123L;


        String messageText = "st0_dog_shelters";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);
        when(user.firstName()).thenReturn(name);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot, times(2)).execute(Mockito.any(SendMessage.class));
        verify(telegramBot).execute(Mockito.any(DeleteMessage.class));
        verify(sendResponse, atLeastOnce()).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(user, atLeastOnce()).firstName();
        verify(userService).updateShelterChoiceByChatId(any(pro.sky.telegrambotshelter.model.User.class), anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage0CatsCommand() {
        // Arrange
        List<Update> updates = new ArrayList<>();
        Long chatId = 123L;
        int messageId = 223;

        String messageText = "st0_cat_shelters";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);
        when(user.firstName()).thenReturn(name);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot,times(2)).execute(Mockito.any(SendMessage.class));
        verify(telegramBot).execute(Mockito.any(DeleteMessage.class));
        verify(sendResponse, atLeastOnce()).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(user, atLeastOnce()).firstName();
        verify(userService).updateShelterChoiceByChatId(any(pro.sky.telegrambotshelter.model.User.class), anyString());
    }


    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1InfoCommand() {
        // Arrange
        String messageText = "st1_shelter_info";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);
        when(update.callbackQuery().data()).thenReturn(messageText);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getGeneralInfo(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1ScheduleCommand() {
        // Arrange
        String messageText = "st1_shelter_schedule";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getSchedule(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1AddressCommand() {
        // Arrange
        String messageText = "st1_shelter_address";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getAddress(anyString());
    }


    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1PathCommand() {
        // Arrange
        String messageText = "st1_shelter_path";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getHowToGet(anyString());
    }


    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1PathSecurityPass() {
        // Arrange
        String messageText = "st1_shelter_security_pass";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getSecurityAndPass(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1SafetyRequirements() {
        // Arrange
        String messageText = "st1_shelter_safety_requirements";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();

        verify(shelterService).getSafety(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1CallAVolunteer() {
        // Arrange
        String messageText = "st1_call_a_volunteer";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendContact.class))).thenReturn(sendResponse);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(SendContact.class));
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(sendResponse).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage1ContactReceiving() {
        // Arrange
        String messageText = "st1_contact_receiving";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(SendMessage.class));
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(sendResponse).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2MeetingRecommendations() {
        // Arrange
        String messageText = "st2_meeting_recommendations";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getMeetingRecommendation(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2DocumentList() {
        // Arrange
        String messageText = "st2_document_list";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getDocumentsList(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2HomeRecommendationsYoung() {
        // Arrange
        String messageText = "st2_home_recommendations_young";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getHomeRecommendationsYoung(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2HomeRecommendationsOld() {
        // Arrange
        String messageText = "st2_home_recommendations_old";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getHomeRecommendationsOld(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2DisabilityRecommendations() {
        // Arrange
        List<Update> updates = new ArrayList<>();
        Long chatId = 123L;
        int messageId = 223;

        String messageText = "st2_home_recommendations_disability";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getDisabilityRecommendations(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2CynologistRecommendations() {
        // Arrange
        String messageText = "st2_cynologist_recommendations";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
         verify(shelterService).getCynologistRecommendations(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2ListOfCynologists() {
        // Arrange
        String messageText = "st2_list_of_cynologists";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getListOfCynologists(anyString());
    }
    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2WhyWeCanDeny() {
        // Arrange
        String messageText = "st2_why_we_can_deny";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
        verify(shelterService).getWhyWeCanDeny(anyString());
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2CallAVolunteer() {
        // Arrange
        String messageText = "st2_call_a_volunteer";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendContact.class))).thenReturn(sendResponse);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(SendContact.class));
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(sendResponse).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStage2ContactReceiving() {
        // Arrange
        String messageText = "st2_contact_receiving";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(SendMessage.class));
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(sendResponse).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testContactChoiceUpdateParser_cats() {
        // Arrange
        String messageText = "vol_contact/+7916";
        String choice = "cats";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        when(shelterService.getShelterTypeByVolunteerId(anyLong())).thenReturn(choice);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }

    @Test
    void testContactChoiceUpdateParser_dogs() {
        // Arrange
        String messageText = "vol_contact/+7916";
        String choice = "dogs";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        when(shelterService.getShelterTypeByVolunteerId(anyLong())).thenReturn(choice);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot).execute(Mockito.any(EditMessageText.class));
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
        verify(message, atLeastOnce()).messageId();
        verify(user, atLeastOnce()).id();
    }



    /**
     * Methods under test: {@link TelegramBotUpdatesListener#process(List)
     *                     @link TelegramBotUpdatesListener#stage3ChoiceUpdateParser(Update)}
     */
    @Test
    void testProcessStage3FillReportDenied() {
        // Arrange
        List<Update> updates = new ArrayList<>();
        Long chatId = 123L;
        int messageId = 223;

        String messageText = "st3_fill_report_master";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot,times(1)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse, times(1)).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)
     *                     @link TelegramBotUpdatesListener#stage3ChoiceUpdateParser(Update)}
     */
    @Test
    void testProcessStage3SendReportDenied() {
        // Arrange
        List<Update> updates = new ArrayList<>();

        Long chatId = 123L;
        int messageId = 223;

        String messageText = "st3_send_report";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);
        when(telegramBot.execute(any(SendPhoto.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot,times(2)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse, times(1)).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
    }

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)
     *                     @link TelegramBotUpdatesListener#stage3ChoiceUpdateParser(Update)}
     */
    @Test
    void testProcessStage3CancelReport() {
        // Arrange
        List<Update> updates = new ArrayList<>();

        Long chatId = 123L;
        int messageId = 223;

        String messageText = "st3_cancel";

        User user = mock(User.class);
        when(user.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.messageId()).thenReturn(messageId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(messageText);
        when(callbackQuery.from()).thenReturn(user);
        when(callbackQuery.message()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);

        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);

        // Act
        telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot,times(2)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse, times(1)).isOk();
        verify(callbackQuery, atLeastOnce()).data();
        verify(callbackQuery, atLeastOnce()).from();
    }
}

