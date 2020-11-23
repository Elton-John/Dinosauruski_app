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
        Teacher teacher = teacherQueryService.getOneByEmail(email);
        if (teacher == null) {
            return false;
        }

//        return BCrypt.checkpw(password, teacher.getPassword()));
        return password.equals(teacher.getPassword());
    }

    public TeacherDTO doLogin(String email) {
        Teacher teacher = teacherQueryService.getOneByEmail(email);
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setEmail(teacher.getEmail());
        teacherDTO.setId(teacher.getId());
        teacherDTO.setNickname(teacher.getNickname());
        return teacherDTO;
    }
}