package pl.dinosauruski.student;

import org.springframework.stereotype.Service;
import pl.dinosauruski.availableSlot.AvailableSlotService;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.Student;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional

public class StudentManagementByTeacherService extends StudentService {
    private final AvailableSlotService availableSlotService;

    public StudentManagementByTeacherService(StudentRepository studentRepository, AvailableSlotService availableSlotService) {
        super(studentRepository);
        this.availableSlotService = availableSlotService;
    }

    @Override
    public void delete(Long id) {
        Student student = studentRepository.getOne(id);
        List<AvailableSlot> slots = student.getSlots();
        slots.forEach(availableSlotService::unBookedSlot);
        studentRepository.deleteById(id);
    }

    public List<Student> findAllIfActive() {
        return studentRepository.findAllIfActive();
    }
}
