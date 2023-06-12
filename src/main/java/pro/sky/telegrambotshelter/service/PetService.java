package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.Collection;

/**сервис для работы с БД питомцев*/
@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

// для волонтера
    /** Добавил метод получения всех питомцев приюта*/
    public Collection<Pet> getAllPetByShelters (long shelterId) {
       return petRepository.findAllByShelterId(shelterId);
    }

    public Pet getPetById(Long idPet) {
        return petRepository.findPetById(idPet);
    }

    public void editPetByVolunteer(Pet pet) {
        petRepository.save(pet);
    }




}
