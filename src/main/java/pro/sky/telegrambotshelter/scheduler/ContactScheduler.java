package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.enums.Phrases;
import pro.sky.telegrambotshelter.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambotshelter.model.ContactForShelter;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactScheduler {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final ShelterService shelterService;
    private final ContactsForCatsShelterService contactsForCatsShelterService;
    private final ContactsForDogsShelterService contactsForDogsShelterService;
    private final String CRON = "10 * * * * *";


    public ContactScheduler(TelegramBot telegramBot,
                            ShelterService shelterService,
                            ContactsForCatsShelterService contactsForCatsShelterService,
                            ContactsForDogsShelterService contactsForDogsShelterService) {
        this.telegramBot = telegramBot;
        this.shelterService = shelterService;
        this.contactsForCatsShelterService = contactsForCatsShelterService;
        this.contactsForDogsShelterService = contactsForDogsShelterService;
    }

    /**
     * метод для отправки каждый час списка контактов волонтеру приюта для кошек
     */
    @Scheduled(cron = CRON)
    public void checkerForCatShelter() {
        if(contactsForCatsShelterService.getAll().isEmpty())
            return;
        SendResponse response = sendAndGetResponse("cats");

        if (response.isOk()) {
            logger.info(Phrases.RESPONSE_STATUS.toString(), response);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    /**
     * метод для отправки каждый час списка контактов волонтеру приюта для собак
     */
    @Scheduled(cron = CRON)
    public void checkerForDogShelter() {
        if(contactsForDogsShelterService.getAll().isEmpty())
            return;
        SendResponse response = sendAndGetResponse("dogs");

        if (response.isOk()) {
            logger.info(Phrases.RESPONSE_STATUS.toString(), response);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }

    private SendResponse sendAndGetResponse(String choice) {
        Long volunteerChatIdCatsShelter = shelterService.getVolunteerChatId(choice);
        return telegramBot.execute(new SendMessage(volunteerChatIdCatsShelter,
                "To contact. Press the button after you called").replyMarkup(createInlineKeyboardMarkup(choice)));
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup(String choice) {
        List<? extends ContactForShelter> contactsForShelter = new ArrayList<>();
        switch (choice) {
            case "cats":
                contactsForShelter = contactsForCatsShelterService.getAll();
                break;
            case "dogs":
                contactsForShelter = contactsForDogsShelterService.getAll();
                break;
        }

        if (contactsForShelter.isEmpty())
            return new InlineKeyboardMarkup();

        List<InlineKeyboardButton[]> inlineKeyboardButtonsList = new ArrayList<>();

        for (ContactForShelter contactForShelter : contactsForShelter) {
            String contactString = contactForShelter.getContact();
            inlineKeyboardButtonsList
                    .add(new InlineKeyboardButton[]{new InlineKeyboardButton(contactForShelter.getName()
                            + ": " + contactString)
                            .callbackData("vol_contact/" + contactString)});
        }
        InlineKeyboardButton[][] inlineKeyboardButtonsArr = new InlineKeyboardButton[inlineKeyboardButtonsList.size()][1];
        inlineKeyboardButtonsList.toArray(inlineKeyboardButtonsArr);


        return new InlineKeyboardMarkup(inlineKeyboardButtonsArr);
    }
}
