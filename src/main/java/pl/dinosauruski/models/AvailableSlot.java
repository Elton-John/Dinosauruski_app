package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
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
    //@Column(nullable = false)
    @ManyToOne
    private Teacher teacher;
    private boolean isOnceFree;
    private LocalDate localDate;
    private boolean isBooked;
    @ManyToOne
    private Student regularStudent;

    public boolean isOnceFree() {
        return isOnceFree;
    }

    public void setOnceFree(boolean onceFree) {
        isOnceFree = onceFree;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
