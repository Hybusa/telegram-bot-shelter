package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.Pet;
/**репозиторий для работы с БД животных*/
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
