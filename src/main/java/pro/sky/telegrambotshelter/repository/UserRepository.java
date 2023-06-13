package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.User;

import java.util.List;
import java.util.Optional;

/**репозиторий для работы с БД пользователей*/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**метод для поиска пользователя по chat_id*/
    Optional<User> findUserByChatId(Long id);


    /**список chat_id пользователей*/
    @Query(value = "SELECT chat_id FROM users", nativeQuery = true)
    List<Long> listChatIdUsers();


    /**список id пользователей приюта для собак*/
    @Query(value = "SELECT DISTINCT user_id FROM user_shelter_join WHERE shelter_id = 0", nativeQuery = true)
    List<Long> listUsersIdFromDogsShelter();


    /**список id пользователей приюта для кошек*/
    @Query(value = "SELECT DISTINCT user_id FROM user_shelter_join WHERE shelter_id = 1", nativeQuery = true)
    List<Long> listUsersIdFromCatsShelter();

    List<User> findUserByShelterTypeChoice(String shelterType);

}
