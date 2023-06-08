package pro.sky.telegrambotshelter.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int volunteerChatId;

    private String shelterType;

    private String meetingRecommendations;

    private String howToGetPet;

    private String documentsList;

    private String generalInfo;

    private String phoneNumber;

    private String schedule;

    private String getHowToGet;

    private String address;

    private String securityAndPass;

    private String safety;

    private String transportingRecommendations;

    private String homeRecommendationsYoung;

    private String homeRecommendationsOld;

    private String cynologistRecommendations;

    private String whyWeCanDeny;


    public Shelter() {
    }

    public Shelter(int volunteerChatId,
                   String shelterType,
                   String meetingRecommendations,
                   String howToGetPet,
                   String documentsList,
                   String generalInfo,
                   String phoneNumber,
                   String schedule,
                   String address,
                   String getHowToGet,
                   String securityAndPass,
                   String safety,
                   String transportingRecommendations,
                   String homeRecommendationsYoung,
                   String homeRecommendationsOld,
                   String cynologistRecommendations,
                   String whyWeCanDeny) {
        this.volunteerChatId = volunteerChatId;
        this.shelterType = shelterType;
        this.meetingRecommendations = meetingRecommendations;
        this.howToGetPet = howToGetPet;
        this.documentsList = documentsList;
        this.generalInfo = generalInfo;
        this.phoneNumber = phoneNumber;
        this.schedule = schedule;
        this.address = address;
        this.getHowToGet = getHowToGet;
        this.securityAndPass = securityAndPass;
        this.safety = safety;
        this.transportingRecommendations = transportingRecommendations;
        this.homeRecommendationsYoung = homeRecommendationsYoung;
        this.homeRecommendationsOld = homeRecommendationsOld;
        this.cynologistRecommendations = cynologistRecommendations;
        this.whyWeCanDeny = whyWeCanDeny;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVolunteerChatId() {
        return volunteerChatId;
    }

    public void setVolunteerChatId(int volunteerChatId) {
        this.volunteerChatId = volunteerChatId;
    }

    public String getShelterType() {
        return shelterType;
    }

    public void setShelterType(String shelterType) {
        this.shelterType = shelterType;
    }

    public String getMeetingRecommendations() {
        return meetingRecommendations;
    }

    public void setMeetingRecommendations(String meetingRecommendations) {
        this.meetingRecommendations = meetingRecommendations;
    }

    public String getHowToGetPet() {
        return howToGetPet;
    }

    public void setHowToGetPet(String howToGetPet) {
        this.howToGetPet = howToGetPet;
    }

    public String getDocumentsList() {
        return documentsList;
    }

    public void setDocumentsList(String documentsList) {
        this.documentsList = documentsList;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGetHowToGet() {
        return getHowToGet;
    }

    public void setGetHowToGet(String getHowToGet) {
        this.getHowToGet = getHowToGet;
    }

    public String getSecurityAndPass() {
        return securityAndPass;
    }

    public void setSecurityAndPass(String securityAndPass) {
        this.securityAndPass = securityAndPass;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public String getTransportingRecommendations() {
        return transportingRecommendations;
    }

    public void setTransportingRecommendations(String transportingRecommendations) {
        this.transportingRecommendations = transportingRecommendations;
    }

    public String getHomeRecommendationsYoung() {
        return homeRecommendationsYoung;
    }

    public void setHomeRecommendationsYoung(String homeRecommendationsYoung) {
        this.homeRecommendationsYoung = homeRecommendationsYoung;
    }

    public String getHomeRecommendationsOld() {
        return homeRecommendationsOld;
    }

    public void setHomeRecommendationsOld(String homeRecommendationsOld) {
        this.homeRecommendationsOld = homeRecommendationsOld;
    }

    public String getCynologistRecommendations() {
        return cynologistRecommendations;
    }

    public void setCynologistRecommendations(String cynologistRecommendations) {
        this.cynologistRecommendations = cynologistRecommendations;
    }

    public String getWhyWeCanDeny() {
        return whyWeCanDeny;
    }

    public void setWhyWeCanDeny(String whyWeCanDeny) {
        this.whyWeCanDeny = whyWeCanDeny;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return volunteerChatId == shelter.volunteerChatId
                && Objects.equals(shelterType, shelter.shelterType)
                && Objects.equals(meetingRecommendations, shelter.meetingRecommendations)
                && Objects.equals(howToGetPet, shelter.howToGetPet)
                && Objects.equals(documentsList, shelter.documentsList)
                && Objects.equals(generalInfo, shelter.generalInfo)
                && Objects.equals(phoneNumber, shelter.phoneNumber)
                && Objects.equals(schedule, shelter.schedule)
                && Objects.equals(address, shelter.address)
                && Objects.equals(getHowToGet, shelter.getHowToGet)
                && Objects.equals(securityAndPass, shelter.securityAndPass)
                && Objects.equals(safety, shelter.safety)
                && Objects.equals(transportingRecommendations, shelter.transportingRecommendations)
                && Objects.equals(homeRecommendationsYoung, shelter.homeRecommendationsYoung)
                && Objects.equals(homeRecommendationsOld, shelter.homeRecommendationsOld)
                && Objects.equals(cynologistRecommendations, shelter.cynologistRecommendations)
                && Objects.equals(whyWeCanDeny, shelter.whyWeCanDeny);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volunteerChatId,
                shelterType,
                meetingRecommendations,
                howToGetPet,
                documentsList,
                generalInfo,
                phoneNumber,
                schedule,
                address,
                getHowToGet,
                securityAndPass,
                safety,
                transportingRecommendations,
                homeRecommendationsYoung,
                homeRecommendationsOld,
                cynologistRecommendations,
                whyWeCanDeny);
    }
}