package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;

import java.util.List;

@Service
public class ContactsForDogsShelterService {
    private UserService userService;

    private ContactsForDogsShelterRepository contactsForDogsShelterRepository;

    /**
     * метод для сохранения контактов в таблицу ContactsForDogsShelter
     */
    public void save(Long chatId, String contact) {
        Long userId = userService.getUserIdByChatId(chatId);
        String name = userService.getUserNameByChatId(chatId);

        contactsForDogsShelterRepository.saveContact(userId, name, contact);
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
}

