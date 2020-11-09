package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
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
    private BigDecimal priceForOneLesson;
    @ManyToMany(mappedBy = "students")
    private Set<Teacher> teachers = new HashSet<>();
    @OneToMany(mappedBy = "regularStudent", fetch = FetchType.EAGER)
    private List<Slot> slots = new ArrayList<>();
    private Boolean active;

}
