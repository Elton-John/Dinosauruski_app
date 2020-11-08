package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.dayName.DayNameQueryService;
import pl.dinosauruski.models.DayName;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("teacher/slots")
class SlotController {
    private final SlotCommandService slotCommandService;

    private final DayNameQueryService dayNameQueryService;
    private final SlotQueryService slotQueryService;


    @GetMapping
    String index(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                 Model model) {
        List<Slot> slots = slotQueryService.showAllSlotsByTeacherId(loggedTeacher.getId());
        model.addAttribute("slots", slots);
        return "slots/index";
    }

    @GetMapping("/new")
    String newSlot(Model model) {
        model.addAttribute("slot", new SlotDTO());
        model.addAttribute("days");
        return "slots/new";
    }

    @PostMapping("/new")
    String create(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                  SlotDTO addSlotForm) {
        slotCommandService.createNewRegularFreeSlot(loggedTeacher, addSlotForm);
        return "redirect:/teacher/slots";
    }

    @GetMapping("/edit/{id}")
    String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("slot", slotQueryService.getOneSlotDtoOrThrow(id));
        model.addAttribute("days");
        return "slots/edit";
    }

    @PatchMapping("/edit")
    String edit(SlotDTO slotDTO) {
        slotCommandService.update(slotDTO);
        return "redirect:/teacher/slots";
    }

    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        SlotDTO slotDTO = slotQueryService.getOneSlotDtoOrThrow(id);
        model.addAttribute("slot", slotDTO);
        return "slots/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        slotCommandService.delete(id);
        return "redirect:/teacher/slots";
    }

    //
//    @ModelAttribute("students")
//    protected List<Student> students() {
//        return studentService.findAll();
//    }
    @ModelAttribute("days")
    private List<DayName> days() {
        return dayNameQueryService.showAllDays();
    }
}
