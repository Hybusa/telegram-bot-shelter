package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.repository.AdoptedCatsRepository;

import java.util.ArrayList;
import java.util.List;

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
     * удаление сущности AdoptedCats из БД
     */
    public void deleteAdoptedCats(AdoptedCats adoptedCats) {
        adoptedCatsRepository.delete(adoptedCats);
    }


    /**
     * поиск сущности AdoptedCats по Id питомца
     */
    public AdoptedCats getByIdPet(Long idPet) {
        return adoptedCatsRepository.findByIdPet(idPet);
    }

    /**
     * поиск сущности AdoptedCats по Id пользователя
     */
    public AdoptedCats getByIdUser(Long idUser) {
        return adoptedCatsRepository.findByIdUser(idUser);

    }

    /**
     * поиск всех Id пользователей которые хотят забрать котов
     */
    public List<Long> getAllIdUser() {

        List<AdoptedCats> adoptedCats = adoptedCatsRepository.findAll();
        List<Long> idSUsers = new ArrayList<>();

        if (adoptedCats.isEmpty()) {
            return null;
        } else {
            for (AdoptedCats aC : adoptedCats) {
                idSUsers.add(aC.getIdUser());
            }
        }

        return idSUsers;

    }

}
