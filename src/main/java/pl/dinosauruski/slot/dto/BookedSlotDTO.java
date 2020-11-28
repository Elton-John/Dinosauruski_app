package pl.dinosauruski.slot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor

public class BookedSlotDTO {
    private Long id;
    @NotNull(message = "Pole nie może być puste")
    private LocalTime time;
    private DAY_OF_WEEK dayOfWeek;

    public String getDayAndTime() {
        return this.dayOfWeek.getTranslation() + " " + this.time;
    }
}
