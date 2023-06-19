package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactsForDogsShelterRepository extends JpaRepository<ContactsForDogsShelter, Long> {
    @Query(value = "SELECT * FROM contacts_For_Dogs_Shelter", nativeQuery = true)
    List<ContactsForDogsShelter> findAllContacts();

    Optional<ContactsForDogsShelter> findByContact(String contact);
}
