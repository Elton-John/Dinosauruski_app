package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.availableSlot.AvailableSlotService;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.Student;

import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping("/teacher/students")
public class StudentManagementByTeacherController {
    private final StudentManagementByTeacherService studentService;
    private final AvailableSlotService availableSlotService;

    @GetMapping
    public String index(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "teachers/students/index";
    }

    @GetMapping("/active")
    public String showOnlyActive(Model model) {
        List<Student> students = studentService.findAllIfActive();
        model.addAttribute("students", students);
        model.addAttribute("onlyActive", true);
        return "teachers/students/index";
    }

    @GetMapping("/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new Student());
        return "teachers/students/new";
    }

    @PostMapping("/new")
    public String create(Student student) {
        studentService.create(student);
        return "redirect:/teacher/students";
    }

    @GetMapping("/{id}/profile")
    public String profile(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        model.addAttribute("slots", student.getSlots());
        return "teachers/students/profile";
    }

    ///////edit 1/3
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        return "teachers/students/edit";
    }

    @PatchMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Student student) {
        studentService.getOneOrThrow(id);
        studentService.update(student);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    ///////edit 2/3
    @GetMapping("/edit/price/{id}")
    public String editPriceForm(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        return "teachers/students/editPrice";
    }

    ///////edit 3/3
    @GetMapping("/edit/slot/{id}")
    public String editSlotForm(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("student", student);
        model.addAttribute("freeSlots");
        model.addAttribute("slots", student.getSlots());
        return "teachers/students/editSlot";
    }

    @GetMapping("/{id}/delete/slot/{slotId}")
    public String unbookedSlot(@PathVariable Long id, @PathVariable Long slotId) {
        availableSlotService.unBookedSlot(availableSlotService.getOneOrThrow(slotId));
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    @PatchMapping("/{id}/add/slot/{slotId}")
    public String bookedSlot(@PathVariable Long id, @PathVariable Long slotId, Student student) {
        availableSlotService.bookedSlot(availableSlotService.getOneOrThrow(slotId), id);
        studentService.update(student);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    @PatchMapping("/edit/slot/{id}/{slotId}")
    public String editSlot(@PathVariable Long id, Student student, @PathVariable Long slotId) {
        studentService.getOneOrThrow(id);
        studentService.update(student);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        Student student = studentService.getOneOrThrow(id);
        model.addAttribute("fullName", student.getFullName());
        model.addAttribute("id", id);
        return "teachers/students/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Student student) {
        studentService.getOneOrThrow(id);
        studentService.delete(id);
        return "redirect:/teacher/students";
    }

    @ModelAttribute("freeSlots")
    public List<AvailableSlot> freeSlots() {
        return availableSlotService.getAllFreeSlots();
    }

}
