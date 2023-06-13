package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long chatId;

    private String contact;

    private boolean failed;

    private String shelterTypeChoice;

    @ManyToOne
    private Shelter shelter;

    public User() {
    }

    public User(String name, Long chatId) {
        this.name = name;
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getShelterTypeChoice() {
        return shelterTypeChoice;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setShelterTypeChoice(String shelterTypeChoice) {
        this.shelterTypeChoice = shelterTypeChoice;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return failed == user.failed
                && Objects.equals(name, user.name)
                && Objects.equals(chatId, user.chatId)
                && Objects.equals(contact, user.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chatId, contact, failed);
    }
}
