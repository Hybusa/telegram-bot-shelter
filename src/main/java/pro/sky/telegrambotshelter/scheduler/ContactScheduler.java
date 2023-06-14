package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambotshelter.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;

import java.util.List;

public class ContactScheduler {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private TelegramBot telegramBot;
    private User user;

    private ContactsForCatsShelterService contactsForCatsShelterService;
    private ContactsForDogsShelterService contactsForDogsShelterService;


    // @Scheduled(cron = "0 * * * *")
    public void checkerForCatShelter() {

        Long volunteerChatIdCatsShelter = 12242L;
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

        Long volunteerChatIdDogsShelter = 12222L;
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
