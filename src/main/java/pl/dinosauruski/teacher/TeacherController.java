package pl.dinosauruski.teacher;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.lesson.dto.LessonsOfWeekDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;
import pl.dinosauruski.teacher.dto.TeacherEditDTO;
import pl.dinosauruski.week.WeekQueryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/teacher")
class TeacherController {
    private final TeacherCommandService teacherCommandService;
    private final TeacherQueryService teacherQueryService;
    private final SlotQueryService slotQueryService;
    private final WeekQueryService weekQueryService;
    private final LessonQueryService lessonQueryService;


    @GetMapping("/cockpit")
    public String displayCockpit(@SessionAttribute(value = "loggedTeacher", required = false) TeacherDTO loggedTeacher,
                                 Model model,
                                 HttpServletRequest request) {
//        Cookie loginCookie = WebUtils.getCookie(request, "login");
//        Cookie idCookie = WebUtils.getCookie(request, "id");
//        if (loginCookie == null || idCookie == null) {
//            return "redirect:/";
//        }
//        Long id = Long.parseLong(idCookie.getValue());
        Long id = loggedTeacher.getId();
        Teacher teacher = teacherQueryService.getOneOrThrow(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("freeSlots", slotQueryService.getAllFreeSlotsByTeacher(id));
        Boolean isGenerated = weekQueryService.checkCurrentWeekIsGenerated(id);
        if (isGenerated) {
            model.addAttribute("isGenerated", true);
                    LessonsOfWeekDTO lessons = lessonQueryService.getAllThisWeekLessonsByTeacher(id);
                     model.addAttribute("week", lessons);
        } else {
            model.addAttribute("isGenerated", false);
        }
        return "teachers/cockpit";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        TeacherEditDTO teacherEditDTO = teacherQueryService.getOneDTOToEdit(id);
        model.addAttribute("teacher", teacherEditDTO);
        return "teachers/edit";
    }


    @PatchMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("teacher") TeacherEditDTO teacherEditDTO,
                         BindingResult result,
                         Model model) {
        teacherQueryService.getOneOrThrow(id);
        if (result.hasErrors()) {
            return "teachers/edit";
        }
        Teacher updatedTeacher = teacherCommandService.update(teacherEditDTO);
        model.addAttribute("teacher", updatedTeacher);
        return "redirect:cockpit";
    }


    @GetMapping("/submit/{id}")
    public String submitDeleting(@PathVariable Long id, Model model) {
        Teacher teacher = teacherQueryService.getOneOrThrow(id);
        model.addAttribute("name", teacher.getNickname());
        model.addAttribute("id", id);
        return "/teachers/submit";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        Teacher teacher = teacherQueryService.getOneOrThrow(id);
        teacherCommandService.delete(teacher);
//        teacherService.deleteCookie(request, response, "login");
//        teacherService.deleteCookie(request, response, "id");
        return "redirect:/";
    }

}
