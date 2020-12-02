package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class LessonsOfWeekDTO {
    private List<LocalDate> dates;
    private List<LessonsOfDayDTO> lessonsOfDayDTOS;


}
