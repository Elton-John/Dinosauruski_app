package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.dto.StudentDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class StudentQueryService {
    private final StudentRepository studentRepository;

    public Student getOneOrThrow(Long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Student> getAllByTeacherId(Long id) {
        return studentRepository.findAllByTeachersContainsTeacher(id);
    }

    public List<Student> getAllActiveStudentsByTeacherId(Long id) {
        List<Student> studentList = getAllByTeacherId(id);
        return studentList.stream()
                .filter(Student::getActive)
                .collect(Collectors.toList());
    }

    public StudentDTO getOneStudentDTOOrThrow(Long id) {
        return studentRepository.findOneStudentDtoById(id).orElseThrow(EntityNotFoundException::new);
    }


}
