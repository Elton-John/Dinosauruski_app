package pl.dinosauruski.student;

import lombok.AllArgsConstructor;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.dto.StudentDTO;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping("/teacher/students")
public class StudentController {
    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;
    private final LessonQueryService lessonQueryService;


    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                 Model model) {
        List<Student> students = studentQueryService.getAllByTeacherId(loggedTeacher.getId());
        model.addAttribute("students", students);
        model.addAttribute("teacher", loggedTeacher);
        return "teachers/students/index";
    }


    @GetMapping("/active")
    String showOnlyActive(@SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                          Model model) {
        List<Student> students = studentQueryService.
                getAllActiveStudentsByTeacherId(loggedTeacher.getId());
        model.addAttribute("teacher", loggedTeacher);
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
        model.addAttribute("teacher", loggedTeacher);
        model.addAttribute("student", student);
        model.addAttribute("slots", student.getSlots());
        model.addAttribute("countPlannedThisMonth", lessonQueryService.countGeneratedLessonThisMonthByStudent(teacherId, id));
        model.addAttribute("plannedLessonThisMonth", lessonQueryService.getAllLessonsInMonthByStudent(teacherId, id));
        model.addAttribute("countNotPaidNextMonth", lessonQueryService.countNotPaidLessonsByStudentNextMonth(teacherId, id));
        model.addAttribute("notPaidLessonsNextMonth", lessonQueryService.getNotPaidLessonsUntilLastDayOfNextMonth(teacherId, id));
        model.addAttribute("overPayment", studentQueryService.getOverPayment(id));
        model.addAttribute("requiredPayment", lessonQueryService.getRequiredPaymentByStudentNextMonth(id, teacherId));
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
        model.addAttribute("valid", true);
        return "teachers/students/submit";
    }


    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") Long studentId,
                  @SessionAttribute("loggedTeacher") TeacherDTO loggedTeacher,
                  @RequestParam("date") @NotBlank String dateAsString,
                  Model model
    ) {

        boolean isDate = GenericValidator.isDate(dateAsString, "yyyy-MM-dd", true);
        if (!isDate) {
            StudentDTO studentDTO = studentQueryService.getOneStudentDTOOrThrow(studentId);
            model.addAttribute("student", studentDTO);
            model.addAttribute("valid", false);
            model.addAttribute("error", "Pole 'data' nie może być puste. Prawidłowy format:\n \"yyyy-MM-dd\"");
            return "teachers/students/submit";
        }
        LocalDate date = LocalDate.parse(dateAsString);
        studentCommandService.suspend(loggedTeacher.getId(), studentId, date);
        return "redirect:/teacher/students";
    }


    @GetMapping("/activate/{id}")
    String activate(@PathVariable Long id) {
        studentCommandService.activate(id);
        return "redirect:/teacher/students/" + id.toString() + "/profile";
    }

}
