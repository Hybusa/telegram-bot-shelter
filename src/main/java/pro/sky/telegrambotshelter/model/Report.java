package pro.sky.telegrambotshelter.model;

import com.pengrad.telegrambot.model.PhotoSize;

import java.time.LocalDateTime;
import java.util.List;

public class Report {

    private Long chatId;
    private int messageId;

    private LocalDateTime addedDate;
    private boolean isActive;
    private String diet;
    private String health;
    private String adaptation;
    private String changes;

    List<PhotoSize> photos;

    public List<PhotoSize> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoSize> photos) {
        this.photos = photos;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getAdaptation() {
        return adaptation;
    }

    public void setAdaptation(String adaptation) {
        this.adaptation = adaptation;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public ReportSteps getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(ReportSteps currentStep) {
        this.currentStep = currentStep;
    }

    private ReportSteps currentStep;

    public void nextStep() {
        if(currentStep == null)
            currentStep = ReportSteps.Photo;
        else if(currentStep == ReportSteps.Photo)
            currentStep = ReportSteps.Health;
        else if(currentStep == ReportSteps.Health)
            currentStep = ReportSteps.Diet;
        else if(currentStep == ReportSteps.Diet)
            currentStep = ReportSteps.Adaptation;
        else if(currentStep == ReportSteps.Adaptation)
            currentStep = ReportSteps.Changes;
        else if(currentStep == ReportSteps.Changes)
            currentStep = null;
    }

    public String doTextReport() {
        return "Health:" + "\n" + health +
                "\nDiet:" + "\n" + diet +
                "\nAdaptation:" + "\n" + adaptation +
                "\n Changes" + "\n" + changes;
    }
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setNullFields(){
        health = null;
        diet = null;
        adaptation = null;
        changes = null;
        photos.clear();
    }
}
