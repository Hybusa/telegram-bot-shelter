package pro.sky.telegrambotshelter.service;

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
     * метод для изменения пользователя в БД
     */
    public void updateShelterChoiceByChatId(User user, String shelterTypeChoice){
        Optional<User> optUser = userRepository.findUserByChatId(user.getChatId());
        User tmpUser;
        tmpUser = optUser.orElse(user);
        tmpUser.setShelterTypeChoice(shelterTypeChoice);
        userRepository.save(tmpUser);
    }

    /**
     * метод для получения контакта пользователя
     */
    public Optional<User> getUserPhone(Long chatId) {
        return userRepository.findUserByChatId(chatId).get;
    }

    /**
     * метод для получения мапы с chat_id пользователей
     */
    public Map<Long, String> getMapUsersChatId() {
        Map<Long, String> usersId = new HashMap<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            usersId.put(user.getId(), user.getShelterTypeChoice());
        }
        return usersId;
    }

    /**
     * метод для получения пользователя по id
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    /**
     * метод для получения list с контактами пользователей приюта для собак
     */
    public Optional<List<String>> getListUsersContactsWithDodShelter() {
        List<Long> usersId = new ArrayList<>(userRepository.listUsersIdFromDogsShelter());
        List<String> contactsFromDods = new ArrayList<>();
        String contact;

        for (Long l : usersId) {
            contact = getUserById(l).getContact();
            if (contact != null) {
                contactsFromDods.add(getUserById(l).getContact());
            }
        }

        return contactsFromDods;
    }

    /**
     * метод для получения list с контактами пользователей приюта для кошек
     */
    public List<String> getListUsersContactsWithCatShelter() {
        List<Long> usersId = new ArrayList<>(userRepository.listUsersIdFromCatsShelter());
        List<String> contactsFromCats = new ArrayList<>();
        String contact;

        for (Long l : usersId) {
            contact = getUserById(l).getContact();
            if (contact != null) {
                contactsFromCats.add(getUserById(l).getContact());
            }
        }

        return contactsFromCats;
    }

}
