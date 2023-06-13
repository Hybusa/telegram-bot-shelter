package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;

import java.util.List;

@Service
public class ContactsForCatsShelterService {

    private ContactsForCatsShelterRepository contactsForCatsShelterRepository;

    private UserService userService;

    public void save(ContactsForCatsShelter userId){
        contactsForCatsShelterRepository.save(userId);
    }

    public List<ContactsForCatsShelter> getAll(){
        return contactsForCatsShelterRepository.findAllUsersId();
    }

    public void deleteAll(List<ContactsForCatsShelter> contactsForCatsShelter){
        for(ContactsForCatsShelter id : contactsForCatsShelter){
            contactsForCatsShelterRepository.deleteAllByUserId(id.getUser_Id());
        }
    }

}
