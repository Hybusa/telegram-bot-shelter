package pro.sky.telegrambotshelter.listener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.info}")
    private String botInfo;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String messageText = update.message().text();
            if (messageText.equals("/start")) {
                startBot(update.message().chat().id(), update.message().chat().firstName());
                choiceMessage(update.message().chat().id());
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void choiceMessage(long chatId) {

        telegramBot.execute(new SendMessage(chatId, "Please choose a type of shelter you're looking for")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(false)
                .replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton("Cats"),
                        new KeyboardButton("Dogs"))
                        .oneTimeKeyboard(true)
                        .resizeKeyboard(true)
                        .selective(true)));

        //        MessageTest.checkTextMessage(sendResponse.message());
//       sendResponse = telegramBot.execute(new SendMessage(chatId, "remove keyboard")
//               .replyMarkup(new ReplyKeyboardRemove())
//               .disableNotification(true)
//               .replyToMessageId(8087)
//       );
//        MessageTest.checkTextMessage(sendResponse.message());
//       sendResponse = telegramBot.execute(new SendMessage(chatId, "hide keyboard").replyMarkup(new ReplyKeyboardRemove()));

        // MessageTest.checkTextMessage(sendResponse.message());
        // SendResponse sendResponse = telegramBot
        //         .execute(new SendMessage(chatId, "Please choose a type of shelter you're looking for")
        //         .replyMarkup(new ReplyKeyboardMarkup("Cats", "Dogs")));
        //   MessageTest.checkTextMessage(sendResponse.message());
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage(chatId, "Hello, " + userName + "! \n" + botInfo);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        SendResponse response = telegramBot.execute(message);
        if (response.isOk()) {
            logger.info("Message: {} sent", message);
        } else {
            logger.error("Error sending. Code: " + response.errorCode());
        }
    }
}