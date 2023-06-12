package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.Pet;

import java.util.Collection;

/**репозиторий для работы с БД животных*/
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Collection<Pet> findAllByShelterId(long shelterId);
    Pet findPetById(long idPet);

}
