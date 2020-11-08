package pl.dinosauruski.teacher.login;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.teacher.dto.TeacherLoginFormDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
class TeacherLoginController {

    private TeacherLoginService teacherLoginService;


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
        boolean validCredentials = teacherLoginService.validate(loginFormDTO.getEmail(), loginFormDTO.getPassword());
        if (!validCredentials) {
            //    result.rejectValue("email", "errors.invalid", "Login i/lub hasło są niepoprawne");
            return "teachers/login";
        }
        TeacherDTO teacherDTO = teacherLoginService.doLogin(loginFormDTO.getEmail());
        session.setAttribute("loggedTeacher", teacherDTO);
        return "redirect:/teacher/cockpit";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//        teacherService.deleteCookie(request, response, "login");
//        teacherService.deleteCookie(request, response, "id");
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}