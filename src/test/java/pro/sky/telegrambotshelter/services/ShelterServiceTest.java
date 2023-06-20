package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.repository.ShelterRepository;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {ShelterService.class})
@ExtendWith(SpringExtension.class)
public class ShelterServiceTest {

    @MockBean
    private ShelterRepository shelterRepository;
    private Shelter shelter;
    @Autowired
    private ShelterService shelterService;

    @BeforeEach
    public void initEach() {
        shelter = mock(Shelter.class);
        when(shelter.getShelterType()).thenReturn("cats");
        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);
    }

    @Test
    void getVolunteerChatId() {

        when(shelterRepository.findShelterByShelterType(shelter.getShelterType())).thenReturn(shelter);

        Long actual = shelterService.getVolunteerChatId(shelter.getShelterType());
        assertEquals(shelter.getVolunteerChatId(), actual);
    }

    @Test
    void getMeetingRecommendation() {

        String actual = shelterService.getMeetingRecommendation(shelter.getShelterType());
        assertEquals(shelter.getMeetingRecommendations(), actual);
    }

    @Test
    void getHowToGetPet() {

        String actual = shelterService.getHowToGetPet(shelter.getShelterType());
        assertEquals(shelter.getHowToGetPet(), actual);
    }

    @Test
    void getDocumentsList() {

        String actual = shelterService.getDocumentsList(shelter.getShelterType());
        assertEquals(shelter.getDocumentsList(), actual);
    }

    @Test
    void getGeneralInfo() {

        String actual = shelterService.getGeneralInfo(shelter.getShelterType());
        assertEquals(shelter.getGeneralInfo(), actual);
    }

    @Test
    void getPhoneNumber() {

        String actual = shelterService.getPhoneNumber(shelter.getShelterType());
        assertEquals(shelter.getPhoneNumber(), actual);
    }

    @Test
    void getSchedule() {

        String actual = shelterService.getSchedule(shelter.getShelterType());
        assertEquals(shelter.getSchedule(), actual);
    }

    @Test
    void getAddress() {

        String actual = shelterService.getAddress(shelter.getShelterType());
        assertEquals(shelter.getAddress(), actual);
    }

    @Test
    void getHowToGet() {

        String actual = shelterService.getHowToGet(shelter.getShelterType());
        assertEquals(shelter.getGetHowToGet(), actual);
    }

    @Test
    void getSecurityAndPass() {

        String actual = shelterService.getSecurityAndPass(shelter.getShelterType());
        assertEquals(shelter.getSecurityAndPass(), actual);
    }

    @Test
    void getSafety() {

        String actual = shelterService.getSafety(shelter.getShelterType());
        assertEquals(shelter.getSafety(), actual);
    }

    @Test
    void getTransportingRecommendations() {

        String actual = shelterService.getTransportingRecommendations(shelter.getShelterType());
        assertEquals(shelter.getTransportingRecommendations(), actual);
    }

    @Test
    void getHomeRecommendationsYoung() {

        String actual = shelterService.getHomeRecommendationsYoung(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsYoung(), actual);
    }

    @Test
    void getHomeRecommendationsOld() {

        String actual = shelterService.getHomeRecommendationsOld(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsOld(), actual);
    }

    @Test
    void getCynologistRecommendations() {

        String actual = shelterService.getCynologistRecommendations(shelter.getShelterType());
        assertEquals(shelter.getCynologistRecommendations(), actual);
    }

    @Test
    void getWhyWeCanDeny() {

        String actual = shelterService.getWhyWeCanDeny(shelter.getShelterType());
        assertEquals(shelter.getWhyWeCanDeny(), actual);
    }

    @Test
    void getDisabilityRecommendations() {

        String actual = shelterService.getDisabilityRecommendations(shelter.getShelterType());
        assertEquals(shelter.getDisabilityRecommendations(), actual);
    }

    @Test
    void getListOfCynologists() {

        String actual = shelterService.getDisabilityRecommendations(shelter.getShelterType());
        assertEquals(shelter.getListOfCynologists(), actual);
    }

    @Test
    void getShelterTypeByVolunteerId(){

        when(shelterRepository.findShelterByVolunteerChatId(shelter.getVolunteerChatId())).thenReturn(Optional.of(shelter));

        String actual = shelterService.getShelterTypeByVolunteerId(shelter.getVolunteerChatId());
        assertEquals(shelter.getShelterType(), actual);
    }

    @Test
    void getShelterTypeByVolunteerIdWithNotFoundException(){

        when(shelterRepository.findShelterByVolunteerChatId(shelter.getVolunteerChatId())).thenReturn(Optional.empty());
        String expectedMessage = "Such Volunteer Not Found!";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> shelterService.getShelterTypeByVolunteerId(shelter.getVolunteerChatId())
        );


        assertEquals(expectedMessage, exception.getMessage());
    }
}
