package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class LessonPaymentDTO {
    private Long id;
    private DAY_OF_WEEK dayOfWeek;
    private LocalTime time;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Student studentWhoPays;
    private boolean isRebooked;
    private boolean requiredPayment;
    private boolean paid;
    private Week week;

}
