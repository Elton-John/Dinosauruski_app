package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.teacher.dto.TeacherEditDTO;
import pl.dinosauruski.teacher.dto.TeacherRegistrationFormDTO;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TeacherCommandService {
    private final TeacherRepository teacherRepository;

  public   void create(TeacherRegistrationFormDTO registrationForm) {
        //String hashedPassword = hashPassword(registrationForm.getPassword());
        Teacher teacher = new Teacher();
        teacher.setName(registrationForm.getName());
        teacher.setSurname(registrationForm.getSurname());
        teacher.setNickname(registrationForm.getNickname());
        teacher.setEmail(registrationForm.getEmail());
        //      teacher.setPassword(hashedPassword);
        teacher.setPassword(registrationForm.getPassword());
        teacherRepository.save(teacher);
    }

    public  Teacher update(TeacherEditDTO teacherEditDTO) {
        Teacher teacher = teacherRepository.getOne(teacherEditDTO.getId());
        teacher.setName(teacherEditDTO.getName());
        teacher.setSurname(teacherEditDTO.getSurname());
        teacher.setNickname(teacherEditDTO.getNickname());
        teacherRepository.save(teacher);
        return teacher;
    }

    public   void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    private   String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    //    public void deleteCookie(HttpServletRequest request,
//                             HttpServletResponse response,
//                             String cookieName) {
//        Cookie cookie = WebUtils.getCookie(request, cookieName);
//        if (cookie != null) {
//            cookie.setMaxAge(0);
//            response.addCookie(cookie);
//        }
//    }
}
