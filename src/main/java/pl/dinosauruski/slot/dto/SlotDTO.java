package pl.dinosauruski.slot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dinosauruski.models.DayName;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private Long id;
    private DayName dayName;
    private LocalTime time;

}
