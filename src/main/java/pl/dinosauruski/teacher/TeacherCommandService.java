package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.dto.TeacherEditDTO;
import pl.dinosauruski.teacher.dto.TeacherRegistrationFormDTO;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TeacherCommandService {
    private final TeacherRepository teacherRepository;
    private final TeacherQueryService teacherQueryService;
    private final StudentQueryService studentQueryService;

    public void create(TeacherRegistrationFormDTO registrationForm) {
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

    public Teacher update(TeacherEditDTO teacherEditDTO) {
        Teacher teacher = teacherRepository.getOne(teacherEditDTO.getId());
        teacher.setName(teacherEditDTO.getName());
        teacher.setSurname(teacherEditDTO.getSurname());
        teacher.setNickname(teacherEditDTO.getNickname());
        teacherRepository.save(teacher);
        return teacher;
    }

    public void updateAfterAddingStudent(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    public void updateAfterDeletingStudent(Long teacherId, Long studentId) {
        Teacher teacher = teacherQueryService.getOneOrThrow(teacherId);
        Student student = studentQueryService.getOneOrThrow(studentId);
        Set<Student> students = teacher.getStudents();
        Set<Student> studentSet = students.stream()
                .filter(student1 -> !student1.equals(student))
                .collect(Collectors.toSet());
        teacher.setStudents(studentSet);
        teacherRepository.save(teacher);

    }

    private String hashPassword(String password) {
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
