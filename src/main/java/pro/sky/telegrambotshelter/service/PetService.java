package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

/**сервис для работы с БД питомцев*/
@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }


}
