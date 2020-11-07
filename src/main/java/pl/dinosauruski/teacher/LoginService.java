package pl.dinosauruski.teacher;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Teacher;

@Service
public class LoginService {
    @Autowired
    private TeacherRepository teacherRepository;

    public boolean validate(String email, String password) {
        Teacher teacher = teacherRepository.findByEmail(email);
        if (BCrypt.checkpw(password, teacher.getPassword())) {
            return true;
        }
        return false;
    }

    public TeacherDTO login(String email) {
        Teacher teacher = teacherRepository.findByEmail(email);
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setEmail(teacher.getEmail());
        teacherDTO.setId(teacher.getId());
        return teacherDTO;
    }
}