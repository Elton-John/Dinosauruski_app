package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.slot.dto.FreeSlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;
    private final SlotQueryService slotQueryService;
    private final StudentQueryService studentQueryService;
    private final TeacherQueryService teacherQueryService;
    private final LessonQueryService lessonQueryService;
    private final WeekQueryService weekQueryService;
    private final LessonCommandService lessonCommandService;


    public Slot create(Long teacherId, FreeSlotDTO freeSlotDTO) {
        Teacher teacher = teacherQueryService.getOneOrThrow(teacherId);
        Slot slot = new Slot();
        slot.setDayOfWeek(freeSlotDTO.getDayOfWeek());
        slot.setTime(freeSlotDTO.getTime());
        slot.setDayOfWeek(freeSlotDTO.getDayOfWeek());
        slot.setTeacher(teacher);
        slot.setBooked(false);
        slot.setArchived(false);
        slotRepository.save(slot);
        return slot;
    }


    public void updateFreeSlot(FreeSlotDTO freeSlotDTO) {
        Slot slot = slotQueryService.getOneOrThrow(freeSlotDTO.getId());
        slot.setDayOfWeek(freeSlotDTO.getDayOfWeek());
        slot.setTime(freeSlotDTO.getTime());
        slotRepository.save(slot);
    }


    public void updateBookedSlot(BookedSlotDTO bookedSlotDTO, LocalDate date, Long teacherId) {
        LocalDate now = LocalDate.now();
        if (date.isBefore(now)) {
            date = now;
        }
        List<Optional<Lesson>> lessons = lessonQueryService.getAllGeneratedLessonsBySlotAfterDate(bookedSlotDTO.getId(), date);

        List<Week> weeks = lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Lesson::getWeek)
                .collect(Collectors.toList());

        lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(lesson -> !lesson.isCancelledByTeacher() || !lesson.isRebooked())
                .forEach(lesson -> {
                    // lessonCommandService.savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);
                    lessonCommandService.delete(lesson.getId(), teacherId);
                });
        Slot slot = slotQueryService.getOneOrThrow(bookedSlotDTO.getId());
        slot.setDayOfWeek(bookedSlotDTO.getDayOfWeek());
        slot.setTime(bookedSlotDTO.getTime());
        Slot savedSlot = slotRepository.save(slot);

        if (lessons.size() > 0) {
            lessonCommandService.generateLessonsBySlotForWeeks(savedSlot.getId(), weeks, teacherId);
        }
    }


    public void deleteFreeSlot(Long id) {
        //slotRepository.deleteById(id);
        slotQueryService.getOneOrThrow(id).setArchived(true);
    }


    public void deleteBookedSlot(Long slotId, LocalDate date, Long teacherId) {
        LocalDate now = LocalDate.now();
        if (date.isBefore(now)) {
            date = now;
        }
        List<Optional<Lesson>> lessons = lessonQueryService.getAllGeneratedLessonsBySlotAfterDate(slotId, date);
        lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(lesson -> !lesson.isCancelledByTeacher() || !lesson.isRebooked())
                .forEach(lesson -> {
                    lessonCommandService.delete(lesson.getId(), teacherId);
                });
        slotQueryService.getOneOrThrow(slotId).setArchived(true);

    }


    public void makeSlotFree(Long slotId, Long studentId, LocalDate date) {
        Slot slot = slotQueryService.getOneOrThrow(slotId);
        boolean hasLessons = lessonQueryService.checkSlotHasGeneratedLessons(slotId);
        if (hasLessons) {
            List<Optional<Lesson>> allLessons = lessonQueryService.getAllGeneratedLessonsBySlotAfterDate(slotId, date);

            allLessons.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(lesson -> {
                        lessonCommandService.delete(lesson.getId(), slot.getTeacher().getId());
                    });
            slot.setArchived(true);
            FreeSlotDTO newSlot = new FreeSlotDTO();
            newSlot.setDayOfWeek(slot.getDayOfWeek());
            newSlot.setTime(slot.getTime());
            create(slot.getTeacher().getId(), newSlot);
        } else {
            deleteStudentReference(slot);
            slot.setBooked(false);
            slotRepository.save(slot);
        }

    }


    private void deleteStudentReference(Slot slot) {
        slot.setRegularStudent(null);

    }


    public void makeSlotBooked(Long slotId, Long studentId, LocalDate date) {
        Slot slot = slotQueryService.getOneOrThrow(slotId);
        slot.setBooked(true);
        slot.setRegularStudent(studentQueryService.getOneOrThrow(studentId));
        Slot bookedSlot = slotRepository.save(slot);
        Long teacherId = slot.getTeacher().getId();
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksAfterDate(teacherId, date);
        lessonCommandService.generateLessonsBySlotForWeeks(bookedSlot.getId(), weeks, teacherId);
    }
}
