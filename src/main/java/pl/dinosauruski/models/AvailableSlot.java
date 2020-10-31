package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.models.DayName;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;

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
