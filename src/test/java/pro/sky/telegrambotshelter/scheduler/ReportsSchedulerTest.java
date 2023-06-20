package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.service.AdoptedCatsService;
import pro.sky.telegrambotshelter.service.AdoptedDogsService;
import pro.sky.telegrambotshelter.service.PetService;
import pro.sky.telegrambotshelter.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = ReportsScheduler.class)
@ExtendWith(SpringExtension.class)
class ReportsSchedulerTest {

    @MockBean
    private AdoptedCatsService adoptedCatsService;

    @MockBean
    private AdoptedDogsService adoptedDogsService;

    @MockBean
    private UserService userService;

    @MockBean
    private PetService petService;

    @Autowired
    private ReportsScheduler reportsScheduler;

    private AdoptedCats adoptedCats;

    private AdoptedDogs adoptedDogs;

    private User userWithCat;
    private User userWithDog;

    private Pet petCat;
    private Pet petDog;

    private Shelter shelterCats;
    private Shelter shelterDogs;

    private final Long idPetCat = 1L;
    private final Long idShelterPetCat = 0L;
    private final Long idUserCat = 100L;
    private final String nameCat = "Барсик";
    private final String nameUserCat = "Антон";

    private final Long iPetDog = 2L;
    private final Long idShelterPetDog = 1L;
    private final Long idUserDog = 200L;
    private final String nameDog = "Тузик";
    private final String nameUserDog = "Ксения";

    private final Long volunteerChatId = 998877L;

    private final LocalDateTime localDateTime = LocalDateTime.now();


    @BeforeEach
    public void init() {

        shelterCats = new Shelter();
        shelterCats.setId(idShelterPetCat);
        shelterCats.setVolunteerChatId(volunteerChatId);
        shelterCats.setShelterType("cats");

        shelterDogs = new Shelter();
        shelterDogs.setId(idShelterPetDog);
        shelterDogs.setVolunteerChatId(volunteerChatId);
        shelterDogs.setShelterType("dogs");

        userWithCat = new User(nameUserCat, 5000L);
        userWithCat.setId(idUserCat);
        userWithCat.setShelter(shelterCats);

        userWithDog = new User(nameUserDog, 10_000L);
        userWithDog.setId(idUserDog);

        petCat = new Pet();
        petCat.setId(idPetCat);
        petCat.setName(nameCat);
        petCat.setShelter(shelterCats);

        petDog = new Pet();
        petDog.setId(iPetDog);
        petDog.setName(nameDog);
        petDog.setShelter(shelterDogs);

        adoptedCats = new AdoptedCats(idPetCat, idUserCat, localDateTime, localDateTime.plusDays(30), localDateTime);
        adoptedDogs = new AdoptedDogs(iPetDog, idUserDog, localDateTime, localDateTime.plusDays(30), localDateTime);

    }

