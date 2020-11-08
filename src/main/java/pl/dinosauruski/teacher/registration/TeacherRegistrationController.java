package pl.dinosauruski.teacher.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.dinosauruski.teacher.TeacherCommandService;
import pl.dinosauruski.teacher.dto.TeacherRegistrationFormDTO;

import javax.validation.Valid;

@AllArgsConstructor
@Controller
@RequestMapping("/new")
 class TeacherRegistrationController {

    private final TeacherCommandService teacherCommandService;

    @GetMapping
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new TeacherRegistrationFormDTO());
        return "teachers/new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("teacher") TeacherRegistrationFormDTO registrationForm,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "teachers/new";
        }

        if (registrationForm.getPassword().equals(registrationForm.getRepeatPassword())) {
            teacherCommandService.create(registrationForm);
            return "redirect:/";
        } else {
            model.addAttribute("passwordError", "Hasła są rożne.");
            return "teachers/new";
        }
    }
}
