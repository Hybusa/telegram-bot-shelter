package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactsForCatsShelterRepository extends JpaRepository<ContactsForCatsShelter, Long> {

    @Query(value = "SELECT * FROM contacts_For_Cats_Shelter", nativeQuery = true)
    List<ContactsForCatsShelter> findAllContacts();
    Optional<ContactsForCatsShelter> findByContact(String contact);
}
