package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;

@Service
public class ContactsForDogsShelterService {

    private ContactsForDogsShelterRepository contactsForDogsShelterRepository;
    public void save(ContactsForDogsShelter userId){
        contactsForDogsShelterRepository.save(userId);
    }

    public void delete(ContactsForDogsShelter userId){
        contactsForDogsShelterRepository.delete(userId);
    }
}
