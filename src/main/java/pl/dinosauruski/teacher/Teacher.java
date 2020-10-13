package pl.dinosauruski.teacher;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.availableHour.AvailableSlot;
import pl.dinosauruski.student.Student;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String nickname;
    private String login;
    private String password;
    private String email;
    @ManyToMany
    @JoinTable(name = "teachers_student", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students;
    @OneToMany(mappedBy = "teacher")
    private List<AvailableSlot> availableSlots;

}
