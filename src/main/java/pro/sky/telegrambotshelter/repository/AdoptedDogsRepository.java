package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.AdoptedDogs;

public interface AdoptedDogsRepository extends JpaRepository<AdoptedDogs, Long> {
    AdoptedDogs findByIdUser (Long idUser);

}
