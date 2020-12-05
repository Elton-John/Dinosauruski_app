package pl.dinosauruski.rebooking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Rebooking;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.rebooking.dto.RebookingDTO;
import pl.dinosauruski.student.StudentQueryService;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RebookingCommandService {
    private RebookingRepository rebookingRepository;
    private RebookingQueryService rebookingQueryService;
    private StudentQueryService studentQueryService;
    private LessonQueryService lessonQueryService;
    private LessonCommandService lessonCommandService;


    public void create(Long id, Long notRegularStudentId, Long teacherId) {
        Rebooking rebooking = new Rebooking();
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        rebooking.setLesson(lesson);
        rebooking.setNotRegularStudent(studentQueryService.getOneOrThrow(notRegularStudentId));
        lesson.setRebooked(true);
        lesson.setRebooking(rebooking);
        lesson.setRequiredPayment(true);
        rebookingRepository.save(rebooking);
        lessonCommandService.updatePaymentForStudent(notRegularStudentId, teacherId);
    }


    public void update(RebookingDTO rebookingDTO, Long teacherId) {
        Rebooking rebooking = rebookingQueryService.getOneOrThrow(rebookingDTO.getId());
        Student notRegularStudent = rebookingDTO.getNotRegularStudent();
        rebooking.setNotRegularStudent(notRegularStudent);
        lessonCommandService.updatePaymentForStudent(notRegularStudent.getId(), teacherId);
        rebookingRepository.save(rebooking);
    }


    public void cancelBookingOnceFreeLesson(Long id) {
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        if (lesson.isPaid()) {
            lessonCommandService.savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);
        }
        Long studentId = lesson.getRebooking().getNotRegularStudent().getId();
        Long teacherId = lesson.getSlot().getTeacher().getId();

        lesson.setRebooked(false);
        Rebooking rebooking = lesson.getRebooking();
        delete(rebooking);
        lesson.setRequiredPayment(false);
        lessonCommandService.updatePaymentForStudent(studentId, teacherId);
    }


    public void delete(Rebooking rebooking) {
        rebookingRepository.delete(rebooking);
    }
}
