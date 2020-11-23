package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.teacher.dto.TeacherEditDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TeacherQueryService {
    private final TeacherRepository teacherRepository;

    public Teacher getOneOrThrow(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public TeacherEditDTO getOneDTOToEdit(Long id) {
        return teacherRepository.findEditableDataById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Teacher getOneByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }
}
