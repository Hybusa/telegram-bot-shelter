package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.repository.AdoptedCatsRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = AdoptedCatsService.class)
@ExtendWith(SpringExtension.class)
class AdoptedCatsServiceTest {

    @MockBean
    private AdoptedCatsRepository adoptedCatsRepository;

    @Autowired
    private AdoptedCatsService adoptedCatsService;

    private AdoptedCats adoptedCats;

    private final Long idPet = 200L;

    private final Long idUser = 1L;

    private LocalDateTime localDateTime;

    @BeforeEach
    public void init() {

        localDateTime = LocalDateTime.now();
        adoptedCats = new AdoptedCats(idPet, idUser, localDateTime, localDateTime.plusDays(30), localDateTime);

    }

    @Test
    void save_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedCatsRepository.save(adoptedCats)).thenReturn(adoptedCats);

        //Test start

        adoptedCatsService.save(adoptedCats);
        verify(adoptedCatsRepository).save(adoptedCats);
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

    @Test
    void update_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedCatsRepository.save(adoptedCats)).thenReturn(adoptedCats);

        //Test start

        adoptedCatsService.update(adoptedCats);
        verify(adoptedCatsRepository).save(adoptedCats);
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

    @Test
    void updateLastReports_success() {

        //Input data preparation

        //Preparing the expected result

        when(adoptedCatsRepository.findByIdUser(idUser)).thenReturn(Optional.ofNullable(adoptedCats));
        when(adoptedCatsRepository.save(adoptedCats)).thenReturn(adoptedCats);

        //Test start

        adoptedCatsService.updateLastReports(idUser, localDateTime);
        verify(adoptedCatsRepository).findByIdUser(idUser);
        verify(adoptedCatsRepository).save(adoptedCats);
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

    @Test
    void delete_success() {

        //Input data preparation

        //Preparing the expected result

        //Test start

        adoptedCatsService.delete(adoptedCats);
        verify(adoptedCatsRepository).delete(adoptedCats);
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

    @Test
    void getAll_success() {

        //Input data preparation

        List<AdoptedCats> adoptedCatsList = new ArrayList<>();
        adoptedCatsList.add(adoptedCats);

        //Preparing the expected result

        when(adoptedCatsRepository.findAll()).thenReturn(adoptedCatsList);

        //Test start

        List<AdoptedCats> adoptedCatsListActual = adoptedCatsService.getAll();

        assertEquals(adoptedCatsList, adoptedCatsListActual);
        verify(adoptedCatsRepository).findAll();
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

    @Test
    void getAllAdoptedCatsToMap_success() {

        //Input data preparation

        List<AdoptedCats> adoptedCatsList = new ArrayList<>();
        adoptedCatsList.add(adoptedCats);
        Map<Long, AdoptedCats> adoptedCatsMapExpected = new HashMap<>();

        //Preparing the expected result

        when(adoptedCatsRepository.findAll()).thenReturn(adoptedCatsList);
        adoptedCatsMapExpected.put(adoptedCats.getIdUser(), adoptedCats);

        //Test start

        Map<Long, AdoptedCats> adoptedCatsMapActual = adoptedCatsService.getAllAdoptedCatsToMap();

        assertEquals(adoptedCatsMapExpected, adoptedCatsMapActual);
        verify(adoptedCatsRepository).findAll();
        verifyNoMoreInteractions(adoptedCatsRepository);

    }

}