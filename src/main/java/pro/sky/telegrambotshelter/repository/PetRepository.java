package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.Shelter;

import java.util.List;
import java.util.Optional;

/**репозиторий для работы с БД животных*/
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByShelter(Shelter shelter);
    Optional<Pet> getPetByUser_ChatId(Long chatId);
}
