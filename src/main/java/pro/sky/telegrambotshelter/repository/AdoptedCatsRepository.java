package pro.sky.telegrambotshelter.repository;

import liquibase.pro.packaged.O;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.AdoptedCats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface AdoptedCatsRepository extends JpaRepository<AdoptedCats, Long> {

    AdoptedCats findByIdPet (Long idPet);
    AdoptedCats findByIdUser (Long idUser);

}
