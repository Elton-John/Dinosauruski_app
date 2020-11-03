package pl.dinosauruski.availableSlot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.dayName.DayNameService;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.StudentService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class AvailableSlotService {
    private final AvailableSlotRepository availableSlotRepository;
    private final StudentService studentService;
    private final DayNameService dayNameService;

    public List<AvailableSlot> showAll() {
        return availableSlotRepository.findAll();
    }

    public AvailableSlot getOneOrThrow(Long id) {
        return availableSlotRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void createRegularFreeSlot(AvailableSlot availableSlot) {
        availableSlot.setBooked(false);
        availableSlot.setOnceFree(false);
        availableSlotRepository.save(availableSlot);
        Integer dayNameId = availableSlot.getDayName().getId();
        dayNameService.markAsWorkDay(dayNameId);
    }

    public void createOnceFreeSlot(AvailableSlot availableSlot) {
        availableSlot.setBooked(false);
        availableSlotRepository.save(availableSlot);
    }

    public void bookedSlot(AvailableSlot availableSlot, Long studentId) {
        Student student = studentService.getOneOrThrow(studentId);
        availableSlot.setRegularStudent(student);
        availableSlot.setBooked(true);
        update(availableSlot);
    }

    public void unBookedSlot(AvailableSlot availableSlot) {
        availableSlotRepository.deleteStudentReference(availableSlot.getId());
        availableSlot.setBooked(false);
        update(availableSlot);
    }

    public void update(AvailableSlot availableSlot) {
        availableSlotRepository.save(availableSlot);
    }

    public void delete(AvailableSlot availableSlot) {
        availableSlotRepository.delete(availableSlot);
    }
}
