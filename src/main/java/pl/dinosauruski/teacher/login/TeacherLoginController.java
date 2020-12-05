package pl.dinosauruski.teacher.login;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.teacher.dto.TeacherLoginFormDTO;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
class TeacherLoginController {
    private final TeacherLoginService teacherLoginService;


    @GetMapping
    public String loginPage(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                            Model model) {
        model.addAttribute("teacherLoginForm", new TeacherLoginFormDTO());
        return loggedTeacher != null ? "redirect:/teacher/cockpit" : "teachers/login";
    }


    @PostMapping
    public String login(@Valid @ModelAttribute("teacherLoginForm") TeacherLoginFormDTO loginFormDTO,
                        BindingResult result,
                        HttpSession session,
                        Model model) {
        if (result.hasErrors()) {
            return "teachers/login";
        }
        boolean validCredentials = teacherLoginService.validate(loginFormDTO.getEmail(), loginFormDTO.getPassword());
        if (!validCredentials) {
            model.addAttribute("foul", true);
            return "teachers/login";
        }
        TeacherDTO teacherDTO = teacherLoginService.doLogin(loginFormDTO.getEmail());
        session.setAttribute("loggedTeacher", teacherDTO);
        return "redirect:/teacher/cockpit";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}