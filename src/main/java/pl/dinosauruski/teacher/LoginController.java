package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import pl.dinosauruski.models.Teacher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@AllArgsConstructor
@Controller
@RequestMapping()
public class LoginController {
    private final TeacherService teacherService;

    @PostMapping("/login")
    public String login(@SessionAttribute(value = "loggedTeacher", required = false) Teacher teacher, @RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletResponse response,
                        Model model, HttpSession session) {
        if (teacherService.checkLogin(email, password) == null) {
            return "redirect:/";
        } else {
            Teacher loggedTeacher = teacherService.checkLogin(email, password);
            Cookie loginCookie = new Cookie("login", "yes");
            Cookie idCookie = new Cookie("id", loggedTeacher.getId().toString());
            loginCookie.setPath("/teacher");
            idCookie.setPath("/teacher");
            response.addCookie(loginCookie);
            response.addCookie(idCookie);
            model.addAttribute("teacher", loggedTeacher);
            session.setAttribute("loggedTeacher", loggedTeacher);

//
            return "teachers/cockpit";
        }
    }
}
