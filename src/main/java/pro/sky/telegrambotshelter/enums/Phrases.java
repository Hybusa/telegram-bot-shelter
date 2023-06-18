package pro.sky.telegrambotshelter.enums;

public enum Phrases {
    PROCESSING_UPDATE("Processing update: {}"),
    RESPONSE_STATUS("Response is {}"),
    ERROR_SENDING("Error sending. Code: "),
    RESTART_THE_BOT("Restart the bot"),
    CONTACT_RECEIVED("Thank you. Our volunteer will contact you!"),
    SHELTER_CHOICE_MESSAGE("Please choose a type of shelter you're looking for"),
    ;

    private final String s;
    Phrases(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }

}
