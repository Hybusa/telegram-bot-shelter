package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ContactScheduler.class})
@ExtendWith(SpringExtension.class)
public class ContactSchedulerTest {
    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private ContactsForCatsShelterService contactsForCatsShelterService;
    @MockBean
    private ContactsForDogsShelterService contactsForDogsShelterService;

    /*@Test
    void checkerForCatShelter() {
        int volunteerChatId = 333;
        List<ContactsForCatsShelter> contactsForCat = new ArrayList<>();
        ContactsForCatsShelter contact = new ContactsForCatsShelter(333L, "Sam", "33333");
        contactsForCat.add(contact);
        ContactScheduler contactScheduler = new ContactScheduler(telegramBot, shelterService, contactsForCatsShelterService, contactsForDogsShelterService);
        SendResponse sendResponse = telegramBot.execute((new SendMessage(volunteerChatId, contactsForCat.toString())));


        when(shelterService.getVolunteerChatId("cats")).thenReturn(333);
        when(contactsForCatsShelterService.getAll()).thenReturn(contactsForCat);
        when(telegramBot.execute((new SendMessage(volunteerChatId, contactsForCat.toString())))).thenReturn(sendResponse.isOk())

        verify(telegramBot).execute((new SendMessage(volunteerChatId, contactsForCat.toString())));
    }
    */
    }

