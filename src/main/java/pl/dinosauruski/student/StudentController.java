package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.Student;

import java.awt.print.Book;
import java.util.List;
import java.util.Set;

///only CRUD
@Controller
@AllArgsConstructor
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public String index(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "students/index";
    }

    @GetMapping("/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new Student());
        return "students/new";
    }

    @PostMapping("/new")
    public String create(Student student) {
        studentService.create(student);
        return "redirect:/students";
    }

    @GetMapping("/{id}/profile")
    public String profile(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        model.addAttribute("slots", student.getSlots());
        return "students/profile";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        return "students/edit";
    }

    @PatchMapping("/{id}")
    public String edit(@PathVariable Long id, Student student) {
        studentService.getOneOrThrow(id);
        studentService.update(student);
        return "redirect:/students";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("id", id);
        return "students/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Student student) {
        studentService.getOneOrThrow(id);
        studentService.delete(student);
        return "redirect:/students";
    }

}
