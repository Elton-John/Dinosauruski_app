package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    private Boolean isOnceFree;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Boolean isBooked;
    @ManyToOne(fetch = FetchType.EAGER)
    private Student regularStudent;

    public Boolean getOnceFree() {
        return isOnceFree;
    }

    public void setOnceFree(Boolean onceFree) {
        isOnceFree = onceFree;
    }

    public Boolean getBooked() {
        return isBooked;
    }

    public void setBooked(Boolean booked) {
        isBooked = booked;
    }
}
