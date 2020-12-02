package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class LessonViewDTO {
    private Long id;
      @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Student student;
    private LocalTime time;
    private Week week;
    private boolean completed;
    private boolean cancelledByTeacher;
    private boolean cancelledByStudent;
    private boolean lastMinuteCancelled;
    private boolean rebooked;
      private boolean paid;
    private boolean requiredPayment;

}
