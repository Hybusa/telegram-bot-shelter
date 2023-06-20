package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.AdoptedDogs;
import pro.sky.telegrambotshelter.repository.AdoptedDogsRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = AdoptedDogsService.class)
@ExtendWith(SpringExtension.class)
class AdoptedDogsServiceTest {

    @MockBean
    private AdoptedDogsRepository adoptedDogsRepository;

    @Autowired
    private AdoptedDogsService adoptedDogsService;

    private AdoptedDogs adoptedDogs;

    private final Long idPet = 200L;

    private final Long idUser = 1L;

    private LocalDateTime localDateTime;

    @BeforeEach
    public void init() {

        localDateTime = LocalDateTime.now();
        adoptedDogs = new AdoptedDogs(idPet, idUser, localDateTime, localDateTime.plusDays(30), localDateTime);

    }

    @Test
    void save_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedDogsRepository.save(adoptedDogs)).thenReturn(adoptedDogs);

        //Test start

        adoptedDogsService.save(adoptedDogs);
        verify(adoptedDogsRepository).save(adoptedDogs);
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

    @Test
    void update_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedDogsRepository.save(adoptedDogs)).thenReturn(adoptedDogs);

        //Test start

        adoptedDogsService.update(adoptedDogs);
        verify(adoptedDogsRepository).save(adoptedDogs);
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

    @Test
    void updateLastReports_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedDogsRepository.findByIdUser(idUser)).thenReturn(Optional.ofNullable(adoptedDogs));
        when(adoptedDogsRepository.save(adoptedDogs)).thenReturn(adoptedDogs);

        //Test start

        adoptedDogsService.updateLastReports(idUser, localDateTime);
        verify(adoptedDogsRepository).findByIdUser(idUser);
        verify(adoptedDogsRepository).save(adoptedDogs);
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

    @Test
    void delete_success() {

        //Input data preparation

        //Preparing the expected result

        //Test start

        adoptedDogsService.delete(adoptedDogs);
        verify(adoptedDogsRepository).delete(adoptedDogs);
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

    @Test
    void getAll_success() {

        //Input data preparation

        List<AdoptedDogs> adoptedDogsList = new ArrayList<>();
        adoptedDogsList.add(adoptedDogs);

        //Preparing the expected result

        when(adoptedDogsRepository.findAll()).thenReturn(adoptedDogsList);

        //Test start

        List<AdoptedDogs> adoptedDogsListActual = adoptedDogsService.getAll();

        assertEquals(adoptedDogsList, adoptedDogsListActual);
        verify(adoptedDogsRepository).findAll();
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

    @Test
    void getAllAdoptedDogsToMap_success() {

        //Input data preparation

        List<AdoptedDogs> adoptedDogsList = new ArrayList<>();
        adoptedDogsList.add(adoptedDogs);
        Map<Long, AdoptedDogs> adoptedDogsMapExpected = new HashMap<>();

        //Preparing the expected result

        when(adoptedDogsRepository.findAll()).thenReturn(adoptedDogsList);
        adoptedDogsMapExpected.put(adoptedDogs.getIdUser(), adoptedDogs);

        //Test start

        Map<Long, AdoptedDogs> adoptedDogsMapActual = adoptedDogsService.getAllAdoptedDogsToMap();

        assertEquals(adoptedDogsMapExpected, adoptedDogsMapActual);
        verify(adoptedDogsRepository).findAll();
        verifyNoMoreInteractions(adoptedDogsRepository);

    }

}