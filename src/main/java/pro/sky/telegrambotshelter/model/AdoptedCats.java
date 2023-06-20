package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="adopted_cats")
public class AdoptedCats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "id_pet")
    private Long idPet;

    @JoinColumn(name = "id_user")
    private Long idUser;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime lastReportDate;

    public AdoptedCats() {
    }

    public AdoptedCats(Long idPet,
                       Long idUser,
                       LocalDateTime periodStart,
                       LocalDateTime periodEnd,
                       LocalDateTime lastReportDate) {

        this.idPet = idPet;
        this.idUser = idUser;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.lastReportDate = lastReportDate;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPet() {
        return idPet;
    }

    public void setIdPet(Long idPet) {
        this.idPet = idPet;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDateTime getLastReportDate() {
        return lastReportDate;
    }

    public void setLastReportDate(LocalDateTime lastReportDate) {
        this.lastReportDate = lastReportDate;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdoptedCats that = (AdoptedCats) o;
        return Objects.equals(id, that.id)
                && Objects.equals(idPet, that.idPet)
                && Objects.equals(idUser, that.idUser)
                && Objects.equals(periodStart, that.periodStart)
                && Objects.equals(periodEnd, that.periodEnd)
                && Objects.equals(lastReportDate, that.lastReportDate);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idPet, idUser, periodStart, periodEnd, lastReportDate);
    }

}
