package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.User;
/**репозиторий для работы с БД пользователей*/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**метод для поиска пользователя по chat_id*/
    User findUserByChatId(Long id);
}
