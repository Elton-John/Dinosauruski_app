package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.dinosauruski.models.Teacher;

import javax.validation.Valid;

@AllArgsConstructor
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;


    @GetMapping("/new")
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "teachers/new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "teachers/new";
        }
        if (teacher.getPassword().equals(teacher.getRepeatPassword())) {
            teacherService.create(teacher);
            /////////////////set cookies

            return "redirect:cockpit";
        } else {
            model.addAttribute("passwordError", "Hasła są rożne.");
            return "teachers/new";
        }
    }

    @GetMapping("/cockpit")
    public String displayCockpit(Teacher teacher, Model model) {
        model.addAttribute("name", "teacher");
        ////////////////////sprawdzamy cookie
        return "teachers/cockpit";
    }

}
