package pl.dinosauruski.slot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SlotInfoDTO {
    private Long id;
    private DAY_OF_WEEK dayOfWeek;
    private LocalTime time;
    private Student regularStudent;
}
