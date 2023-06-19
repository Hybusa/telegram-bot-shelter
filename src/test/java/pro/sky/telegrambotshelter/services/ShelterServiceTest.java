package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.repository.ShelterRepository;
import pro.sky.telegrambotshelter.service.ShelterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {ShelterService.class})
@ExtendWith(SpringExtension.class)
public class ShelterServiceTest {

    @MockBean
    private ShelterRepository shelterRepository;

    private final Shelter shelter = new Shelter(111, "cats", "q", "p", "w", "q", "p", "w", "q", "p", "w", "q", "p", "w", "q", "p", "w", "q", "p");

    private ShelterService shelterService;

    @BeforeEach
    public void initEach() {
        shelterService = new ShelterService(shelterRepository);
    }

    @Test
    void getVolunteerChatId() {

        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        Long actual = shelterService.getVolunteerChatId(shelter.getShelterType());
        assertEquals(shelter.getVolunteerChatId(), actual);
    }

    @Test
    void getMeetingRecommendation() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getMeetingRecommendation(shelter.getShelterType());
        assertEquals(shelter.getMeetingRecommendations(), actual);
    }

    @Test
    void getHowToGetPet() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getHowToGetPet(shelter.getShelterType());
        assertEquals(shelter.getHowToGetPet(), actual);
    }

    @Test
    void getDocumentsList() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getDocumentsList(shelter.getShelterType());
        assertEquals(shelter.getDocumentsList(), actual);
    }

    @Test
    void getGeneralInfo() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getGeneralInfo(shelter.getShelterType());
        assertEquals(shelter.getGeneralInfo(), actual);
    }

    @Test
    void getPhoneNumber() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getPhoneNumber(shelter.getShelterType());
        assertEquals(shelter.getPhoneNumber(), actual);
    }

    @Test
    void getSchedule() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getSchedule(shelter.getShelterType());
        assertEquals(shelter.getSchedule(), actual);
    }

    @Test
    void getAddress() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getAddress(shelter.getShelterType());
        assertEquals(shelter.getAddress(), actual);
    }

    @Test
    void getHowToGet() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getHowToGet(shelter.getShelterType());
        assertEquals(shelter.getGetHowToGet(), actual);
    }

    @Test
    void getSecurityAndPass() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getSecurityAndPass(shelter.getShelterType());
        assertEquals(shelter.getSecurityAndPass(), actual);
    }

    @Test
    void getSafety() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getSafety(shelter.getShelterType());
        assertEquals(shelter.getSafety(), actual);
    }

    @Test
    void getTransportingRecommendations() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getTransportingRecommendations(shelter.getShelterType());
        assertEquals(shelter.getTransportingRecommendations(), actual);
    }

    @Test
    void getHomeRecommendationsYoung() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getHomeRecommendationsYoung(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsYoung(), actual);
    }

    @Test
    void getHomeRecommendationsOld() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getHomeRecommendationsOld(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsOld(), actual);
    }

    @Test
    void getCynologistRecommendations() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getCynologistRecommendations(shelter.getShelterType());
        assertEquals(shelter.getCynologistRecommendations(), actual);
    }

    @Test
    void getWhyWeCanDeny() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getWhyWeCanDeny(shelter.getShelterType());
        assertEquals(shelter.getWhyWeCanDeny(), actual);
    }

    @Test
    void getDisabilityRecommendations() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getDisabilityRecommendations(shelter.getShelterType());
        assertEquals(shelter.getDisabilityRecommendations(), actual);
    }

    @Test
    void getListOfCynologists() {
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        String actual = shelterService.getListOfCynologists(shelter.getShelterType());
        assertEquals(shelter.getListOfCynologists(), actual);
    }
}
