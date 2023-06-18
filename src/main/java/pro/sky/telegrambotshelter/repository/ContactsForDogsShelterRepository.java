package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ContactsForDogsShelterRepository extends JpaRepository<ContactsForDogsShelter, Long> {
    @Query(value = "SELECT * FROM contacts_For_Dogs_Shelter", nativeQuery = true)
    List<ContactsForDogsShelter> findAllContacts();

}
