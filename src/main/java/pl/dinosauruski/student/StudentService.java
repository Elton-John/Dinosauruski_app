package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Student;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public Student getOneOrThrow(Long id){
       return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
