package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedDogs;
import pro.sky.telegrambotshelter.repository.AdoptedDogsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdoptedDogsService {

    private final AdoptedDogsRepository adoptedDogsRepository;

    public AdoptedDogsService(AdoptedDogsRepository adoptedDogsRepository) {
        this.adoptedDogsRepository = adoptedDogsRepository;
    }

    /**
     * сохранение сущности AdoptedDogs в БД
     */
    public void save(AdoptedDogs adoptedDogs) {
        adoptedDogsRepository.save(adoptedDogs);
    }

    /**
     * редактирование сущности AdoptedDogs в БД
     */
    public void update(AdoptedDogs adoptedDogs) {adoptedDogsRepository.save(adoptedDogs);}

    /**
     * удаление сущности AdoptedDogs из БД
     */
    public void deleteAdoptedDogs(AdoptedDogs adoptedDogs) {
        adoptedDogsRepository.delete(adoptedDogs);
    }

    /**
     * поиск сущности AdoptedDogs по Id питомца
     */
    public AdoptedDogs findByIdPet(Long idPet) {
        return adoptedDogsRepository.findByIdPet(idPet);
    }

    /**
     * поиск сущности AdoptedDogs по Id пользователя
     */
    public AdoptedDogs findByIdUser(Long idUser) {
        return adoptedDogsRepository.findByIdUser(idUser);
    }


    /**
     * поиск всех Id пользователей которые хотят забрать собак
     */
    public List<Long> getAllIdUser() {

        List<AdoptedDogs> adoptedDogs = adoptedDogsRepository.findAll();
        List<Long> idSUsers = new ArrayList<>();

        if (adoptedDogs.isEmpty()) {
            return null;
        } else {
            for (AdoptedDogs aD : adoptedDogs) {
                idSUsers.add(aD.getIdUser());
            }
        }

        return idSUsers;

    }

}
