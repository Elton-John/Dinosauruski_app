package pl.dinosauruski.student;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.availableHour.AvailableHour;
import pl.dinosauruski.teacher.Teacher;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
  //  private List<Teacher> teachers;
   // private List<AvailableHour> fixedHours;

}
