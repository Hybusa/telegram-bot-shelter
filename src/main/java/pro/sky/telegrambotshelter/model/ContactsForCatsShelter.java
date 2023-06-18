package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contacts_For_Cats_Shelter")
public class ContactsForCatsShelter {
    @Id
    private Long user_Id;
    private String name;
    private String contact;
    public ContactsForCatsShelter() {
    }

    public ContactsForCatsShelter(Long user_Id, String name, String contact) {
        this.user_Id = user_Id;
        this.name = name;
        this.contact = contact;
    }

    public Long getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(Long user_Id) {
        this.user_Id = user_Id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactsForCatsShelter)) return false;
        ContactsForCatsShelter contacts = (ContactsForCatsShelter) o;
        return Objects.equals(user_Id, contacts.user_Id) && Objects.equals(name, contacts.name) && Objects.equals(contact, contacts.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_Id, name, contact);
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "name=" + name +
                ", contact=" + contact + " \n";
    }
}
