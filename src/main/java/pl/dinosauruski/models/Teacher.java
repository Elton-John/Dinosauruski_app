package pl.dinosauruski.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Pole nie możeeee być puste.")
    private String name;
    @NotBlank(message = "Pole nie możeee być puste.")
    private String surname;
    private String nickname;
    @NotBlank(message = "Pole nie możeeee być puste.")
    @Size(min = 3, message = "Minimum 8 znaków.")
    private String password;
    //    @Transient
//    @NotBlank(message = "Pole nie może być puste.")
//    private String repeatPassword;
    @NotBlank(message = "Pole nie może być puste.")
    @Email
    private String email;
    @ManyToMany
    @JoinTable(name = "teachers_students", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students = new HashSet<>();
    @OneToMany(mappedBy = "teacher")
    private List<AvailableSlot> availableSlots;
    @OneToMany(mappedBy = "teacher")
    private Set<Payment> payments = new HashSet<>();

}
