package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
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
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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


    /**
     * Method under test: {@link ContactScheduler#checkerForCatShelter()}
     */
    @Test
    void testCheckerForCatShelter_NoEntriesInTheDB_MessageNotSent() {
        when(contactsForCatsShelterService.getAll()).thenReturn(new ArrayList<>());
        contactScheduler.checkerForCatShelter();
        verify(contactsForCatsShelterService).getAll();
        verify(telegramBot,never()).execute(Mockito.any(SendMessage.class));
    }


    /**
     * Method under test: {@link ContactScheduler#checkerForCatShelter()}
     */
    @Test
    void testCheckerForCatShelter_CorrectParams_MethodCallsVerified() {
        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);
        when(shelterService.getVolunteerChatId(anyString())).thenReturn(1L);

        ContactsForCatsShelter contactsForCatsShelter = new ContactsForCatsShelter();
        contactsForCatsShelter.setContact("Contact");
        contactsForCatsShelter.setName("Name");

        ArrayList<ContactsForCatsShelter> contactsForCatsShelterList = new ArrayList<>();
        contactsForCatsShelterList.add(contactsForCatsShelter);
        when(contactsForCatsShelterService.getAll()).thenReturn(contactsForCatsShelterList);
        contactScheduler.checkerForCatShelter();

        verify(telegramBot).execute(any(SendMessage.class));
        verify(sendResponse).isOk();
        verify(shelterService).getVolunteerChatId(any());
        verify(contactsForCatsShelterService, atLeast(1)).getAll();
    }


    /**
     * Method under test: {@link ContactScheduler#checkerForDogShelter()}
     */
    @Test
    void testCheckerForDogShelter_NoEntriesInTheDB_MessageNotSent() {
        when(contactsForCatsShelterService.getAll()).thenReturn(new ArrayList<>());
        contactScheduler.checkerForCatShelter();
        verify(contactsForCatsShelterService).getAll();
        verify(telegramBot,never()).execute(Mockito.any(SendMessage.class));
    }

    /**
     * Method under test: {@link ContactScheduler#checkerForDogShelter()}
     */
    @Test
    void testCheckerForDogShelter_CorrectParams_MethodCallsVerified() {
        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(sendResponse.errorCode()).thenReturn(-1);
        when(telegramBot.execute(Mockito.<BaseRequest<SendMessage, SendResponse>>any())).thenReturn(sendResponse);
        when(shelterService.getVolunteerChatId(anyString())).thenReturn(1L);

        ContactsForDogsShelter contactsForDogsShelter = new ContactsForDogsShelter();
        contactsForDogsShelter.setContact("Contact");
        contactsForDogsShelter.setName("Name");

        ArrayList<ContactsForDogsShelter> contactsForDogsShelterList = new ArrayList<>();
        contactsForDogsShelterList.add(contactsForDogsShelter);
        when(contactsForDogsShelterService.getAll()).thenReturn(contactsForDogsShelterList);
        contactScheduler.checkerForDogShelter();
        verify(telegramBot).execute(any(SendMessage.class));
        verify(sendResponse).isOk();
        verify(shelterService).getVolunteerChatId(anyString());
        verify(contactsForDogsShelterService, atLeast(1)).getAll();
    }


    /**
     * Method under test: {@link ContactScheduler#createInlineKeyboardMarkup(String)}
     */
    @Test
    void testCreateInlineKeyboardMarkup_CorrectParamsForCats_ReturnsCorrectInlineKeyboard() {
        String contact = "+7916";
        String name = "Alex";
        ContactsForCatsShelter contactsForCatsShelter = new ContactsForCatsShelter();
        contactsForCatsShelter.setContact(contact);
        contactsForCatsShelter.setName(name);
        when(contactsForCatsShelterService.getAll()).thenReturn(List.of(contactsForCatsShelter));


        List<InlineKeyboardButton[]> inlineKeyboardButtonsList = new ArrayList<>();
        inlineKeyboardButtonsList
                .add(new InlineKeyboardButton[]{new InlineKeyboardButton(name
                        + ": " + contact)
                        .callbackData("vol_contact/" + contact)});
        InlineKeyboardButton[][] inlineKeyboardButtonsArr = new InlineKeyboardButton[inlineKeyboardButtonsList.size()][1];
        inlineKeyboardButtonsList.toArray(inlineKeyboardButtonsArr);

        InlineKeyboardMarkup expected = new InlineKeyboardMarkup(inlineKeyboardButtonsArr);


        InlineKeyboardMarkup actual = contactScheduler.createInlineKeyboardMarkup("cats");

        assertEquals(actual,expected);
        verify(contactsForCatsShelterService).getAll();
    }

    /**
     * Method under test: {@link ContactScheduler#createInlineKeyboardMarkup(String)}
     */
    @Test
    void testCreateInlineKeyboardMarkup_CorrectParamsForDogs_ReturnsCorrectInlineKeyboard() {
        String contact = "+7916";
        String name = "Alex";
        ContactsForDogsShelter contactsForDogsShelter = new ContactsForDogsShelter();
        contactsForDogsShelter.setContact(contact);
        contactsForDogsShelter.setName(name);
        when(contactsForDogsShelterService.getAll()).thenReturn(List.of(contactsForDogsShelter));

        List<ContactsForDogsShelter> contactsForShelter = new ArrayList<>();
        contactsForShelter.add(contactsForDogsShelter);

        List<InlineKeyboardButton[]> inlineKeyboardButtonsList = new ArrayList<>();
        inlineKeyboardButtonsList
                .add(new InlineKeyboardButton[]{new InlineKeyboardButton(name
                        + ": " + contact)
                        .callbackData("vol_contact/" + contact)});
        InlineKeyboardButton[][] inlineKeyboardButtonsArr = new InlineKeyboardButton[inlineKeyboardButtonsList.size()][1];
        inlineKeyboardButtonsList.toArray(inlineKeyboardButtonsArr);

        InlineKeyboardMarkup expected = new InlineKeyboardMarkup(inlineKeyboardButtonsArr);


        InlineKeyboardMarkup actual = contactScheduler.createInlineKeyboardMarkup("dogs");

        assertEquals(actual,expected);
        verify(contactsForDogsShelterService).getAll();
    }


    /**
     * Method under test: {@link ContactScheduler#createInlineKeyboardMarkup(String)}
     */
    @Test
    void testCreateInlineKeyboardMarkup_EmptyDB_ReturnsEmptyInlineKeyboard() {
        String contact = "+7916";
        String name = "Alex";
        ContactsForDogsShelter contactsForDogsShelter = new ContactsForDogsShelter();
        contactsForDogsShelter.setContact(contact);
        contactsForDogsShelter.setName(name);
        when(contactsForDogsShelterService.getAll()).thenReturn(List.of(contactsForDogsShelter));

        List<ContactsForDogsShelter> contactsForShelter = new ArrayList<>();
        contactsForShelter.add(contactsForDogsShelter);

        InlineKeyboardMarkup expected = new InlineKeyboardMarkup();


        InlineKeyboardMarkup actual = contactScheduler.createInlineKeyboardMarkup("cats");

        assertEquals(actual,expected);
        verify(contactsForCatsShelterService).getAll();
    }
}

