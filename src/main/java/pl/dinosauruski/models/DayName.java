package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Setter
@Getter
public class DayName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private boolean isDayOff;
    private Integer displayOrder;
    @OneToMany(mappedBy = "dayName")
    private Set<Slot> slots;

    public boolean isDayOff() {
        return isDayOff;
    }

    public void setDayOff(boolean dayOff) {
        isDayOff = dayOff;
    }

    @Override
    public String toString() {
        return "DayName{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDayOff=" + isDayOff +
                ", displayOrder=" + displayOrder +
                '}';
    }


}
