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

@Transactional
@AllArgsConstructor
@Service
public class TeacherService {

    private TeacherRepository teacherRepository;

    public Teacher getOneOrThrow(Long id) {
        return teacherRepository.findById(id).orElseThrow(EntityNotFoundException::new);   // teacher not found
    }

    public void create(Teacher teacher) {
        String hashedPassword = hashPassword(teacher.getPassword());
        teacher.setPassword(hashedPassword);
        teacher.setRepeatPassword(hashedPassword);
        teacherRepository.save(teacher);
    }

    public Teacher update(Teacher teacher) {
        String hashedPassword = hashPassword(teacher.getPassword());
        teacher.setPassword(hashedPassword);
        teacher.setRepeatPassword(hashedPassword);
        teacherRepository.save(teacher);
        return getOneOrThrow(teacher.getId());
    }

    public void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    protected void deleteCookie(HttpServletRequest request,
                                HttpServletResponse response,
                                String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
