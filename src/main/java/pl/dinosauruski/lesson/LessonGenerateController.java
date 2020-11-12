package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import pl.dinosauruski.teacher.dto.TeacherDTO;

@Controller
@AllArgsConstructor
@RequestMapping("/teacher/calendar")
public class LessonGenerateController {
    private LessonCommandService lessonCommandService;

    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO, Model model) {
        model.addAttribute("teacher", teacherDTO);
        return "calendar/index";
    }

    @GetMapping("/generate")
    String generateThisWeek(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO) {
        lessonCommandService.generateWeekLessonsForTeacher(teacherDTO.getId());
        return "redirect:/teacher/cockpit";
    }
}
