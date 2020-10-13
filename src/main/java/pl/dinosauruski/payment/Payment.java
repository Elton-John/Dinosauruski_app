package pl.dinosauruski.payment;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.student.Student;
import pl.dinosauruski.teacher.Teacher;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Payment {
    @Id
    @Setter
    @Getter
    private Long id;
    private LocalDate date;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Teacher teacher;
    private Double sum;


}
