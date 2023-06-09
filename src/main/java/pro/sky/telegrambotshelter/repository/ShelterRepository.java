package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.Shelter;

/**репозиторий для работы с БД приютов*/
@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    /**метод для поиска приюта по типу*/
    Shelter findShelterByShelterType(String shelterType);
}
