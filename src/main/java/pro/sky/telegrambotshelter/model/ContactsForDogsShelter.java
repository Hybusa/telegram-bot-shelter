package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "contacts_For_Dogs_Shelter")
public class ContactsForDogsShelter implements ContactForShelter {

    @Id
    private Long user_id;
    private String name;
    private String contact;

    public ContactsForDogsShelter() {
    }

    public ContactsForDogsShelter(Long user_id, String name, String contact) {
        this.user_id = user_id;
        this.name = name;
        this.contact = contact;
    }

    public Long getUser_Id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactsForDogsShelter)) return false;
        ContactsForDogsShelter that = (ContactsForDogsShelter) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(name, that.name) && Objects.equals(contact, that.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, name, contact);
    }

    @Override
    public String toString() {
        return "name=" + name +
                ", contact=" + contact + " \n";
    }

}
