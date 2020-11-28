package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.slot.SlotCommandService;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.TeacherCommandService;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class StudentCommandService {
    private StudentRepository studentRepository;
    private StudentQueryService studentQueryService;
    private TeacherQueryService teacherQueryService;
    private TeacherCommandService teacherCommandService;
    private SlotCommandService slotCommandService;
    private SlotQueryService slotQueryService;


    public void update(StudentDTO studentDTO) {
        Student student = studentQueryService.getOneOrThrow(studentDTO.getId());
        student.setName(studentDTO.getName());
        student.setSurname(studentDTO.getSurname());
        student.setEmail(studentDTO.getEmail());
        //student.setPriceForOneLesson(studentDTO.getPriceForOneLesson());
        student.setOverpayment(studentDTO.getOverpayment());
        studentRepository.save(student);
    }

    public Student create(StudentDTO studentDTO, Long teacherId) {
        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setSurname(studentDTO.getSurname());
        student.setEmail(studentDTO.getEmail());
        student.setPriceForOneLesson(studentDTO.getPriceForOneLesson());
        student.setOverpayment(BigDecimal.valueOf(0));
        Set<Teacher> teachers = new HashSet<>();
        Teacher teacher = teacherQueryService.getOneOrThrow(teacherId);
        teachers.add(teacher);
        student.setTeachers(teachers);
        student.setActive(false);
        Student savedStudent = studentRepository.save(student);
        teacher.getStudents().add(student);
        teacherCommandService.updateAfterAddingStudent(teacher);
        return savedStudent;
    }

    public void delete(Long teacherId, Long studentId) {
        teacherCommandService.updateAfterDeletingStudent(teacherId, studentId);
        cancelBookedSlots(teacherId, studentId);
        studentRepository.deleteById(studentId);
    }

    private void cancelBookedSlots(Long teacherId, Long studentId) {
        List<Slot> slots = slotQueryService.getSlots(teacherId, studentId);
        slots.forEach(slotCommandService::makeSlotFree);

    }

}
