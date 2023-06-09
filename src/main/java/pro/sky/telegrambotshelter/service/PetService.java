package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.List;
import java.util.Optional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**сервис для работы с БД питомцев*/
@Service
public class PetService {
    private final PetRepository petRepository;
    private final ShelterService shelterService;

    public PetService(PetRepository petRepository, ShelterService shelterService) {
        this.petRepository = petRepository;
        this.shelterService = shelterService;
    }


    /**Создать питомца*/
    public Pet createPet(Pet pet) {
       return petRepository.save(pet);
    }

    /**Обновить информацию о питомце*/
    public Optional<Pet> updatePet(Pet pet) {
        if(petRepository.existsById(pet.getId()))
            return Optional.of(petRepository.save(pet));
        return Optional.empty();
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

    /**Получить всех питомцев*/
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    /**Получить всех питомцев из конерктного приюта*/
    public List<Pet> getAllByShelterId(Long shelterId) {
        return petRepository.findAllByShelter(shelterService.getShelterById(shelterId)
                .orElseThrow(() -> new NotFoundException("Shelter not found")));
    }

    /**Получить питомца по id*/
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    /**метод для удаления питомца из ДБ*/
    public void deletePet(Long id){
        if(!petRepository.existsById(id))
            throw new NotFoundException("Pet id not found");
        petRepository.deleteById(id);
    }

    /**метод определяет наличие усыновленного питомца по chat id*/
    public boolean isExistUser(Long chatId){
        return petRepository.getPetByUser_ChatId(chatId).isPresent();
    }
}
