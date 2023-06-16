package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.List;
import java.util.Optional;

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
}
