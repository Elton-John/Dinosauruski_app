package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.payment.PaymentQueryService;
import pl.dinosauruski.slot.SlotQueryService;
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
    private final SlotQueryService slotQueryService;
    private final LessonQueryService lessonQueryService;
    private final PaymentQueryService paymentQueryService;

    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                 Model model) {
        List<Student> students = studentQueryService.getAllByTeacherId(loggedTeacher.getId());
        model.addAttribute("students", students);
        return "teachers/students/index";
    }

    @GetMapping("/active")
    String showOnlyActive(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                          Model model) {
        List<Student> students = studentQueryService.
                findAllIfActiveStudentsByTeacherId(loggedTeacher.getId());
        model.addAttribute("students", students);
        model.addAttribute("onlyActive", true);
        return "teachers/students/index";
    }

    @GetMapping("/new")
    String newStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "teachers/students/new";
    }

    @PostMapping("/new")
    String create(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
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
    String profile(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                   @PathVariable Long id,
                   Model model) {
        Student student = studentQueryService.getOneOrThrow(id);
        Long teacherId = loggedTeacher.getId();
        model.addAttribute("student", student);
        model.addAttribute("slots", student.getSlots());
        model.addAttribute("countPaidThisMonth", lessonQueryService.countPaidLessonsByStudentThisMonth(teacherId, id));
        model.addAttribute("countPlannedThisMonth", lessonQueryService.countGeneratedLessonThisMonthByStudent(teacherId, id));
        model.addAttribute("plannedLessonThisMonth", lessonQueryService.getGeneratedLessonInMonthByStudent(teacherId, id));
        model.addAttribute("countNotPaidNextMonth", lessonQueryService.countNotPaidLessonsByStudentNextMonth(teacherId, id));
        model.addAttribute("notPaidLessonsNextMonth", lessonQueryService.getNotPaidLessonsUntilLastDayOfNextMonth(teacherId, id));
        model.addAttribute("overPayment", paymentQueryService.getOverPayment(id, teacherId));
        model.addAttribute("requiredPayment", lessonQueryService.countRequiredPaymentAfterAddingOverPayment(id, teacherId));
        return "teachers/students/profile";
    }

    @GetMapping("/edit/{id}")
    String editForm(@PathVariable Long id,
                    Model model) {
        model.addAttribute("student", studentQueryService.getOneStudentDTOOrThrow(id));
        return "teachers/students/edit";
    }

    @PatchMapping("/edit/{id}")
    String edit(@PathVariable Long id,
                @Valid @ModelAttribute("student") StudentDTO studentDTO,
                BindingResult result) {
        if (result.hasErrors()) {
            return "teachers/students/edit";
        }
        studentCommandService.update(studentDTO);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

    @GetMapping("/submit/{id}")
    String submitDeleting(@PathVariable Long id, Model model) {
        StudentDTO studentDTO = studentQueryService.getOneStudentDTOOrThrow(id);
        model.addAttribute("student", studentDTO);
        return "teachers/students/submit";
    }

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") Long studentId,
                  @SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher) {
        studentCommandService.delete(loggedTeacher.getId(), studentId);
        return "redirect:/teacher/students";
    }

}
