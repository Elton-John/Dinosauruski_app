package pl.dinosauruski.slot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlotDTO {
    private Long id;
    private DAY_OF_WEEK dayOfWeek;
    private LocalTime time;
    private Long teacherId;
    private Boolean isBooked;
    private Long regularStudentId;
}
