package pl.dinosauruski.lesson;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.availableSlot.AvailableSlot;
import pl.dinosauruski.student.Student;
import pl.dinosauruski.teacher.Teacher;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @ManyToOne
    private AvailableSlot slot;
    @ManyToOne
    private Teacher teacher;
    @ManyToOne
    private Student student;
    private boolean completed;
    private boolean cancelled;
    private boolean lastMinuteCancelled;
    private boolean transferred;
    private boolean paid;
    private Double requiredPayment;
}
