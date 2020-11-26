package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("teacher/slots")
class SlotController {
    private final SlotCommandService slotCommandService;
    private final SlotQueryService slotQueryService;
    private final StudentQueryService studentQueryService;


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
        return "slots/new";
    }

    @PostMapping("/new")
    String create(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                  @Valid @ModelAttribute("slot") SlotDTO addSlotForm, BindingResult result) {
        if (result.hasErrors()){
            return "slots/new";
        }
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
    String submitDeleting(@PathVariable Long id, Model model) {
        SlotDTO slotDTO = slotQueryService.getOneSlotDtoOrThrow(id);
        model.addAttribute("slot", slotDTO);
        return "slots/submit";
    }

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable Long id) {
        slotCommandService.delete(id);
        return "redirect:/teacher/slots";
    }

    @GetMapping("/booking/{studentId}")
    String editSlot(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                    @PathVariable Long studentId,
                    Model model,
                    HttpSession session) {
        Long teacherId = loggedTeacher.getId();
        List<Slot> slots = slotQueryService.getSlots(teacherId, studentId);
        List<Slot> freeSlots = slotQueryService.getAllFreeSlotsByTeacher(teacherId);
        StudentDTO studentDTO = studentQueryService.getOneStudentDTOOrThrow(studentId);
        model.addAttribute("studentSlots", slots);
        model.addAttribute("freeSlots", freeSlots);
        model.addAttribute("student", studentDTO);
        session.setAttribute("student", studentDTO);
        return "slots/cancelBooking";
    }

    @PostMapping("/booking")
    String bookSlot(@SessionAttribute("student") StudentDTO studentDTO,

                    @RequestParam("bookedSlotId") Long bookedSlotId) {

        Long studentId = studentDTO.getId();
        slotCommandService.makeSlotBooked(bookedSlotId, studentId);
        return "redirect:/teacher/slots/booking/" + studentDTO.getId();
    }

    @DeleteMapping("/cancel/{id}")
    String cancelSlot(@SessionAttribute("student") StudentDTO studentDTO,
                      @PathVariable Long id,
                      Model model) {
        Slot slot = slotQueryService.getOneOrThrow(id);
        slotCommandService.makeSlotFree(slot);
        return "redirect:/teacher/slots/booking/" + studentDTO.getId();
    }

}
