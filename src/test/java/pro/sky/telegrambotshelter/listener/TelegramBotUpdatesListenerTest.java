package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.service.ShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${telegram.bot.info}")
    String botInfo;

    /**
     * Method under test: {@link TelegramBotUpdatesListener#process(List)}
     */
    @Test
    void testProcessStarNewUser() {
        // Arrange
        List<Update> updates = new ArrayList<>();
        String name = "Jane";
        String messageText = "/start";
        String returnMessageText = "Hello, " + name + "! \n" + botInfo;

        Long chatId = 123L;




        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(chatId);
        when(chat.firstName()).thenReturn(name);


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
        when(sendResponse.message()).thenReturn(returnMessage);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        updates.add(update);
        // Act

        this.telegramBotUpdatesListener.process(updates);

        // Assert
        verify(telegramBot,atLeast(2)).execute(Mockito.any(SendMessage.class));
        verify(sendResponse,atLeast(2)).isOk();
        verify(message, atLeastOnce()).chat();
        verify(message,atLeastOnce()).text();
        verify(chat,atLeastOnce()).id();
        verify(chat,atLeastOnce()).firstName();
        verify(userService,atMostOnce()).save(Mockito.any(pro.sky.telegrambotshelter.model.User.class));
    }



//    /**
//     * Method under test: {@link TelegramBotUpdatesListener#messageParser(Update)}
//     */
//    @Test
//    void testMessageParser() {
//        SendResponse sendResponse = mock(SendResponse.class);
//        when(sendResponse.isOk()).thenReturn(true);
//        when(sendResponse.errorCode()).thenReturn(-1);
//        when(telegramBot.execute(Mockito.<BaseRequest<SendMessage, SendResponse>>any())).thenReturn(sendResponse);
//        Chat chat = mock(Chat.class);
//        when(chat.id()).thenReturn(1L);
//        when(chat.firstName()).thenReturn("Jane");
//        Message message = mock(Message.class);
//        when(message.text()).thenReturn("Text");
//        when(message.chat()).thenReturn(chat);
//        Update update = mock(Update.class);
//        when(update.message()).thenReturn(message);
//        telegramBotUpdatesListener.messageParser(update);
//        verify(telegramBot).execute(Mockito.<BaseRequest<SendMessage, SendResponse>>any());
//        verify(sendResponse).isOk();
//        verify(update, atLeast(1)).message();
//        verify(message, atLeast(1)).chat();
//        verify(message).text();
//        verify(chat).id();
//        verify(chat).firstName();
//    }


}

