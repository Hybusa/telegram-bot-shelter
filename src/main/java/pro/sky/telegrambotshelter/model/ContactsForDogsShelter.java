package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "contacts_For_Dogs_Shelter")
public class ContactsForDogsShelter {

    @Id
    Long user_id;
    private String name;
    private String contact;

    public ContactsForDogsShelter(){}

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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
        return "ContactsForDogsShelter{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
