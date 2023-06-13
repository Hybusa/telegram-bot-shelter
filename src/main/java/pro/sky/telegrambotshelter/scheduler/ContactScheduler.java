package pro.sky.telegrambotshelter.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambotshelter.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambotshelter.model.User;

public class ContactScheduler {
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    private User user;

    @Scheduled(cron = "0/5 * * * * *")
    public void checker() {

    }

    private void createList() {

    }
}
