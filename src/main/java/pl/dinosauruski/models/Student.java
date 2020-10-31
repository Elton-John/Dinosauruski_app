package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String nickname;
    private String login;
    private String password;
    private String email;
    private Double priceForOneLesson;
    @ManyToMany(mappedBy = "students")
    private List<Teacher> teachers;
    @OneToMany(mappedBy = "regularStudent")
    private List<AvailableSlot> fixedHours;

}
