package pl.dinosauruski.rebooking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Rebooking;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.rebooking.dto.RebookingDTO;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RebookingCommandService {
    private RebookingRepository rebookingRepository;
    private LessonQueryService lessonQueryService;
    private RebookingQueryService rebookingQueryService;

    public void create(Long id, Student notRegularStudent) {
        Rebooking rebooking = new Rebooking();
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        rebooking.setLesson(lesson);
        rebooking.setNotRegularStudent(notRegularStudent);
        lesson.setRebooked(true);
        lesson.setRebooking(rebooking);
        lesson.setRequiredPayment(true);
        rebookingRepository.save(rebooking);
    }

    public void update(RebookingDTO rebookingDTO) {
        Rebooking rebooking = rebookingQueryService.getOneOrThrow(rebookingDTO.getId());
        rebooking.setNotRegularStudent(rebookingDTO.getNotRegularStudent());
        rebookingRepository.save(rebooking);
    }

    public void delete(Rebooking rebooking) {
        rebookingRepository.delete(rebooking);
    }
}
