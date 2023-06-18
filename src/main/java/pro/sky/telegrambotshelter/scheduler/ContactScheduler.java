package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import liquibase.pro.packaged.L;
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

    /**
     * метод для отправки каждый час списка контактов волонтеру приюта для кошек
     */
     @Scheduled(cron = "0 * * * *")
    public void checkerForCatShelter() {

        Long volunteerChatIdCatsShelter = shelterService.getVolunteerChatId("Cats");
        List<ContactsForCatsShelter> contactsForCatsShelter = contactsForCatsShelterService.getAll();

        if (contactsForCatsShelter == null)
            return;
        SendResponse response = telegramBot.execute(new SendMessage(volunteerChatIdCatsShelter, contactsForCatsShelter.toString()));
        if (response.isOk()) {
            contactsForCatsShelterService.deleteAll(contactsForCatsShelter);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    /**
     * метод для отправки каждый час списка контактов волонтеру приюта для собак
     */
     @Scheduled(cron = "0 * * * *")
    public void checkerForDogShelter() {

        Long volunteerChatIdDogsShelter = shelterService.getVolunteerChatId("dogs");
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
