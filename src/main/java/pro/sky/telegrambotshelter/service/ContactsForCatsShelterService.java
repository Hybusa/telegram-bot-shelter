package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ContactsForCatsShelterService {



    private final ContactsForCatsShelterRepository contactsForCatsShelterRepository;

    public ContactsForCatsShelterService(ContactsForCatsShelterRepository contactsForCatsShelterRepository) {

        this.contactsForCatsShelterRepository = contactsForCatsShelterRepository;
    }

    /**
     * метод для сохранения контактов в таблицу ContactsForCatsShelter
     */
    public void save(User user) {
        ContactsForCatsShelter contactsForCatsShelter =
                new ContactsForCatsShelter(user);
        contactsForCatsShelterRepository.save(contactsForCatsShelter);
    }

    /**
     * метод получения контактов из таблицы ContactsForCatsShelter
     */
    public List<ContactsForCatsShelter> getAll() {
        return contactsForCatsShelterRepository.findAllContacts();
    }

    /**
     * метод удаления контактов из таблицы ContactsForCatsShelter
     */
    public void deleteAll(List<ContactsForCatsShelter> contactsForCatsShelter) {
        contactsForCatsShelterRepository.deleteAll(contactsForCatsShelter);
    }

    public void deleteByContact(String contact) {
        Optional<ContactsForCatsShelter> optContactsForCatsShelter =
                contactsForCatsShelterRepository.findByContact(contact);
        optContactsForCatsShelter
                .ifPresent(contactsForCatsShelter -> contactsForCatsShelterRepository
                        .deleteById(contactsForCatsShelter.getUser_Id()));
    }
}
