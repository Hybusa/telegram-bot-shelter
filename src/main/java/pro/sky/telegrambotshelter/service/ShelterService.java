package pro.sky.telegrambotshelter.service;

import liquibase.pro.packaged.S;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.repository.ShelterRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**сервис для работы с БД приютов*/
@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    /**метод для получения id волонтера*/
    public long getVolunteerChatId(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getVolunteerChatId();
    }

    /**метод для получения информации из БД о рекомендациях при первом знакомстве с животным*/
    public String getMeetingRecommendation(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getMeetingRecommendations();
    }

    /**метод для получения информации из БД о том, как взять питомца*/
    public String getHowToGetPet(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHowToGetPet();
    }

    /**метод для получения информации из БД о документах*/
    public String getDocumentsList(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getDocumentsList();
    }

    /**метод для получения основной информации из БД о приюте*/
    public String getGeneralInfo(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getGeneralInfo();
    }

    /**метод для получения из БД номера телефона приюта*/
    public String getPhoneNumber(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getPhoneNumber();
    }

    /**метод для получения из БД расписания приюта*/
    public String getSchedule(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSchedule();
    }

    /**метод для получения из БД адреса приюта*/
    public String getAddress(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getAddress();
    }


    public String getHowToGet(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getGetHowToGet();
    }

    /**метод для получения из БД информации об охране и пропуске*/
    public String getSecurityAndPass(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSecurityAndPass();
    }

    /**метод для получения из БД информации о технике безопасности*/
    public String getSafety(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSafety();
    }

    /**метод для получения из БД рекомендаций о транспортировке животного*/
    public String getTransportingRecommendations(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getTransportingRecommendations();
    }

    /**метод для получения из БД рекомендаций по обустройству дома для щенка/котенка*/
    public String getHomeRecommendationsYoung(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHomeRecommendationsYoung();
    }

    /**метод для получения из БД рекомендаций по обустройству дома для взрослого животного*/
    public String getHomeRecommendationsOld(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHomeRecommendationsOld();
    }

    /**метод для получения из БД рекомендаций кинолога*/
    public String getCynologistRecommendations(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getCynologistRecommendations();
    }

    /**метод для получения из БД списка причин для отказа*/
    public String getWhyWeCanDeny(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getWhyWeCanDeny();
    }

    /**
     * получение всех приютов в мапу (Id чат волонтера - приют)
     * */
    public Map<Long, Shelter> getAllSheltersToMap() {

        Map<Long, Shelter> shelterMap = new HashMap<>();
        List<Shelter> shelterList = shelterRepository.findAll();

        for (Shelter shelter: shelterList) {
            shelterMap.put(shelter.getVolunteerChatId(), shelter);
        }

        return shelterMap;

    }


}
