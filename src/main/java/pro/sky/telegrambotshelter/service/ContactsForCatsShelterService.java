package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;

import java.util.List;

@Service
public class ContactsForCatsShelterService {

    private final UserService userService;

    private final ContactsForCatsShelterRepository contactsForCatsShelterRepository;

    public ContactsForCatsShelterService(UserService userService, ContactsForCatsShelterRepository contactsForCatsShelterRepository) {
        this.userService = userService;
        this.contactsForCatsShelterRepository = contactsForCatsShelterRepository;
    }

    /**
     * метод для сохранения контактов в таблицу ContactsForCatsShelter
     */
    public void save(Long chatId, String contact) {
        Long userId = userService.getUserIdByChatId(chatId);
        String name = userService.getUserNameByChatId(chatId);

        ContactsForCatsShelter contactsForCatsShelter = new ContactsForCatsShelter(userId, name, contact);
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
}
