package pl.dinosauruski.availableHour;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.dayName.DayName;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Setter
@Getter
public class AvailableHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   // private DayName dayOfWeek;
    private LocalTime time;
}
