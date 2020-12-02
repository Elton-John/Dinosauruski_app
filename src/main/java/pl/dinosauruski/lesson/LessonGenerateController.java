package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonsOfWeekDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.rebooking.RebookingCommandService;
import pl.dinosauruski.rebooking.RebookingQueryService;
import pl.dinosauruski.rebooking.dto.RebookingDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.week.WeekQueryService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/teacher/calendar")
public class LessonGenerateController {
    private LessonCommandService lessonCommandService;
    private WeekQueryService weekQueryService;
    private LessonQueryService lessonQueryService;
    private StudentQueryService studentQueryService;
    private RebookingCommandService rebookingCommandService;
    private RebookingQueryService rebookingQueryService;

    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO, Model model) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int previousYear = year - 1;
        int nextYear = year + 1;
        model.addAttribute("teacher", teacherDTO);
        model.addAttribute("year", year);
        model.addAttribute("previousYear", previousYear);
        model.addAttribute("nextYear", nextYear);
        model.addAttribute("months");
        return "calendar/index";
    }

    @GetMapping("/{year}")
    String showYear(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                    @PathVariable("year") int year, Model model) {
        int previousYear = year - 1;
        int nextYear = year + 1;
        model.addAttribute("teacher", teacherDTO);
        model.addAttribute("year", year);
        model.addAttribute("previousYear", previousYear);
        model.addAttribute("nextYear", nextYear);
        model.addAttribute("months");
        return "calendar/index";
    }

    @GetMapping("/generate/month/{month}/{year}")
    String generateMonth(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                         @PathVariable int month,
                         @PathVariable int year) {
        lessonCommandService.generateMonthLessonsForTeacher(year, month, teacherDTO.getId());
        return "redirect:/teacher/calendar/" + month + "/" + year;
    }

    @GetMapping("/{month}/{year}")
    String showMonthYearLessons(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                                @PathVariable int month,
                                @PathVariable int year,
                                Model model) {

        model.addAttribute("teacher", teacherDTO);
        boolean isGenerated = weekQueryService.checkMonthIsGenerated(year, month, teacherDTO.getId());
        if (isGenerated) {
            List<LessonsOfWeekDTO> weeks = lessonQueryService.getAllLessonsOfAllWeeksOfMonth(year, month, teacherDTO.getId());
            model.addAttribute("isGenerated", true);
            model.addAttribute("weeks", weeks);
            model.addAttribute("thisMonth", month);
            model.addAttribute("thisYear", year);
            model.addAttribute("months");
            return "calendar/month";
        }
        model.addAttribute("isGenerated", false);
        model.addAttribute("thisMonth", month);
        model.addAttribute("thisYear", year);
        model.addAttribute("months");
        return "calendar/month";

    }

    @GetMapping("/lessons/{id}")
    String letCancelLessonByTeacher(@PathVariable Long id,
                                    Model model) {
        model.addAttribute("lesson", lessonQueryService.getOneOrThrow(id));
        return "calendar/lesson";
    }

    @PostMapping("/lessons/cancel/{id}")
    String cancelLessonByTeacher(@PathVariable Long id,
                                 HttpServletRequest request) {
        String cancel = request.getParameter("cancel");
        if (!lessonCommandService.cancelling(id, cancel)) {
            return "redirect:/teacher/calendar/lessons/" + id;
        }
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        int month = lesson.getDate().getMonth().getValue();
        int year = lesson.getDate().getYear();
        return "redirect:/teacher/calendar/" + month + "/" + year;
    }

    @PostMapping("/lessons/delete/{id}")
    String deleteLesson(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                        @PathVariable Long id) {

        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        int month = lesson.getDate().getMonth().getValue();
        int year = lesson.getDate().getYear();
        lessonCommandService.delete(id, teacherDTO.getId());
        return "redirect:/teacher/calendar/" + month + "/" + year;
    }

    @GetMapping("/lessons/rebooking/{id}")
    String letAddStudentToOnceFreeSlot(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                                       @PathVariable Long id,
                                       Model model) {
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        List<Student> students = studentQueryService.getAllActiveStudentsByTeacherId(teacherDTO.getId());
        model.addAttribute("lesson", lesson);
        model.addAttribute("students", students);
        return "calendar/rebooking";
    }

    @PostMapping("/lessons/rebooking/{id}")
    String addStudentToOnceFreeSlot(
            @PathVariable Long id,
            Model model,
            @RequestParam("notRegularStudent") Student student) {
        rebookingCommandService.create(id, student);
        LessonDTO lessonDTO = lessonQueryService.getOneLessonDtoOrThrow(id);
        int month = lessonDTO.getDate().getMonth().ordinal() + 1;
        int year = lessonDTO.getDate().getYear();
        return "redirect:/teacher/calendar/" + month + "/" + year;
    }

    @GetMapping("/lessons/rebooking/edit/{id}")
    String letEditOnesFreeBookedLesson(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                                       @PathVariable Long id,
                                       Model model) {
        model.addAttribute("rebooking", rebookingQueryService.getOneDtoOrThrow(id));
        model.addAttribute("lesson", lessonQueryService.getOneLessonDtoOrThrow(id));
        List<Student> students = studentQueryService.getAllActiveStudentsByTeacherId(teacherDTO.getId());
        model.addAttribute("students", students);
        return "calendar/editRebooking";
    }

    @PatchMapping("/lessons/rebooking/edit/{id}")
    String editOnesFreeBookedLesson(@ModelAttribute("rebooking") RebookingDTO rebookingDTO,
                                    @PathVariable Long id) {
        rebookingCommandService.update(rebookingDTO);
        LessonDTO lessonDTO = lessonQueryService.getOneLessonDtoOrThrow(id);
        int month = lessonDTO.getDate().getMonth().ordinal() + 1;
        int year = lessonDTO.getDate().getYear();
        return "redirect:/teacher/calendar/" + month + "/" + year;
    }

    @GetMapping("/lessons/rebooking/delete/{id}")
    String cancelBookingOnceFreeSlot(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                                     @PathVariable Long id) {
        lessonCommandService.cancelBookingOnceFreeLesson(id);
        LessonDTO lessonDTO = lessonQueryService.getOneLessonDtoOrThrow(id);
        int month = lessonDTO.getDate().getMonth().ordinal() + 1;
        int year = lessonDTO.getDate().getYear();
        return "redirect:/teacher/calendar/" + month + "/" + year;

    }

    @ModelAttribute("months")
    private Map<Integer, String> months() {
        Map<Integer, String> months = new HashMap<>();
        months.put(1, "styczeń");
        months.put(2, "luty");
        months.put(3, "marzec");
        months.put(4, "kwiecień");
        months.put(5, "maj");
        months.put(6, "czerwiec");
        months.put(7, "lipiec");
        months.put(8, "sierpień");
        months.put(9, "wrzesień");
        months.put(10, "październik");
        months.put(11, "listopad");
        months.put(12, "grudzień");
        return months;
    }

}
