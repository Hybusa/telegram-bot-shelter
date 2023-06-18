package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;


import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ContactScheduler.class})
@ExtendWith(SpringExtension.class)
public class ContactSchedulerTest {

    @Autowired
    private ContactScheduler contactScheduler;
    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private ContactsForCatsShelterService contactsForCatsShelterService;
    @MockBean
    private ContactsForDogsShelterService contactsForDogsShelterService;

    @BeforeEach
    public void initEach() {
        telegramBot = Mockito.mock(TelegramBot.class);
        contactScheduler = new ContactScheduler(telegramBot, shelterService, contactsForCatsShelterService, contactsForDogsShelterService);
    }

    @Test
    void checkerForCatShelter() {
        SendResponse response = mock(SendResponse.class);
        when(response.isOk()).thenReturn(true);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

        contactScheduler.checkerForCatShelter();

        verify(telegramBot).execute(Mockito.any(SendMessage.class));
        verify(response).isOk();
    }

    @Test
    void checkerForDogShelter() {
        SendResponse response = mock(SendResponse.class);
        when(response.isOk()).thenReturn(true);

        when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

        contactScheduler.checkerForDogShelter();

        verify(telegramBot).execute(Mockito.any(SendMessage.class));
        verify(response).isOk();
    }
}

