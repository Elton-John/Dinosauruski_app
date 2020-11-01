package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Teacher;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Transactional
@AllArgsConstructor

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public Teacher getOneOrThrow(Long id) {
        return teacherRepository.findById(id).orElseThrow(EntityNotFoundException::new);   // teacher not found
    }

    public void create(Teacher teacher) {
        String hashedPassword = hashPassword(teacher.getPassword());
        teacher.setPassword(hashedPassword);
        teacherRepository.save(teacher);
    }

    public void update(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Teacher checkLogin(String email, String password) {
        Teacher teacher = teacherRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new); // teacher not found
        if (BCrypt.checkpw(password, teacher.getPassword())) {
            return teacher;
        }
        return null;
    }

}
