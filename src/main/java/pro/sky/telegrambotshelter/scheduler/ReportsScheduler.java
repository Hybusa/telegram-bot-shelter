package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.AdoptedCats;
import pro.sky.telegrambotshelter.model.AdoptedDogs;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.AdoptedCatsService;
import pro.sky.telegrambotshelter.service.AdoptedDogsService;
import pro.sky.telegrambotshelter.service.PetService;
import pro.sky.telegrambotshelter.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportsScheduler {

    private final AdoptedCatsService adoptedCatsService;

    private final AdoptedDogsService adoptedDogsService;

    private final UserService userService;

    private final PetService petService;

    public ReportsScheduler(AdoptedCatsService adoptedCatsService,
                            AdoptedDogsService adoptedDogsService,
                            UserService userService,
                            PetService petService) {

        this.adoptedCatsService = adoptedCatsService;
        this.adoptedDogsService = adoptedDogsService;
        this.userService = userService;
        this.petService = petService;

    }

    /**
     * обработка списка сообщений по дате последнего отчета, для дальнейшей отправки ботом пользователю
     */
    public List<SendMessage> lastReportDateToUserScheduler() {

        List<SendMessage> sendMessage = new ArrayList<>();

        List<User> users = new ArrayList<>();

        List<AdoptedCats> adoptedCatsList = adoptedCatsService.getAll();
        List<AdoptedDogs> adoptedDogsList = adoptedDogsService.getAll();

        Map<Long, User> idUserMap = userService.getAllByIdNameMap();

        LocalDateTime localDateTime = LocalDateTime.now();

        adoptedCatsList
                .stream()
                .filter(lastReport -> lastReport.getLastReportDate() != null)
                .filter(lastReport -> lastReport.getLastReportDate().plusDays(1).isBefore(localDateTime))
                .forEach(adoptedCats ->  users.add(idUserMap.get(adoptedCats.getIdUser())));

        adoptedDogsList
                .stream()
                .filter(lastReport -> lastReport.getLastReportDate() != null)
                .filter(lastReport -> lastReport.getLastReportDate().plusDays(1).isBefore(localDateTime))
                .forEach(adoptedDogs -> users.add(idUserMap.get(adoptedDogs.getIdUser())));

        if (users.isEmpty()) {
            return null;
        } else {

            for (User user : users) {
                sendMessage.add(new SendMessage(user.getChatId(), "Dear user " + user.getName()
                        + ", more than a 1 day has passed since the last report, about your pet, please send a report!"));
            }

            return sendMessage;

        }

    }

    /**
     * обработка списка сообщений по дате последнего отчета, для дальнейшей отправки ботом волонтёру
     */
    public List<SendMessage> lastReportDateToVolunteerScheduler() {

        List<SendMessage> sendMessage = new ArrayList<>();

        List<AdoptedCats> adoptedCatsList = adoptedCatsService.getAll();
        List<AdoptedDogs> adoptedDogsList = adoptedDogsService.getAll();

        Map<User, Long> usersVolunteerId = new HashMap<>();
        Map<Long, User> idUserMap = userService.getAllByIdNameMap();
        Map<Long, Pet> petIdShelterMap = petService.getAllPetsMapIdPet();

        LocalDateTime localDateTime = LocalDateTime.now();

        adoptedCatsList
                .stream()
                .filter(lastReport -> lastReport.getLastReportDate() != null)
                .filter(lastReport -> lastReport.getLastReportDate().plusDays(2).isBefore(localDateTime))
                .forEach(adoptedCats -> {

                    usersVolunteerId.put(idUserMap.get(adoptedCats.getIdUser()),
                            petIdShelterMap.get(adoptedCats.getIdPet()).getShelter().getVolunteerChatId());

                });

        adoptedDogsList
                .stream()
                .filter(lastReport -> lastReport.getLastReportDate() != null)
                .filter(lastReport -> lastReport.getLastReportDate().plusDays(2).isBefore(localDateTime))
                .forEach(adoptedDogs -> {

                    usersVolunteerId.put(idUserMap.get(adoptedDogs.getIdUser()),
                            petIdShelterMap.get(adoptedDogs.getIdPet()).getShelter().getVolunteerChatId());

                });

        if (usersVolunteerId.isEmpty()) {
            return null;
        } else {

            usersVolunteerId.forEach((k, v) -> sendMessage.add(new SendMessage(v, "Dear volunteer, user " + k.getName()
                    + ", more than a 2 days has passed since the last report, about pet, please do something")));

            return sendMessage;

        }

    }

    public List<SendMessage> endOfTrialPeriod() {

        List<SendMessage> sendMessage = new ArrayList<>();

        List<AdoptedCats> adoptedCatsList = adoptedCatsService.getAll();
        List<AdoptedDogs> adoptedDogsList = adoptedDogsService.getAll();
        List<User> usersWhoEndTrial = new ArrayList<>();

        Map<Long, User> idUserMap = userService.getAllByIdNameMap();

        LocalDateTime localDateTime = LocalDateTime.now();

        adoptedCatsList
                .stream()
                .filter(periodEnd -> periodEnd.getPeriodEnd() != null)
                .filter(periodEnd -> periodEnd.getPeriodEnd().isBefore(localDateTime))
                .forEach(adoptedCats ->  {

                    usersWhoEndTrial.add(idUserMap.get(adoptedCats.getIdUser()));
                    adoptedCatsService.delete(adoptedCats);

                });

        adoptedDogsList
                .stream()
                .filter(periodEnd -> periodEnd.getPeriodEnd() != null)
                .filter(periodEnd -> periodEnd.getPeriodEnd().isBefore(localDateTime))
                .forEach(adoptedDogs -> {

                    usersWhoEndTrial.add(idUserMap.get(adoptedDogs.getIdUser()));
                    adoptedDogsService.delete(adoptedDogs);

                });

        if (usersWhoEndTrial.isEmpty()) {
            return null;
        } else {

            usersWhoEndTrial.forEach(user -> sendMessage.add(new SendMessage(user.getChatId(), "Dear user "
                    + user.getName() + ", you are finished trial period, congratulation!")));

            return sendMessage;

        }

    }

}
