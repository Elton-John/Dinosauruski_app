package pl.dinosauruski.lesson.dto;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.models.*;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class LessonViewDTO {
    private Long id;
    //  @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    //private boolean archived;
  //  private boolean paid;
    private boolean requiredPayment;
    // private BigDecimal requiredPayment;
//    @ManyToOne
//    private Payment payment;


    @Override
    public String toString() {
        return "LessonViewDTO{" +
                "id=" + id +
                ", date=" + date +
                ", dayOfWeek=" + dayOfWeek +
                ", student=" + student +
                ", time=" + time +
                '}';
    }
}
