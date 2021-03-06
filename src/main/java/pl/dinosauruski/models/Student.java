package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private List <Slot> slots = new ArrayList<>();
    private Boolean active;
    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private Set<Payment> payments = new HashSet<>();
    private BigDecimal overpayment;
    @OneToMany(mappedBy = "notRegularStudent", fetch = FetchType.EAGER)
    private Set<Rebooking> rebookings =  new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (!id.equals(student.id)) return false;
        if (!name.equals(student.name)) return false;
        if (!surname.equals(student.surname)) return false;
        return email.equals(student.email);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
