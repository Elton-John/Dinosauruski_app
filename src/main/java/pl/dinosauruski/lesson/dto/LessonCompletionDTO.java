package pl.dinosauruski.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonCompletionDTO {
    private Long id;
    private boolean cancelledByTeacher;
    private boolean cancelledByStudent;
    private boolean lastMinuteCancelled;

}
