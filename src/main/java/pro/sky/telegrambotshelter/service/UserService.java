package pro.sky.telegrambotshelter.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Report;
import org.webjars.NotFoundException;
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
     * метод для изменения выбора пользователя в БД
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
    public void saveContacts(User user, String contact) {
        Optional<User> optUser = userRepository.findUserByChatId(user.getChatId());
        User tmpUser;
        tmpUser = optUser.orElse(user);
        tmpUser.setContact(contact);
        userRepository.save(tmpUser);
    }

    /**
     * метод для создания пользователя
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }


    /**
     * метод для обновления пользователя
     */
    public Optional<User> updateUser(User user) {
        if (userRepository.existsById(user.getId()))
            return Optional.of(userRepository.save(user));
        return Optional.empty();
    }

    /**
     * метод для получения всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * метод для получения всех пользователей по типу приюта
     */
    public List<User> getAllUsersByShelterTypeChoice(String choice) {
        return userRepository.findAllByShelterTypeChoice(choice);
    }


    /**
     * метод удаления Пользователя по id
     */
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id))
            throw new NotFoundException("Pet id not found");
        userRepository.deleteById(id);
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

    public Long getUserIdByChatId(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        if (optUser.isEmpty())
            throw new NotFoundException("User was not found by chatId");
        return optUser.get().getId();
    }

    public String getUserNameByChatId(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        if (optUser.isEmpty())
            throw new NotFoundException("User was not found by chatId");
        return optUser.get().getName();
    }

    /**
     * метод для получения пользователя по id
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * метод для получения list с контактами пользователей приюта для собак
     */
    public List<String> getListUsersContactsWithDodShelter() {
        List<Long> usersId = new ArrayList<>(userRepository.listUsersIdFromDogsShelter());

        return getContacts(usersId);
    }

    /**
     * метод для получения типа приюта пользователя
     */
    public String getUsersShelterTypeChoice(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        if (optUser.isEmpty())
            throw new NotFoundException("User was not found by chatId");
        return optUser.get().getShelterTypeChoice();
    }

    /**
     * Метод удаления из таблицы users по chat_iD
     */
    public void deleteUsersByChatId(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        optUser.ifPresent(userRepository::delete);
    }

    @NotNull
    private List<String> getContacts(List<Long> usersId) {
        List<String> contactsFromCats = new ArrayList<>();

        for (Long l : usersId) {
            Optional<User> optUser = getUserById(l);
            if (optUser.isPresent()) {
                String contact = optUser.get().getContact();
                if (contact != null)
                    contactsFromCats.add(contact);
            }
        }
        return contactsFromCats;
    }

    /**
     * метод для получения контакта пользователя
     */
    public String getContact(Long chatId) {
        Optional<User> optUser = userRepository.findUserByChatId(chatId);
        if (optUser.isEmpty())
            throw new NotFoundException("User was not found by chatId");
        return optUser.get().getContact();
    }
    /**
     * получение всех пользователей в мапу (Id пользователя - пользователь)
     * */
    public Map<Long, User> getAllByIdNameMap() {

        List<User> userList = userRepository.findAll();
        Map<Long, User> usersIdNameMap = new HashMap<>();

        for (User user: userList) {
            usersIdNameMap.put(user.getId(), user);
        }

        return usersIdNameMap;

    }

    public Optional<User> getUserByChatId(Long chatId){
        return  userRepository.findUserByChatId(chatId);
    }
}
