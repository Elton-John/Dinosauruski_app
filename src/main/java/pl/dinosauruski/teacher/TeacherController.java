package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.availableSlot.AvailableSlotService;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.Teacher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final AvailableSlotService availableSlotService;


    @GetMapping("/cockpit")
    public String displayCockpit(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                                 Model model,
                                 HttpServletRequest request) {
//        Cookie loginCookie = WebUtils.getCookie(request, "login");
//        Cookie idCookie = WebUtils.getCookie(request, "id");
//        if (loginCookie == null || idCookie == null) {
//            return "redirect:/";
//        }
//        Long id = Long.parseLong(idCookie.getValue());
        Long id = loggedTeacher.getId();
        Teacher teacher = teacherService.getOneOrThrow(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("freeSlots");
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
        teacherService.deleteCookie(request, response, "login");
        teacherService.deleteCookie(request, response, "id");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        teacherService.deleteCookie(request, response, "login");
        teacherService.deleteCookie(request, response, "id");
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }


    @ModelAttribute("freeSlots")
    public List<AvailableSlot> slots() {
        return availableSlotService.getAllFreeSlots();
    }

}
