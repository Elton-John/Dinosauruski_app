package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.slot.DAY_OF_WEEK;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
public class LessonPaymentDTO {
    private Long id;
    private DAY_OF_WEEK dayOfWeek;
    private LocalTime time;
    private LocalDate date;
    private Student studentWhoPays;
    private boolean isRebooked;
    private boolean requiredPayment;
   // private BigDecimal requiredPayment;
}
