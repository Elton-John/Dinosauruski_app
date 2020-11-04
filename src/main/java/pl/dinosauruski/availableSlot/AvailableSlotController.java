package pl.dinosauruski.availableSlot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.dayName.DayNameService;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.DayName;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.StudentService;

import javax.swing.*;
import java.awt.print.Book;
import java.util.List;

///only CRUD
@Controller
@AllArgsConstructor
@RequestMapping("/slots")
public class AvailableSlotController {
    private final AvailableSlotService availableSlotService;
    private final DayNameService dayNameService;
    private final StudentService studentService;

    @GetMapping
    protected String index(Model model) {
        List<AvailableSlot> slots = availableSlotService.showAll();
        model.addAttribute("slots", slots);
        return "slots/index";
    }

    @GetMapping("/new")
    protected String newSlot(Model model) {
        AvailableSlot availableSlot = new AvailableSlot();
        model.addAttribute("slot", availableSlot);
        model.addAttribute("days");
        model.addAttribute("students");
        return "slots/new";
    }

    @PostMapping("/new")
    protected String create(AvailableSlot availableSlot) {
        availableSlotService.create(availableSlot);
        return "redirect:/slots";
    }

    @GetMapping("/{id}/edit")
    protected String editForm(@PathVariable Long id, Model model) {
        AvailableSlot slot = availableSlotService.getOneOrThrow(id);
        model.addAttribute("slot", slot);
                return "slots/edit";
    }

    @PatchMapping("/{id}/edit")
    protected String edit(AvailableSlot availableSlot, @PathVariable Long id) {
        availableSlotService.getOneOrThrow(id);
        availableSlotService.update(availableSlot);
        return "redirect:/slots";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        AvailableSlot slot = availableSlotService.getOneOrThrow(id);
        model.addAttribute("dayName", slot.getDayName());
        model.addAttribute("time", slot.getTime());
        model.addAttribute("id", id);
        return "slots/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        AvailableSlot slot = availableSlotService.getOneOrThrow(id);
        availableSlotService.delete(slot);
        return "redirect:/slots";
    }

    @ModelAttribute("days")
    protected List<DayName> days() {
        return dayNameService.showAllDays();
    }

    @ModelAttribute("students")
    protected List<Student> students() {
        return studentService.findAll();
    }
}
