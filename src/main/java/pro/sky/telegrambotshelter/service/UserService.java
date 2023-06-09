package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.UserRepository;

/**сервис для работы с пользователем*/
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**метод для сохранения юзера в БД*/
    public void save(User user) {
        userRepository.save(user);
    }

    /**метод для изменения пользователя в БД*/
    public User update(User user) {
        return userRepository.save(user);
    }

    /**метод для получения контакта пользователя*/
    public String getUserPhone(Long chatId){
        return userRepository.findUserByChatId(chatId).getContact();
    }

}
