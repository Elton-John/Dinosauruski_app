package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Student;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class StudentService {
    protected final StudentRepository studentRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student getOneOrThrow(Long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void create(Student student) {
        studentRepository.save(student);
    }

    public void update(Student student) {
        studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
