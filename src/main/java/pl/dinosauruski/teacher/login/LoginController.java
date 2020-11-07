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
    public String loginPage(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                            Model model) {
        if (loggedTeacher != null) {
            return "redirect:/teacher/cockpit";
        }
        model.addAttribute("teacherLoginForm", new TeacherLoginFormDTO());
        return "teachers/login";
    }

    @PostMapping
    public String login(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                        @Valid @ModelAttribute("teacherLoginForm") TeacherLoginFormDTO loginFormDTO,
                        BindingResult result,
                        HttpSession session,
                        Model model) {
        if (loggedTeacher != null) {
            return "redirect:/teacher/cockpit";
        }
        if (result.hasErrors()) {
            return "teachers/login";
        }
        boolean validCredentials = loginService.validate(loginFormDTO.getEmail(), loginFormDTO.getPassword());
        if (!validCredentials) {
            //    result.rejectValue("email", "errors.invalid", "Login i/lub hasło są niepoprawne");
            return "teachers/login";
        }
        TeacherDTO teacherDTO = loginService.login(loginFormDTO.getEmail());
        session.setAttribute("loggedTeacher", teacherDTO);
        return "redirect:/teacher/cockpit";
    }
}