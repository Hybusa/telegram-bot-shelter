package pro.sky.telegrambotshelter.service;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
        when(shelter.getShelterType()).thenReturn("test");
        when(shelterRepository.findShelterByShelterType(anyString())).thenReturn(shelter);
    }

    @Test
    void getVolunteerChatId() {
        when(shelter.getVolunteerChatId()).thenReturn(22L);

        Long actual = shelterService.getVolunteerChatId(shelter.getShelterType());
        assertEquals(shelter.getVolunteerChatId(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getMeetingRecommendation() {
        when(shelter.getMeetingRecommendations()).thenReturn("Meeting Recommendation");

        String actual = shelterService.getMeetingRecommendation(shelter.getShelterType());
        assertEquals(shelter.getMeetingRecommendations(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getHowToGetPet() {
        when(shelter.getGetHowToGet()).thenReturn("How To Get Pet");

        String actual = shelterService.getHowToGetPet(shelter.getShelterType());
        assertEquals(shelter.getHowToGetPet(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getDocumentsList() {
        when(shelter.getDocumentsList()).thenReturn("Documents list");

        String actual = shelterService.getDocumentsList(shelter.getShelterType());
        assertEquals(shelter.getDocumentsList(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getGeneralInfo() {
        when(shelter.getDocumentsList()).thenReturn("General Info");

        String actual = shelterService.getGeneralInfo(shelter.getShelterType());
        assertEquals(shelter.getGeneralInfo(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getPhoneNumber() {
        when(shelter.getPhoneNumber()).thenReturn("Phone Number");

        String actual = shelterService.getPhoneNumber(shelter.getShelterType());
        assertEquals(shelter.getPhoneNumber(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getSchedule() {
        when(shelter.getSchedule()).thenReturn("Schedule");

        String actual = shelterService.getSchedule(shelter.getShelterType());
        assertEquals(shelter.getSchedule(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getAddress() {
        when(shelter.getAddress()).thenReturn("Address");

        String actual = shelterService.getAddress(shelter.getShelterType());
        assertEquals(shelter.getAddress(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getHowToGet() {
        when(shelter.getGetHowToGet()).thenReturn("How To Get");

        String actual = shelterService.getHowToGet(shelter.getShelterType());
        assertEquals(shelter.getGetHowToGet(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getSecurityAndPass() {
        when(shelter.getSecurityAndPass()).thenReturn("Security And Pass");

        String actual = shelterService.getSecurityAndPass(shelter.getShelterType());
        assertEquals(shelter.getSecurityAndPass(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getSafety() {
        when(shelter.getSafety()).thenReturn("Safety");

        String actual = shelterService.getSafety(shelter.getShelterType());
        assertEquals(shelter.getSafety(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getTransportingRecommendations() {
        when(shelter.getTransportingRecommendations()).thenReturn("Transporting Recommendations");

        String actual = shelterService.getTransportingRecommendations(shelter.getShelterType());
        assertEquals(shelter.getTransportingRecommendations(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getHomeRecommendationsYoung() {
        when(shelter.getHomeRecommendationsYoung()).thenReturn("Home Recommendations Young");

        String actual = shelterService.getHomeRecommendationsYoung(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsYoung(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getHomeRecommendationsOld() {
        when(shelter.getHomeRecommendationsOld()).thenReturn("Home Recommendations Old");

        String actual = shelterService.getHomeRecommendationsOld(shelter.getShelterType());
        assertEquals(shelter.getHomeRecommendationsOld(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getCynologistRecommendations() {
        when(shelter.getCynologistRecommendations()).thenReturn("Cynologist Recommendations");

        String actual = shelterService.getCynologistRecommendations(shelter.getShelterType());
        assertEquals(shelter.getCynologistRecommendations(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getWhyWeCanDeny() {
        when(shelter.getWhyWeCanDeny()).thenReturn("Why We Can Deny");

        String actual = shelterService.getWhyWeCanDeny(shelter.getShelterType());
        assertEquals(shelter.getWhyWeCanDeny(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getDisabilityRecommendations() {
        when(shelter.getDisabilityRecommendations()).thenReturn("Disability Recommendations");

        String actual = shelterService.getDisabilityRecommendations(shelter.getShelterType());
        assertEquals(shelter.getDisabilityRecommendations(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getListOfCynologists() {
        when(shelter.getListOfCynologists()).thenReturn("List Of Cynologistss");

        String actual = shelterService.getListOfCynologists(shelter.getShelterType());
        assertEquals(shelter.getListOfCynologists(), actual);
        verify(shelterRepository).findShelterByShelterType(anyString());
    }

    @Test
    void getShelterTypeByVolunteerId(){
        when(shelter.getVolunteerChatId()).thenReturn(22L);
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
