package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;
    private final SlotQueryService slotQueryService;
    private final StudentQueryService studentQueryService;
    private final TeacherQueryService teacherQueryService;
    private final LessonCommandService lessonCommandService;

    public void createNewRegularFreeSlot(TeacherDTO loggedTeacher, SlotDTO slotForm) {
        Teacher teacher = teacherQueryService.getOneOrThrow(loggedTeacher.getId());
        create(slotForm, teacher);

    }

    private Slot create(SlotDTO addSlotForm, Teacher teacher) {
        Slot slot = new Slot();
        slot.setDayOfWeek(addSlotForm.getDayOfWeek());
        slot.setTime(addSlotForm.getTime());
        slot.setDayOfWeek(addSlotForm.getDayOfWeek());
        slot.setTeacher(teacher);
        slot.setBooked(false);
        slot.setOnceFree(false);
        slotRepository.save(slot);
        return slot;
    }

    public void update(SlotDTO slotDTO) {
        Slot slot = slotQueryService.getOneOrThrow(slotDTO.getId());
        slot.setDayOfWeek(slotDTO.getDayOfWeek());
        slot.setTime(slotDTO.getTime());
        slotRepository.save(slot);
    }

    public void delete(Long id) {
        slotRepository.deleteById(id);
    }

//    public void createOnceFreeSlot(Slot slot) {
//        slot.setBooked(false);
//        slotRepository.save(slot);
//    }

    public void makeSlotFree(Slot slot) {
        deleteStudentReference(slot);
        slot.setBooked(false);
        slotRepository.save(slot);
        lessonCommandService.removeGeneratedLessons(slot);
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
