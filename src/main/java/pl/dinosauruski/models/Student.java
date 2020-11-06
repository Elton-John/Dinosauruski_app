package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Teacher> teachers = new HashSet<>();
    @OneToMany(mappedBy = "regularStudent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AvailableSlot> slots = new ArrayList<>();
    private Boolean active;

    public String getFullName() {
        return this.name + " " + this.surname;
    }


}
