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
    private boolean completed;
    private boolean cancelled;
    private boolean lastMinuteCancelled;
    private boolean transferred;
   // private boolean archived;
}
