package pro.sky.telegrambotshelter.service;

import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.UserRepository;

import java.util.*;

/**
 * сервис для работы с пользователем
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * метод для сохранения пользователя в БД
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * метод для изменения типа приюта пользователя в БД
     */
    public void updateShelterChoiceByChatId(User user, String shelterTypeChoice) {
        Optional<User> optUser = userRepository.findUserByChatId(user.getChatId());
        User tmpUser;
        tmpUser = optUser.orElse(user);
        tmpUser.setShelterTypeChoice(shelterTypeChoice);
        userRepository.save(tmpUser);
    }

    /**
     * метод для добавления контактов пользователя в БД
     */
    public void saveContactsByChatId(User user, String contact) {
        Optional<User> optUser = userRepository.findUserByChatId(user.getChatId());
        User tmpUser;
        tmpUser = optUser.orElse(user);
        tmpUser.setContact(contact);
        userRepository.save(tmpUser);
    }

    /**
     * метод для получения мапы с chat_id пользователей
     */
    public Map<Long, String> getMapUsersChatIdWithChoice() {
        Map<Long, String> usersId = new HashMap<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String shelterTypeChoice = user.getShelterTypeChoice();
            if (shelterTypeChoice != null)
                usersId.put(user.getChatId(), user.getShelterTypeChoice());
        }
        return usersId;
    }

    public Long getUserIdByChatId(Long chatId){
       return userRepository.findUserByChatId(chatId).get().getId();
    }

    /**
     * метод для получения пользователя по id
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    /**
     * метод для получения типа приюта пользователя
     */
    public String getUsersShelterTypeChoice(Long chatId){
        return userRepository.findUserByChatId(chatId).get().getShelterTypeChoice();
    }


    /**
     * метод для получения list с контактами пользователей приюта для собак
     */
    public List<String> getListUsersContactsWithDodShelter() {
        List<Long> usersId = new ArrayList<>(userRepository.listUsersIdFromDogsShelter());

        return getContacts(usersId);
    }


    /**
     * метод для получения контакта пользователя
     */
    public String getContact(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        return optUser.get().getContact();
    }

    /**
     * метод для получения list с контактами пользователей приюта для кошек
     */
    public List<String> getListUsersContactsWithCatShelter() {
        List<Long> usersId = new ArrayList<>(userRepository.listUsersIdFromCatsShelter());

        return getContacts(usersId);
    }

    @NotNull
    private List<String> getContacts(List<Long> usersId) {
        List<String> contacts = new ArrayList<>();

        for (Long l : usersId) {
            Optional<User> optUser = getUserById(l);
            if (optUser.isPresent()) {
                String contact = optUser.get().getContact();
                if (contact != null)
                    contacts.add(contact);
            }
        }
        return contacts;
    }


}
