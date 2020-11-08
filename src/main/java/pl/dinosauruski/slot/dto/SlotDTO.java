package pl.dinosauruski.slot.dto;

import lombok.*;
import pl.dinosauruski.models.DayName;
import pl.dinosauruski.models.Teacher;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private Long id;
    private DayName dayName;
    private LocalTime time;
   // private Long teacherId;



}
