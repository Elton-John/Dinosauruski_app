package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.dayName.DayNameCommandService;
import pl.dinosauruski.dayName.DayNameQueryService;
import pl.dinosauruski.models.DayName;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class SlotCommandService {
    private final SlotRepository slotRepository;
    private final SlotQueryService slotQueryService;
    private final DayNameQueryService dayNameQueryService;
    private final DayNameCommandService dayNameCommandService;
    private final TeacherQueryService teacherQueryService;

    public void createNewRegularFreeSlot(TeacherDTO loggedTeacher, SlotDTO slotForm) {
        Teacher teacher = teacherQueryService.getOneOrThrow(loggedTeacher.getId());
        Slot slot = create(slotForm, teacher);
        Integer dayNameId = slot.getDayName().getId();
        dayNameCommandService.markAsWorkDay(dayNameId);
    }

    private Slot create(SlotDTO addSlotForm, Teacher teacher) {
        Slot slot = new Slot();
        slot.setDayName(addSlotForm.getDayName());
        slot.setTime(addSlotForm.getTime());
        slot.setTeacher(teacher);
        slot.setBooked(false);
        slot.setOnceFree(false);
        slotRepository.save(slot);
        return slot;
    }

    public void update(SlotDTO slotDTO) {
        Slot slot = slotQueryService.getOneOrThrow(slotDTO.getId());
        slot.setDayName(slotDTO.getDayName());
        slot.setTime(slotDTO.getTime());
        slotRepository.save(slot);
        dayNameCommandService.markAsWorkDay(slot.getDayName().getId());
        refreshDayNameStatus();
    }

    public void delete(Long id) {
        slotRepository.deleteById(id);
        refreshDayNameStatus();
    }

//    public void createOnceFreeSlot(Slot slot) {
//        slot.setBooked(false);
//        slotRepository.save(slot);
//    }

    public void bookedSlot(Slot slot, Student student) {
        // Student student = studentService.getOneOrThrow(studentId);
        slot.setRegularStudent(student);
        slot.setBooked(true);
        updateBooked(slot);
    }

    public void unBookedSlot(Slot slot) {
        deleteStudentReference(slot);
        slot.setBooked(false);
        updateBooked(slot);
    }

    private void deleteStudentReference(Slot slot) {
        slot.setRegularStudent(null);
    }

    public void updateBooked(Slot slot) {
        slotRepository.save(slot);
    }

    private void refreshDayNameStatus() {
        Set<Integer> workDaysId = slotRepository.findAllDayNameId();
        for (int id = 1; id <= 7; id++) {
            DayName dayName = dayNameQueryService.getById(id);
            if (workDaysId.contains(id)) {
                dayName.setDayOff(false);
            } else {
                dayName.setDayOff(true);
            }
        }
    }
}
