package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**сервис для работы с БД питомцев*/
@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * редактирование питомца волонтером
     */
    public void editPetByVolunteer(Pet pet) {
        petRepository.save(pet);
    }

    /**
     * получение всех питомцев в мапу (Id - питомец)
     */
    public Map<Long, Pet> getAllPetsMapIdPet() {

        List<Pet> pets = petRepository.findAll();
        Map<Long, Pet> petsShelterMap = new HashMap<>();

        for (Pet pet: pets) {
            petsShelterMap.put(pet.getId(), pet);
        }

        return petsShelterMap;

    }

}
