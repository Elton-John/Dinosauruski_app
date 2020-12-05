package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.slot.dto.FreeSlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("teacher/slots")
class SlotController {
    private final SlotCommandService slotCommandService;
    private final SlotQueryService slotQueryService;
    private final StudentQueryService studentQueryService;
    private final LessonQueryService lessonQueryService;


    @GetMapping
    String index(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                 Model model) {
        List<Slot> slots = slotQueryService.getAllSlotsByTeacherId(loggedTeacher.getId());
        model.addAttribute("slots", slots);
        return "slots/index";
    }


    @GetMapping("/new")
    String newSlot(Model model) {
        model.addAttribute("slot", new FreeSlotDTO());
        return "slots/new";
    }


    @PostMapping("/new")
    String create(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                  @Valid @ModelAttribute("slot") FreeSlotDTO freeSlotDto, BindingResult result) {
        if (result.hasErrors()) {
            return "slots/new";
        }
        slotCommandService.create(loggedTeacher.getId(), freeSlotDto);
        return "redirect:/teacher/slots";
    }


    @GetMapping("/free/edit/{id}")
    String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("slot", slotQueryService.getOneFreeSlotDtoOrThrow(id));
        model.addAttribute("days");
        return "slots/freeSlotEdit";
    }


    @PatchMapping("/free/edit")
    String edit(@Valid @ModelAttribute("slot") FreeSlotDTO freeSlotDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "slots/freeSlotEdit";
        }
        slotCommandService.updateFreeSlot(freeSlotDTO);
        return "redirect:/teacher/slots";
    }


    @GetMapping("/booked/edit/{id}")
    String editBookedSlotForm(@PathVariable Long id, Model model) {
        model.addAttribute("slot", slotQueryService.getOneBookedSlotDtoOrThrow(id));
        model.addAttribute("days");
        model.addAttribute("valid", true);
        return "slots/bookedSlotEdit";
    }


    @PatchMapping("/booked/edit")
    String editBookedSlot(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                          @ModelAttribute("slot") BookedSlotDTO bookedSlotDTO,
                          @RequestParam("date") String dateAsString,
                          Model model
    ) {
        boolean isDate = GenericValidator.isDate(dateAsString, "yyyy-MM-dd", true);
        if (!isDate) {
            model.addAttribute("slot", slotQueryService.getOneBookedSlotDtoOrThrow(bookedSlotDTO.getId()));
            model.addAttribute("days");
            model.addAttribute("valid", false);
            model.addAttribute("error", "Pole 'data' nie może być puste. Prawidłowy format:\n \"yyyy-MM-dd\"");
            return "slots/bookedSlotEdit";
        }

        LocalDate date = LocalDate.parse(dateAsString);
        if (lessonQueryService.generatedLessonsWereRebookedOrCancelByTeacherAfterDate(bookedSlotDTO.getId(), date)) {
            //List<Week> weeksToChangedManual = slotQueryService.getWeeksWhereGeneratedLessonsWereChanged(bookedSlotDTO, date);
            return "redirect:/teacher/slots";
        } else {
            slotCommandService.updateBookedSlot(bookedSlotDTO, date, loggedTeacher.getId());
            return "redirect:/teacher/slots";
        }

    }


    @GetMapping("/free/submit/{id}")
    String submitDeleting(@PathVariable Long id, Model model) {
        FreeSlotDTO freeSlotDTO = slotQueryService.getOneFreeSlotDtoOrThrow(id);
        model.addAttribute("slot", freeSlotDTO);
        return "slots/freeSlotSubmit";
    }


    @DeleteMapping("free/delete/{id}")
    String delete(@PathVariable Long id) {
        slotCommandService.deleteFreeSlot(id);
        return "redirect:/teacher/slots";
    }


    @GetMapping("/booked/submit/{id}")
    String submitDeletingBookedSlot(@PathVariable Long id, Model model) {
        BookedSlotDTO bookedSlotDTO = slotQueryService.getOneBookedSlotDtoOrThrow(id);
        model.addAttribute("slot", bookedSlotDTO);
        return "slots/bookedSlotSubmit";
    }


    @DeleteMapping("booked/delete/{id}")
    String deleteBookedSlot(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                            @PathVariable Long id, HttpServletRequest request) {
        LocalDate date = LocalDate.parse(request.getParameter("date"));
        slotCommandService.deleteBookedSlot(id, date, loggedTeacher.getId());
        return "redirect:/teacher/slots";
    }


    @GetMapping("/booking/{studentId}")
    String editSlot(@SessionAttribute(value = "loggedTeacher") TeacherDTO loggedTeacher,
                    @PathVariable Long studentId,
                    Model model,

                    HttpSession session) {
        Long teacherId = loggedTeacher.getId();
        List<Slot> slots = slotQueryService.getSlots(teacherId, studentId);
        List<FreeSlotDTO> freeSlots = slotQueryService.getAllFreeSlotDTOsByTeacher(teacherId);
        StudentDTO studentDTO = studentQueryService.getOneStudentDTOOrThrow(studentId);
        model.addAttribute("studentSlots", slots);
        model.addAttribute("freeSlots", freeSlots);
        model.addAttribute("student", studentDTO);
        session.setAttribute("student", studentDTO);
        return "slots/cancelBooking";
    }


    @PostMapping("/booking")
    String bookSlot(@SessionAttribute("student") StudentDTO studentDTO,
                    @RequestParam("date") String dateAsString,
                    @RequestParam("slotId") Long slotId) {


        LocalDate date = LocalDate.parse(dateAsString);
        slotCommandService.makeSlotBooked(slotId, studentDTO.getId(), date);
        return "redirect:/teacher/slots/booking/" + studentDTO.getId();
    }


    @DeleteMapping("/cancel/{id}")
    String cancelSlot(@SessionAttribute("student") StudentDTO studentDTO,
                      @PathVariable Long id,
                      HttpServletRequest request) {
        LocalDate date = LocalDate.parse(request.getParameter("date"));
        slotCommandService.makeSlotFree(id, studentDTO.getId(), date);
        return "redirect:/teacher/slots/booking/" + studentDTO.getId();
    }
}
