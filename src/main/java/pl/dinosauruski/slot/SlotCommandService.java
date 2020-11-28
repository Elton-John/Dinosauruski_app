package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.*;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.slot.dto.FreeSlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;
    private final SlotQueryService slotQueryService;
    private final StudentQueryService studentQueryService;
    private final TeacherQueryService teacherQueryService;
    private final LessonCommandService lessonCommandService;
    private final LessonQueryService lessonQueryService;


    public Slot create(TeacherDTO teacherDTO, FreeSlotDTO freeSlotDTO) {
        Teacher teacher = teacherQueryService.getOneOrThrow(teacherDTO.getId());
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
                    lessonCommandService.savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);
                    lessonCommandService.delete(lesson.getId());
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

        Set<Student> students = new HashSet<>();

        lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(lesson -> {
                    if (lesson.isRebooked()) {
                        Student student = lesson.getRebooking().getNotRegularStudent();
                        students.add(student);
                    } else {
                        Student student = lesson.getSlot().getRegularStudent();
                        students.add(student);
                    }
                });

        students.forEach(student -> lessonCommandService.updatePaymentForStudent(student.getId(), teacherId));

        lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(lesson -> !lesson.isCancelledByTeacher() || !lesson.isRebooked())
                .forEach(lesson -> {
                    lessonCommandService.savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);
                    lessonCommandService.delete(lesson.getId());
                });
        slotQueryService.getOneOrThrow(slotId).setArchived(true);

    }


    public void makeSlotFree(Slot slot) {
        lessonCommandService.removeGeneratedLessons(slot);
        deleteStudentReference(slot);
        slot.setBooked(false);
        slotRepository.save(slot);

    }

    private void deleteStudentReference(Slot slot) {
        slot.setRegularStudent(null);
    }

    public void makeSlotBooked(Long bookedSlotId, Long studentId) {
        Slot slot = slotQueryService.getOneOrThrow(bookedSlotId);
        slot.setBooked(true);
        slot.setRegularStudent(studentQueryService.getOneOrThrow(studentId));
        slotRepository.save(slot);
        lessonCommandService.generateFutureLessonsForStudent(slot, studentId);
    }


}
