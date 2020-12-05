package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import javax.persistence.*;
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
    private boolean booked;
    @ManyToOne(fetch = FetchType.EAGER)
    private Student regularStudent;
    private boolean archived;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        if (!id.equals(slot.id)) return false;
        if (dayOfWeek != slot.dayOfWeek) return false;
        if (!time.equals(slot.time)) return false;
        return teacher.equals(slot.teacher);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + dayOfWeek.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + teacher.hashCode();
        return result;
    }
}
