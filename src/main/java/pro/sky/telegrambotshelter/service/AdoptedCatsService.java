package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.repository.AdoptedCatsRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdoptedCatsService {

    private final AdoptedCatsRepository adoptedCatsRepository;

    public AdoptedCatsService(AdoptedCatsRepository adoptedCatsRepository) {
        this.adoptedCatsRepository = adoptedCatsRepository;
    }

    /**
     * сохранение сущности AdoptedCats в БД
     */
    public void save(AdoptedCats adoptedCats) {
        adoptedCatsRepository.save(adoptedCats);
    }

    /**
     * редактирование сущности AdoptedCats в БД
     */
    public void update(AdoptedCats adoptedCats) {
        adoptedCatsRepository.save(adoptedCats);
    }

    /**
     * редактирование последней даты очета AdoptedCats
     */
    public void updateLastReports(Long idUser, LocalDateTime localDateTime) {

        Optional<AdoptedCats> adoptedCats = adoptedCatsRepository.findByIdUser(idUser);

        adoptedCats.ifPresent(entity -> {

            entity.setLastReportDate(localDateTime);
            update(entity);

        });

    }

    /**
     * удаление сущности AdoptedCats из БД
     */
    public void delete(AdoptedCats adoptedCats) {
        adoptedCatsRepository.delete(adoptedCats);
    }

    /**
     * поиск всех записей adopted_cats
     */
    public List<AdoptedCats> getAll() {
        return adoptedCatsRepository.findAll();
    }

    /**
     * получение всех AdoptedCats в мапу (Id пользователя - AdoptedCat)
     */
    public Map<Long, AdoptedCats> getAllAdoptedCatsToMap() {

        List<AdoptedCats> adoptedCatsList = adoptedCatsRepository.findAll();
        Map<Long, AdoptedCats> adoptedCatsMap = new HashMap<>();

        for (AdoptedCats adoptedCat : adoptedCatsList) {
            adoptedCatsMap.put(adoptedCat.getIdUser(), adoptedCat);
        }

        return adoptedCatsMap;

    }


}
