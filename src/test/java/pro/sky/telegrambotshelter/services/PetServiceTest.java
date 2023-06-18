package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.repository.PetRepository;
import pro.sky.telegrambotshelter.service.PetService;

@ContextConfiguration(classes = {PetService.class})
@ExtendWith(SpringExtension.class)
public class PetServiceTest {

    @MockBean
    private PetRepository petRepository;


}