    @Test
    void lastReportDateToUserScheduler_cat_success() {

        //Input data preparation

        List<AdoptedCats> adoptedCatsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();

        adoptedCats.setLastReportDate(localDateTime.minusDays(1));
        adoptedCatsList.add(adoptedCats);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        //Preparing the expected result

        Long expectedIdChat = userWithCat.getChatId();

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear user " + userWithCat.getName()
                + ", more than a 1 day has passed since the last report, about your pet, please send a report!";

        SendMessage expectedSendMessage = new SendMessage(userWithCat.getChatId(), expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(adoptedCatsList);
        when(adoptedDogsService.getAll()).thenReturn(new ArrayList<>());
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.lastReportDateToUserScheduler();
        String actualMessageText = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualMessageText);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedDogsService).getAll();
        verify(userService).getAllByIdNameMap();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);

    }

    @Test
    void lastReportDateToUserScheduler_dog_success() {

        //Input data preparation

        List<AdoptedDogs> adoptedDogsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();

        adoptedDogs.setLastReportDate(localDateTime.minusDays(1));
        adoptedDogsList.add(adoptedDogs);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        //Preparing the expected result

        Long expectedIdChat = userWithDog.getChatId();

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear user " + userWithDog.getName()
                + ", more than a 1 day has passed since the last report, about your pet, please send a report!";

        SendMessage expectedSendMessage = new SendMessage(userWithCat.getChatId(), expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(new ArrayList<>());
        when(adoptedDogsService.getAll()).thenReturn(adoptedDogsList);
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.lastReportDateToUserScheduler();
        String actualSendMessage = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualSendMessage);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedDogsService).getAll();
        verify(userService).getAllByIdNameMap();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);

    }


    @Test
    void lastReportDateToVolunteerScheduler_cat_success() {

        //Input data preparation

        List<AdoptedCats> adoptedCatsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();
        Map<Long, Pet> petIdShelterMap = new HashMap<>();

        adoptedCats.setLastReportDate(localDateTime.minusDays(2));
        adoptedCatsList.add(adoptedCats);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        petIdShelterMap.put(petCat.getId(), petCat);

        //Preparing the expected result

        Long expectedIdChat = volunteerChatId;

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear volunteer, user " + userWithCat.getName()
                + ", more than a 2 days has passed since the last report, about pet, please do something";

        SendMessage expectedSendMessage = new SendMessage(volunteerChatId, expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(adoptedCatsList);
        when(adoptedDogsService.getAll()).thenReturn(new ArrayList<>());
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);
        when(petService.getAllPetsMapIdPet()).thenReturn(petIdShelterMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.lastReportDateToVolunteerScheduler();
        String actualMessageText = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualMessageText);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedDogsService).getAll();
        verify(userService).getAllByIdNameMap();
        verify(petService).getAllPetsMapIdPet();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(petService);

    }

    @Test
    void lastReportDateToVolunteerScheduler_dog_success() {

        //Input data preparation

        List<AdoptedDogs> adoptedDogsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();
        Map<Long, Pet> petIdShelterMap = new HashMap<>();

        adoptedDogs.setLastReportDate(localDateTime.minusDays(2));
        adoptedDogsList.add(adoptedDogs);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        petIdShelterMap.put(petDog.getId(), petDog);

        //Preparing the expected result

        Long expectedIdChat = volunteerChatId;

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear volunteer, user " + userWithDog.getName()
                + ", more than a 2 days has passed since the last report, about pet, please do something";

        SendMessage expectedSendMessage = new SendMessage(volunteerChatId, expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(new ArrayList<>());
        when(adoptedDogsService.getAll()).thenReturn(adoptedDogsList);
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);
        when(petService.getAllPetsMapIdPet()).thenReturn(petIdShelterMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.lastReportDateToVolunteerScheduler();
        String actualMessageText = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualMessageText);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedDogsService).getAll();
        verify(userService).getAllByIdNameMap();
        verify(petService).getAllPetsMapIdPet();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(petService);

    }

    @Test
    void endOfTrialPeriod_cat_success() {

        //Input data preparation

        List<AdoptedCats> adoptedCatsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();

        adoptedCats.setPeriodEnd(localDateTime.minusDays(31));
        adoptedCatsList.add(adoptedCats);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        //Preparing the expected result

        Long expectedIdChat = userWithCat.getChatId();

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear user " + userWithCat.getName() +
                ", you are finished trial period, congratulation!";

        SendMessage expectedSendMessage = new SendMessage(expectedIdChat, expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(adoptedCatsList);
        when(adoptedDogsService.getAll()).thenReturn(new ArrayList<>());
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.endOfTrialPeriod();
        String actualMessageText = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualMessageText);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedCatsService).delete(adoptedCats);
        verify(adoptedDogsService).getAll();
        verify(userService).getAllByIdNameMap();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);

    }

    @Test
    void endOfTrialPeriod_dog_success() {

        //Input data preparation

        List<AdoptedDogs> adoptedDogsList = new ArrayList<>();

        Map<Long, User> idUserMap = new HashMap<>();

        adoptedDogs.setPeriodEnd(localDateTime.minusDays(31));
        adoptedDogsList.add(adoptedDogs);

        idUserMap.put(userWithCat.getId(), userWithCat);
        idUserMap.put(userWithDog.getId(), userWithDog);

        //Preparing the expected result

        Long expectedIdChat = userWithDog.getChatId();

        List<SendMessage> expectedSendMessagesList = new ArrayList<>();
        String expectedTextMessage = "Dear user " + userWithDog.getName() +
                ", you are finished trial period, congratulation!";

        SendMessage expectedSendMessage = new SendMessage(expectedIdChat, expectedTextMessage);
        expectedSendMessagesList.add(expectedSendMessage);

        when(adoptedCatsService.getAll()).thenReturn(new ArrayList<>());
        when(adoptedDogsService.getAll()).thenReturn(adoptedDogsList);
        when(userService.getAllByIdNameMap()).thenReturn(idUserMap);

        //Test start

        List<SendMessage> actualSendMessagesList = reportsScheduler.endOfTrialPeriod();
        String actualMessageText = (String) actualSendMessagesList.get(0).getParameters().get("text");
        Long actualIdChat = (Long) actualSendMessagesList.get(0).getParameters().get("chat_id");

        assertEquals(expectedTextMessage, actualMessageText);
        assertEquals(expectedSendMessagesList.size(), actualSendMessagesList.size());
        assertEquals(expectedIdChat, actualIdChat);

        verify(adoptedCatsService).getAll();
        verify(adoptedDogsService).getAll();
        verify(adoptedDogsService).delete(adoptedDogs);
        verify(userService).getAllByIdNameMap();

        verifyNoMoreInteractions(adoptedCatsService);
        verifyNoMoreInteractions(adoptedDogsService);
        verifyNoMoreInteractions(userService);

    }

}