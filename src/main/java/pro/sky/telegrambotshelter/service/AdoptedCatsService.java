package pro.sky.telegrambotshelter.service;

import liquibase.pro.packaged.A;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.repository.AdoptedCatsRepository;

@Service
public class AdoptedCatsService {

    private final AdoptedCatsRepository adoptedCatsRepository;

    public AdoptedCatsService(AdoptedCatsRepository adoptedCatsRepository) {
        this.adoptedCatsRepository = adoptedCatsRepository;
    }

    public void save(AdoptedCats adoptedCats) {
        adoptedCatsRepository.save(adoptedCats);
    }

    public AdoptedCats findById(Long id) {
        return adoptedCatsRepository.findById(id).get();
    }

    public AdoptedCats findLastAdoptedCats() {
        return adoptedCatsRepository.findFirstByIdPetIsNull().get();
    }

    public AdoptedCats findByIdPet(Long idPet) { return adoptedCatsRepository.findByIdPet(idPet); }

    public AdoptedCats findByIdUser(Long idUser) { return adoptedCatsRepository.findByIdUser(idUser); }

    public void deleteAdoptedCats(AdoptedCats adoptedCats) {adoptedCatsRepository.delete(adoptedCats);}

}
