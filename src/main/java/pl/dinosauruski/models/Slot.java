package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter
@Getter


public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private DAY_OF_WEEK dayOfWeek;
    private LocalTime time;
    @ManyToOne
    private Teacher teacher;
    private Boolean isOnceFree;
   // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    public String getDayAndTime() {
        return this.dayOfWeek.getTranslation() + " " + this.time;
    }


}
