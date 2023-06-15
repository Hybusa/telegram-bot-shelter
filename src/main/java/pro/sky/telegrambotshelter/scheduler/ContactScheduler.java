package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.List;
@Service
public class ContactScheduler {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private TelegramBot telegramBot;
    private ShelterService shelterService;
    private ContactsForCatsShelterService contactsForCatsShelterService;
    private ContactsForDogsShelterService contactsForDogsShelterService;

    public ContactScheduler(TelegramBot telegramBot, ShelterService shelterService, ContactsForCatsShelterService contactsForCatsShelterService, ContactsForDogsShelterService contactsForDogsShelterService) {
        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.contactsForCatsShelterService = contactsForCatsShelterService;
        this.contactsForDogsShelterService = contactsForDogsShelterService;
    }

    // @Scheduled(cron = "0 * * * *")
    public void checkerForCatShelter() {

        int volunteerChatIdCatsShelter = shelterService.getVolunteerChatId("cats");
        List<ContactsForCatsShelter> contactsForCat = contactsForCatsShelterService.getAll();

        if (contactsForCat == null)
            return;
        SendResponse response = telegramBot.execute(new SendMessage(volunteerChatIdCatsShelter, contactsForCat.toString()));
        if (response.isOk()) {
            contactsForCatsShelterService.deleteAll(contactsForCat);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    // @Scheduled(cron = "0 * * * *")
    public void checkerForDogShelter() {

        int volunteerChatIdDogsShelter = shelterService.getVolunteerChatId("dogs");
        List<ContactsForDogsShelter> contactsForDogsShelter = contactsForDogsShelterService.getAll();

        if (contactsForDogsShelter == null)
            return;
        SendResponse response = telegramBot.execute(new SendMessage(volunteerChatIdDogsShelter, contactsForDogsShelter.toString()));
        if (response.isOk()) {
            contactsForDogsShelterService.deleteAll(contactsForDogsShelter);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }
}
