package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.AdoptedDogs;

import java.util.Optional;

@Repository
public interface AdoptedDogsRepository extends JpaRepository<AdoptedDogs, Long> {
    Optional<AdoptedDogs> findByIdUser (Long idUser);

}
