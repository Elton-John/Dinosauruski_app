package pl.dinosauruski.teacher.login;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TeacherLoginService {

    private final TeacherQueryService teacherQueryService;

    public boolean validate(String email, String password) {
        Teacher teacher = teacherQueryService.getOneByEmailOrThrow(email);
//        if (BCrypt.checkpw(password, teacher.getPassword())) {
//            return true;
//        }
        if (password.equals(teacher.getPassword())) {
            return true;
        }
        return false;
    }

    public TeacherDTO doLogin(String email) {
        Teacher teacher = teacherQueryService.getOneByEmailOrThrow(email);
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setEmail(teacher.getEmail());
        teacherDTO.setId(teacher.getId());
        return teacherDTO;
    }
}