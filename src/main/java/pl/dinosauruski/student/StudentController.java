package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.validation.Valid;
import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping("/teacher/students")
public class StudentController {
    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;

    @GetMapping
    public String index(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                        Model model) {
        List<Student> students = studentQueryService.getAllByTeacherId(loggedTeacher.getId());
        model.addAttribute("students", students);
        return "teachers/students/index";
    }

    @GetMapping("/active")
    public String showOnlyActive(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                                 Model model) {
        List<Student> students = studentQueryService.
                findAllIfActiveStudentsByTeacherId(loggedTeacher.getId());
        model.addAttribute("students", students);
        model.addAttribute("onlyActive", true);
        return "teachers/students/index";
    }

    @GetMapping("/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "teachers/students/new";
    }

    @PostMapping("/new")
    public String create(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                         @Valid @ModelAttribute("student") StudentDTO studentDTO,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "teachers/students/new";
        }
        Student student = studentCommandService.create(studentDTO, loggedTeacher.getId());
        model.addAttribute("student", student);
        return "redirect:/teacher/students";
    }

    @GetMapping("/{id}/profile")
    public String profile(@PathVariable Long id, Model model) {
        Student student = studentQueryService.getOneOrThrow(id);
        model.addAttribute("student", student);
        model.addAttribute("slots", student.getSlots());
        return "teachers/students/profile";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id,
                           Model model) {
        model.addAttribute("student", studentQueryService.getOneStudentDTOOrThrow(id));
        return "teachers/students/edit";
    }

    @PatchMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("student") StudentDTO studentDTO,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "teachers/students/edit";
        }
        studentCommandService.update(studentDTO);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        StudentDTO studentDTO = studentQueryService.getOneStudentDTOOrThrow(id);
        model.addAttribute("student", studentDTO);
        return "teachers/students/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long studentId,
                         @SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher) {
        studentCommandService.delete(loggedTeacher.getId(), studentId);
        return "redirect:/teacher/students";
    }

}
