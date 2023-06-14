package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.User;

import java.util.List;
import java.util.Optional;

/**
 * репозиторий для работы с БД пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * метод для поиска пользователя по chat_id
     */
    Optional<User> findUserByChatId(Long id);



    /**
     * список chat_id пользователей
     */
    @Query(value = "SELECT chat_id FROM users", nativeQuery = true)
    List<Long> listChatIdUsers();


}
