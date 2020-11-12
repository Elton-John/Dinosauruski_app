package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.*;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.week.WeekCommandService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LessonCommandService {
    private LessonRepository lessonRepository;
    private LessonQueryService lessonQueryService;
    private SlotQueryService slotQueryService;
    private WeekCommandService weekCommandService;
    private WeekQueryService weekQueryService;
    private StudentQueryService studentQueryService;

    public void create(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setDate(lessonDTO.getDate());
        lesson.setSlot(lessonDTO.getSlot());
        lesson.setStudent(lessonDTO.getStudent());
        lesson.setWeek(lessonDTO.getWeek());
        lesson.setCompleted(false);
        lesson.setCancelled(false);
        lesson.setLastMinuteCancelled(false);
        lesson.setTransferred(false);
        lesson.setArchived(false);
        lessonRepository.save(lesson);
    }

    public void updateCompletion(LessonCompletionDTO lessonCompletionDTO) {
        Lesson lesson = lessonQueryService.getOneOrThrow(lessonCompletionDTO.getId());
        lesson.setCompleted(lessonCompletionDTO.isCompleted());
        lesson.setCancelled(lessonCompletionDTO.isCancelled());
        lesson.setLastMinuteCancelled(lessonCompletionDTO.isLastMinuteCancelled());
        lesson.setTransferred(lessonCompletionDTO.isTransferred());
        // lesson.setArchived(false);
        lessonRepository.save(lesson);
    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }

    public void generateWeekLessonsForTeacher(int year, int week, Long teacherId) {
        List<Slot> slots = slotQueryService.showAllSlotsByTeacherId(teacherId);
        slots.forEach(slot -> {
            if (slot.getBooked()) {
                LessonDTO lessonDTO = new LessonDTO();
                lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(year, week, slot.getDayOfWeek().name()));
                lessonDTO.setSlot(slot);
                lessonDTO.setStudent(slot.getRegularStudent());
                lessonDTO.setWeek(weekQueryService.getOneOrThrow(year, week, teacherId));
                create(lessonDTO);
            }
        });
        weekCommandService.setGenerated(week, year, teacherId);
    }

    public void generateFutureLessonsForStudent(Slot slot, Long studentId) {
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(slot.getTeacher());
        generateAdditionalLessons(weeks, slot, studentId);

    }

    private void generateAdditionalLessons(List<Week> weeks, Slot slot, Long studentId) {
        weeks.forEach(week -> {
            LessonDTO lessonDTO = new LessonDTO();
            lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(
                    week.getYear(),
                    week.getNumberOfWeek(),
                    slot.getDayOfWeek().name()));
            lessonDTO.setSlot(slot);
            lessonDTO.setStudent(studentQueryService.getOneOrThrow(studentId));
            lessonDTO.setWeek(week);
            if (lessonDTO.getDate().isAfter(LocalDate.now())) {
                create(lessonDTO);
            }
        });
    }


    public void removeGeneratedLessons(Slot slot) {
        LocalDate today = LocalDate.now();
        Student student = slot.getRegularStudent();
        Teacher teacher = slot.getTeacher();
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(teacher);

        weeks.forEach(week -> {
            week.getLessons().stream()
                    .filter(lesson -> lesson.getSlot().getId().equals(slot.getId()))
                    .filter(lesson -> lesson.getStudent().getId().equals(slot.getRegularStudent().getId()))
                    .filter(lesson -> lesson.getDate().isAfter(today))
                    .forEach(lesson -> delete(lesson.getId()));
        });

    }

}
