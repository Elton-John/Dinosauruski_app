package pl.dinosauruski.teacher.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.teacher.TeacherService;

import javax.validation.Valid;

@AllArgsConstructor
@Controller
@RequestMapping("/new")
public class RegistrationController {

    private final TeacherService teacherService;

    @GetMapping
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "teachers/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Teacher teacher,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "teachers/new";
        }
        teacherService.create(teacher);
        return "redirect:/";
//        if (teacher.getPassword().equals(teacher.getRepeatPassword())) {
//            teacherService.create(teacher);
//            return "redirect:/";
//        } else {
//            model.addAttribute("passwordError", "Hasła są rożne.");
//            return "teachers/new";
//        }
    }
}
