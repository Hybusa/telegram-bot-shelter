package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;

@Repository
public interface ContactsForDogsShelterRepository extends JpaRepository<ContactsForDogsShelter, Long> {
}
