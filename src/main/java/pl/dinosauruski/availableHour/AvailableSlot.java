package pl.dinosauruski.availableHour;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.dayName.DayName;
import pl.dinosauruski.student.Student;
import pl.dinosauruski.teacher.Teacher;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Setter
@Getter
public class AvailableSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private DayName dayName;
    private LocalTime time;
    @ManyToOne
    private Teacher teacher;
    @ManyToOne
    private Student regularStudent;
}
