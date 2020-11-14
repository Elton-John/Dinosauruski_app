package pl.dinosauruski.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.models.Week;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private Long id;
    private LocalDate date;
    private Slot slot;
    private Week week;


}
