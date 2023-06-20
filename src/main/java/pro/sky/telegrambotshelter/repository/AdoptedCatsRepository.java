package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.AdoptedCats;

import java.util.Optional;

@Repository
public interface AdoptedCatsRepository extends JpaRepository<AdoptedCats, Long> {
    Optional<AdoptedCats> findByIdUser (Long idUser);

}
