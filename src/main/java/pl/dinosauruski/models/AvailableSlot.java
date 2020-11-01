package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

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
