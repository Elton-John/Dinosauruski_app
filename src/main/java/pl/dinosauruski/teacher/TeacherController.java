package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
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
    public String displayLoginForm(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpServletResponse response,
                                   Model model) {
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
            return "teachers/cockpit";
        }
    }

    @GetMapping("/cockpit")
    public String displayCockpit(
            Model model,
            HttpServletRequest request) {
        Cookie loginCookie = WebUtils.getCookie(request, "login");
        Cookie idCookie = WebUtils.getCookie(request, "id");
        if (loginCookie == null || idCookie == null) {
            return "redirect:/";
        }
        Long id = Long.parseLong(idCookie.getValue());
        Teacher teacher = teacherService.getOneOrThrow(id);
        model.addAttribute("teacher", teacher);
        return "teachers/cockpit";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getOneOrThrow(id);
        model.addAttribute("teacher", teacher);
        return "teachers/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Teacher teacher,
                         Model model) {
        teacherService.getOneOrThrow(id);
        Teacher updatedTeacher = teacherService.update(teacher);
        model.addAttribute("teacher", updatedTeacher);
        return "redirect:cockpit";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getOneOrThrow(id);
        model.addAttribute("name", teacher.getName());
        model.addAttribute("id", id);
        return "/teachers/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        Teacher teacher = teacherService.getOneOrThrow(id);
        teacherService.delete(teacher);
        deleteCookie(request, response, "login");
        deleteCookie(request, response, "id");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, "login");
        deleteCookie(request, response, "id");
        return "redirect:/";
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
