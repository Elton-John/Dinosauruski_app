package pl.dinosauruski.teacher.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.teacher.TeacherService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String loginPage(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher) {
        if (loggedTeacher != null) {
            return "redirect:/teacher/cockpit";
        }
        return "teachers/login";
    }

    @PostMapping
    public String login(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                        @ModelAttribute("teacherLoginForm") @Valid TeacherLoginFormDTO form, BindingResult result,
                        HttpSession session, Model model) {
        if (loggedTeacher != null) {
            return "redirect:/teacher/cockpit";
        }
        if (result.hasErrors()) {
            return "teachers/login";
        }
        boolean validCredentials = loginService.validate(form.getEmail(), form.getPassword());
        if (!validCredentials) {
            result.rejectValue("email", "errors.invalid", "Login i/lub hasło są niepoprawne");
            return "teachers/login";
        }
        TeacherDTO teacherDTO = loginService.login(form.getEmail());
        session.setAttribute("loggedTeacher", teacherDTO);
//        Teacher teacher = teacherService.getOneOrThrow(teacherDTO.getId());
//        model.addAttribute("teacher", teacher);
        return "redirect:/teacher/cockpit";
    }
}