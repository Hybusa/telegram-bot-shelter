package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;

import java.util.List;

@Service
public class ContactsForCatsShelterService {

    private UserService userService;

    private ContactsForCatsShelterRepository contactsForCatsShelterRepository;


    /**
     * метод для сохранения контактов в таблицу ContactsForCatsShelter
     */
    public void save(Long chatId, String contact){
        Long userId = userService.getUserIdByChatId(chatId);
        String name = userService.getUserNameByChatId(chatId);

        contactsForCatsShelterRepository.saveContact(userId, name, contact);
    }

    /**
     * метод получения контактов из таблицы ContactsForCatsShelter
     */
    public List<ContactsForCatsShelter> getAll(){
        return contactsForCatsShelterRepository.findAllContacts();
    }

    /**
     * метод удаления контактов из таблицы ContactsForCatsShelter
     */
    public void deleteAll(List<ContactsForCatsShelter> contactsForCatsShelter){
        contactsForCatsShelterRepository.deleteAll(contactsForCatsShelter);
    }
}
