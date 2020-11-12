package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.*;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.slot.dto.SlotInfoDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.week.WeekCommandService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class LessonCommandService {
    private LessonRepository lessonRepository;
    private LessonQueryService lessonQueryService;
    private SlotQueryService slotQueryService;
    private TeacherQueryService teacherQueryService;
    private WeekCommandService weekCommandService;
    private WeekQueryService weekQueryService;
    private StudentQueryService studentQueryService;

    public void create(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setDate(lessonDTO.getDate());
        lesson.setSlot(lessonDTO.getSlot());
        lesson.setStudent(lessonDTO.getStudent());
        lesson.setWeek(lessonDTO.getWeek());
        // lesson.setTeacher(lessonDTO.getTeacher());
        lesson.setCompleted(false);
        lesson.setCancelled(false);
        lesson.setLastMinuteCancelled(false);
        lesson.setTransferred(false);
        lesson.setArchived(false);
        lessonRepository.save(lesson);
    }

    public void updateCompletion(LessonCompletionDTO lessonCompletionDTO) {
        Lesson lesson = lessonQueryService.getOneOrThrow(lessonCompletionDTO.getId());
        lesson.setCompleted(lessonCompletionDTO.isCompleted());
        lesson.setCancelled(lessonCompletionDTO.isCancelled());
        lesson.setLastMinuteCancelled(lessonCompletionDTO.isLastMinuteCancelled());
        lesson.setTransferred(lessonCompletionDTO.isTransferred());
        // lesson.setArchived(false);
        lessonRepository.save(lesson);
    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }

    public void generateWeekLessonsForTeacher(Long id) {
        List<SlotInfoDTO> bookedSlots = slotQueryService.getAllBookedSlotInfoDtoByTeacher(id);
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zoneId);
        int weekOfYear = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        int thisYear = today.getYear();
        LocalDate thisMondayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisSundayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        bookedSlots.forEach(slotInfoDTO -> {
            LessonDTO lessonDTO = new LessonDTO();
            switch (slotInfoDTO.getDayOfWeek().ordinal()) {

                case 0:   //mon
                    lessonDTO.setDate(thisMondayDate);
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //  lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 1:     //tue
                    lessonDTO.setDate(thisMondayDate.plusDays(1));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //  lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 2:    //wed
                    lessonDTO.setDate(thisMondayDate.plusDays(2));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //   lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 3:    //thu
                    lessonDTO.setDate(thisMondayDate.plusDays(3));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //   lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 4:    //fri
                    lessonDTO.setDate(thisMondayDate.plusDays(4));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //   lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 5:    //sat
                    lessonDTO.setDate(thisMondayDate.plusDays(5));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //  lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
                case 6:    //sun
                    lessonDTO.setDate(thisMondayDate.plusDays(6));
                    lessonDTO.setSlot(slotQueryService.getOneOrThrow(slotInfoDTO.getId()));
                    lessonDTO.setStudent(slotInfoDTO.getRegularStudent());
                    lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, weekOfYear, id));
                    //   lessonDTO.setTeacher(teacherQueryService.getOneOrThrow(id));
                    create(lessonDTO);
                    break;
            }
        });


        weekCommandService.setGenerated(weekOfYear, today.getYear(), id);

    }

    public void generateFutureLessonsForStudent(Slot slot, Long studentId) {
        //pobierz tygdoni, które już są generowane, z przysszłości
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(slot.getTeacher());
        //wygeneruj lessony dla tych tygodni,jesli data jest późniejsz niz dzijaj
        generateAdditionalLessons(weeks, slot, studentId);
        //zapisz lessony
    }

    private void generateAdditionalLessons(List<Week> weeks, Slot slot, Long studentId) {
        LessonDTO lessonDTO = new LessonDTO();
        LocalDate today = LocalDate.now();
        int numberOfDayOfWeek = today.getDayOfWeek().getValue() - 1;
        int thisWeek = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        int thisYear = today.getYear();
        List<Integer> numberOfWeekList = weeks.stream().map(Week::getNumberOfWeek).collect(Collectors.toList());

        //Check if this week should be changed
        if (numberOfWeekList.contains(thisWeek)) {
            if (slot.getDayOfWeek().ordinal() > numberOfDayOfWeek) {
                lessonDTO.setSlot(slot);
                LocalDate thisDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(slot.getDayOfWeek().name())));
                lessonDTO.setDate(thisDate);
                lessonDTO.setStudent(studentQueryService.getOneOrThrow(studentId));
                lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, thisWeek, slot.getTeacher().getId()));
                //  lessonDTO.setTeacher(slot.getTeacher());
                create(lessonDTO);
            }
        }

        //Check if other weeks should be changed                                                //cheka na testowanie
//        numberOfWeekList.stream().filter(num -> num != thisWeek).forEach(num -> {
//            lessonDTO.setSlot(slot);
//            LocalDate thisDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(slot.getDayOfWeek().name())));
//            lessonDTO.setDate(thisDate);
//            lessonDTO.setStudent(studentQueryService.getOneOrThrow(studentId));
//            lessonDTO.setWeek(weekQueryService.getOneOrThrow(thisYear, thisWeek,slot.getTeacher().getId()));
//           // lessonDTO.setTeacher(slot.getTeacher());
//            create(lessonDTO);
//        });

    }

    public void removeGeneratedLessons(Slot slot) {                              //cheka na testowanie
        LocalDate today = LocalDate.now();
        //pobierz tygdonie, które już są generowane, z przysszłości
        Student student = slot.getRegularStudent();
        Teacher teacher = slot.getTeacher();
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(teacher);
        //usuń lessony dla tych tygodni jeśli data jest późnijesz niż dzisiaj
        //lessonQueryService.getAllLessonsByWeek(w)
        List<Lesson> allLessons = new ArrayList<>();

        weeks.forEach(week -> {
            List<Lesson> weekLessons = lessonQueryService.getAllLessonsBySlotAndStudentInFuture(slot, student, today);
            allLessons.addAll(weekLessons);
        });

        allLessons.stream().forEach(lesson -> delete(lesson.getId()));


//        weeks.stream().forEach(week -> week.getLessons()
//                .stream()
//                .filter(lesson -> lesson.getSlot().equals(slot))
//                .filter(lesson -> lesson.getDate().isAfter(today))
//                .forEach(lesson -> delete(lesson.getId())));
    }
}
