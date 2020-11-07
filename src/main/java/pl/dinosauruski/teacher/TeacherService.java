package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import pl.dinosauruski.models.Teacher;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class TeacherService {

    private TeacherRepository teacherRepository;

    public Teacher getOneOrThrow(Long id) {
        return teacherRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<TeacherEditDTO> getOneDTOToEdit(Long id) {
        return teacherRepository.findEditableDataById(id);
    }

    public void create(Teacher teacher) {
        String hashedPassword = hashPassword(teacher.getPassword());
        teacher.setPassword(hashedPassword);
        teacherRepository.save(teacher);
    }

    public Teacher update(TeacherEditDTO teacherEditDTO) {
        Teacher teacher = teacherRepository.getOne(teacherEditDTO.getId());
        teacher.setName(teacherEditDTO.getName());
        teacher.setSurname(teacherEditDTO.getSurname());
        teacher.setNickname(teacherEditDTO.getNickname());
        teacherRepository.save(teacher);
        return teacher;
    }

    public void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    public void deleteCookie(HttpServletRequest request,
                             HttpServletResponse response,
                             String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
