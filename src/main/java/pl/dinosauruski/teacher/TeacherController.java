package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import pl.dinosauruski.models.Teacher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.print.Book;

@AllArgsConstructor
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private TeacherService teacherService;

    @GetMapping("/new")
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "teachers/new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Teacher teacher,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "teachers/new";
        }
        if (teacher.getPassword().equals(teacher.getRepeatPassword())) {
            teacherService.create(teacher);
            return "redirect:/";
        } else {
            model.addAttribute("passwordError", "Hasła są rożne.");
            return "teachers/new";
        }
    }

    @PostMapping("/login")
    public String displayLoginForm(@RequestParam("email") String email, @RequestParam("password") String password, HttpServletResponse response) {
        if (teacherService.checkLogin(email, password) == null) {
            return "redirect:/";
        } else {
            Cookie loginCookie = new Cookie("login", "yes");
            loginCookie.setPath("/teacher");
            response.addCookie(loginCookie);
            return "teachers/cockpit";
        }
    }

    @GetMapping("/cockpit")
    public String displayCockpit(Teacher teacher, Model model, HttpServletRequest request) {
        model.addAttribute("name", "teacher");
        Cookie loginCookie = WebUtils.getCookie(request, "login");
        if (loginCookie == null) {
            return "redirect:/";
        }
        return "teachers/cockpit";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getOneOrThrow(id);
        model.addAttribute("teacher", teacher);
        return "teachers/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Teacher teacher) {
        teacherService.update(teacher);
        return "redirect:cockpit";
    }

}
