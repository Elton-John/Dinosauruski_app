package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.week.WeekQueryService;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;

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
        model.addAttribute("index", true);
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
}
