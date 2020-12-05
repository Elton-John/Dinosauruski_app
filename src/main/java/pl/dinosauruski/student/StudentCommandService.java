package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.rebooking.RebookingCommandService;
import pl.dinosauruski.slot.SlotCommandService;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.TeacherCommandService;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private LessonQueryService lessonQueryService;
    private RebookingCommandService rebookingCommandService;


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

    public void suspend(Long teacherId, Long studentId, LocalDate date) {
        Student student = studentQueryService.getOneOrThrow(studentId);
        student.getSlots().forEach(slot -> slotCommandService.makeSlotFree(slot.getId(), studentId, date));
        List<Optional<Lesson>> optionalRebookingLessons = lessonQueryService.getAllRebookingLessonByStudentAndTeacherAfterDate(date, teacherId, studentId);
        optionalRebookingLessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(lesson -> {
                    rebookingCommandService.cancelBookingOnceFreeLesson(lesson.getId());
                });
        updateStudentStatus(studentId);
    }

    private void updateStudentStatus(Long studentId) {
        Student student = studentQueryService.getOneOrThrow(studentId);
        int count = slotQueryService.getAllSlotsByStudent(studentId);
        if (count == 0) {
            student.setActive(false);
        } else {
            student.setActive(true);
        }
    }

    public void activate(Long studentId) {
        Student student = studentQueryService.getOneOrThrow(studentId);
        student.setActive(true);
    }

}
