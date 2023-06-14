package pro.sky.telegrambotshelter.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;

import javax.transaction.Transactional;
import java.util.List;


@Repository
public interface ContactsForCatsShelterRepository extends JpaRepository<ContactsForCatsShelter, Long> {

    @Query(value = "SELECT * FROM contacts_For_Cats_Shelter", nativeQuery = true)
    List<ContactsForCatsShelter> findAllContacts();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO contacts_For_Cats_Shelter(user_Id, name, contact) values (?,?,?)", nativeQuery = true)
    void saveContact(Long user_id, String name, String contact);


}
