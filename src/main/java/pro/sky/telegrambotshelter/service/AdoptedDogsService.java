package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedDogs;
import pro.sky.telegrambotshelter.repository.AdoptedDogsRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public void update(AdoptedDogs adoptedDogs) {
        adoptedDogsRepository.save(adoptedDogs);
    }


    /**
     * редактирование последней даты очета AdoptedDogs
     */
    public void updateLastReports(Long idUser, LocalDateTime localDateTime) {

        Optional<AdoptedDogs> adoptedDogs = adoptedDogsRepository.findByIdUser(idUser);

        adoptedDogs.ifPresent(entity -> {

            entity.setLastReportDate(localDateTime);
            update(entity);

        });

    }

    /**
     * удаление сущности AdoptedDogs из БД
     */
    public void delete(AdoptedDogs adoptedDogs) {
        adoptedDogsRepository.delete(adoptedDogs);
    }

    /**
     * поиск всех записей adopted_cats
     */
    public List<AdoptedDogs> getAll() {
        return adoptedDogsRepository.findAll();
    }

    /**
     * получение всех AdoptedDogs в мапу (Id пользователя - AdoptedDog)
     */
    public Map<Long, AdoptedDogs> getAllAdoptedDogsToMap() {

        List<AdoptedDogs> adoptedDogsList = adoptedDogsRepository.findAll();
        Map<Long, AdoptedDogs> adoptedDogsMap = new HashMap<>();

        for (AdoptedDogs adoptedDog : adoptedDogsList) {
            adoptedDogsMap.put(adoptedDog.getIdUser(), adoptedDog);
        }

        return adoptedDogsMap;

    }

}
