package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ContactsForDogsShelterService {


    private final ContactsForDogsShelterRepository contactsForDogsShelterRepository;

    public ContactsForDogsShelterService(ContactsForDogsShelterRepository contactsForDogsShelterRepository) {
        this.contactsForDogsShelterRepository = contactsForDogsShelterRepository;
    }

    /**
     * метод для сохранения контактов в таблицу ContactsForDogsShelter
     */
    public void save(User user) {
        ContactsForDogsShelter contactsForDogsShelter
                = new ContactsForDogsShelter(user.getId(), user.getName(), user.getContact());
        contactsForDogsShelterRepository.save(contactsForDogsShelter);
    }


    /**
     * метод получения контактов из таблицы ContactsForDogsShelter
     */
    public List<ContactsForDogsShelter> getAll() {
        return contactsForDogsShelterRepository.findAllContacts();
    }

    /**
     * метод удаления контактов из таблицы ContactsForDogsShelter
     */
    public void deleteAll(List<ContactsForDogsShelter> contactsForDogsShelter) {
        contactsForDogsShelterRepository.deleteAll(contactsForDogsShelter);
    }

    public void deleteByContact(String contact) {

        Optional<ContactsForDogsShelter> optContactsForDogsShelter =
                contactsForDogsShelterRepository.findByContact(contact);
        optContactsForDogsShelter
                .ifPresent(contactsForCatsShelter -> contactsForDogsShelterRepository
                        .deleteById(contactsForCatsShelter.getUser_Id()));

    }
}

