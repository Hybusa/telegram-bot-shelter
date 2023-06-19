package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.repository.ShelterRepository;

/**
 * сервис для работы с БД приютов
 */
@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    /**
     * метод для получения id волонтера
     */
    public int getVolunteerChatId(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getVolunteerChatId();
    }

    /**
     * метод для получения информации из БД о рекомендациях при первом знакомстве с животным
     */
    public String getMeetingRecommendation(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getMeetingRecommendations();
    }

    /**
     * метод для получения информации из БД о том, как взять питомца
     */
    public String getHowToGetPet(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHowToGetPet();
    }

    /**
     * метод для получения информации из БД о документах
     */
    public String getDocumentsList(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getDocumentsList();
    }

    /**
     * метод для получения основной информации из БД о приюте
     */
    public String getGeneralInfo(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getGeneralInfo();
    }

    /**
     * метод для получения из БД номера телефона приюта
     */
    public String getPhoneNumber(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getPhoneNumber();
    }

    /**
     * метод для получения из БД расписания приюта
     */
    public String getSchedule(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSchedule();
    }

    /**
     * метод для получения из БД адреса приюта
     */
    public String getAddress(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getAddress();
    }


    public String getHowToGet(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getGetHowToGet();
    }

    /**
     * метод для получения из БД информации об охране и пропуске
     */
    public String getSecurityAndPass(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSecurityAndPass();
    }

    /**
     * метод для получения из БД информации о технике безопасности
     */
    public String getSafety(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getSafety();
    }

    /**
     * метод для получения из БД рекомендаций о транспортировке животного
     */
    public String getTransportingRecommendations(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getTransportingRecommendations();
    }

    /**
     * метод для получения из БД рекомендаций по обустройству дома для щенка/котенка
     */
    public String getHomeRecommendationsYoung(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHomeRecommendationsYoung();
    }

    /**
     * метод для получения из БД рекомендаций по обустройству дома для взрослого животного
     */
    public String getHomeRecommendationsOld(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getHomeRecommendationsOld();
    }

    /**
     * метод для получения из БД рекомендаций кинолога
     */
    public String getCynologistRecommendations(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getCynologistRecommendations();
    }

    /**
     * метод для получения из БД списка причин для отказа
     */
    public String getWhyWeCanDeny(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getWhyWeCanDeny();
    }

    /**
     * метод для получения из БД списка рекомендауий для животных с инвалидностью
     */
    public String getDisabilityRecommendations(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getDisabilityRecommendations();
    }

    /**
     * метод для получения из БД списка кинологов
     */
    public String getListOfCynologists(String shelterType) {
        return shelterRepository.findShelterByShelterType(shelterType).getListOfCynologists().replace(",", "\n");
    }

    public String getShelterTypeByVolunteerId(Long chatId) {
        Optional<Shelter> optionalShelter = shelterRepository.findShelterByVolunteerChatId(chatId);
        if(optionalShelter.isPresent())
            return optionalShelter.get().getShelterType();
        throw new NotFoundException("Such Volunteer Not Found!");
    }
}
