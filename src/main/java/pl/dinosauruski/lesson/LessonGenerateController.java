package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.week.WeekQueryService;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
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

    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO, Model model) {
        Long teacherId = teacherDTO.getId();
        int numberOfWeek = weekQueryService.getCurrentNumberOfWeek();
        int year = weekQueryService.getCurrentYear();
        int nextNumberOfWeek = numberOfWeek + 1;
        int nextYear = year;
        LocalDate now = LocalDate.now();
        int weeksInYear = (int) IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(now).getMaximum();
        if (numberOfWeek == weeksInYear) {
            nextNumberOfWeek = 1;
            nextYear = year + 1;
        }

        boolean isGenerated = weekQueryService.checkWeekIsGenerated(year, numberOfWeek, teacherId);
        if (isGenerated) {
            model.addAttribute("isGenerated", true);
            List<Lesson> lessons = lessonQueryService.getGeneratedLessonsOfWeek(year, numberOfWeek, teacherId);
            model.addAttribute("thisWeekLessons", lessons);
        } else {
            model.addAttribute("isGenerated", false);
        }

        boolean isNextGenerated = weekQueryService.checkWeekIsGenerated(year, nextNumberOfWeek, teacherId);
        if (isNextGenerated) {
            model.addAttribute("isNextGenerated", true);
            List<Lesson> lessons = lessonQueryService.getGeneratedLessonsOfWeek(year, nextNumberOfWeek, teacherId);
            model.addAttribute("nextWeekLessons", lessons);
        } else {
            model.addAttribute("isNextGenerated", false);
        }

        model.addAttribute("teacher", teacherDTO);
        model.addAttribute("week", numberOfWeek);
        model.addAttribute("year", year);
        model.addAttribute("nextWeek", nextNumberOfWeek);
        model.addAttribute("nextYear", nextYear);
//        model.addAttribute("index", true);
        model.addAttribute("months");
        return "calendar/index";
    }

    @GetMapping("/generate/{year}/{week}")
    String generateWeek(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                        @PathVariable int year,
                        @PathVariable int week,
                        Model model) {
        lessonCommandService.generateWeekLessonsForTeacher(year, week, teacherDTO.getId());
        return "redirect:/teacher/calendar";
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
        boolean isGenerated = weekQueryService.checkMonthIsGenerated(year, month, teacherDTO.getId());
        if (isGenerated) {
            List<Lesson> lessons = lessonQueryService.getAllMonthYearLessonsByTeacher(year, month, teacherDTO.getId());
            model.addAttribute("isGenerated", true);
            model.addAttribute("lessons", lessons);
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
