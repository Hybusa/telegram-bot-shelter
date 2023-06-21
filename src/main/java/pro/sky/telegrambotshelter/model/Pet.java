package pro.sky.telegrambotshelter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String shortInfo;
    private int rejections;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "shelters_id")
    private Shelter shelter;

    public Pet() {
    }

    public Pet(String name,
               int age,
               String shortInfo,
               User user,
               Shelter shelter) {
        this.name = name;
        this.age = age;
        this.shortInfo = shortInfo;
        this.rejections = 0;
        this.user = user;
        this.shelter = shelter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public int getRejections() {
        return rejections;
    }

    public void setRejections(int rejections) {
        this.rejections = rejections;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return age == pet.age
                && rejections == pet.rejections
                && Objects.equals(name, pet.name)
                && Objects.equals(shortInfo, pet.shortInfo)
                && Objects.equals(user, pet.user)
                && Objects.equals(shelter, pet.shelter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age,
                shortInfo,
                rejections,
                user,
                shelter);
    }
}
