package pl.dinosauruski.models;

import lombok.Getter;
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
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Pole nie może być pustym.")
    private String name;
    @NotBlank(message = "Pole nie może być pustym.")
    private String surname;
    private String nickname;
    @NotBlank(message = "Pole nie może być pustym.")
    private String login;
    @NotBlank(message = "Pole nie może być pustym.")
    @Size(min = 3, message = "Minimum 8 znaków.")
    private String password;
    // @Transient
    @NotBlank(message = "Pole nie może być pustym.")
    private String repeatPassword;
    @NotBlank(message = "Pole nie może być pustym.")
    @Email
    private String email;
    @ManyToMany
    @JoinTable(name = "teachers_student", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students = new HashSet<>();
    @OneToMany(mappedBy = "teacher")
    private List<AvailableSlot> availableSlots;
    @OneToMany(mappedBy = "teacher")
    private Set<Payment> payments = new HashSet<>();

}
