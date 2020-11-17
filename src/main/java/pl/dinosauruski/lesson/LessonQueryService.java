package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.week.WeekQueryService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class LessonQueryService {
    private LessonRepository lessonRepository;
    private WeekQueryService weekQueryService;

    public Lesson getOneOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonDTO getOneLessonDtoOrThrow(Long id) {
        return lessonRepository.findOneLessonDTO(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonCompletionDTO getOneLessonCompletionDtoOrThrow(Long id) {
        return lessonRepository.findOneCompletionDto(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Lesson> getAllThisWeekLessonsByTeacher(Long id) {
        //  ZoneId zoneId = ZoneId.of("Europe/Warsaw" );
        LocalDate today = LocalDate.now();
        LocalDate thisMondayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisSundayDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return lessonRepository.findAllThisWeekLessonsByTeacher(id, thisMondayDate, thisSundayDate);
    }

    public List<Lesson> getGeneratedLessonsOfWeek(int year, int nextNumberOfWeek, Long teacherId) {
        Week week = weekQueryService.getOneOrThrow(year, nextNumberOfWeek, teacherId);
        return lessonRepository.findAllLessonByWeekAndTeacherId(week, teacherId);
    }

    public List<Lesson> getAllMonthYearLessonsByTeacher(int year, int month, Long teacherId) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        int numberOfFirstWeek = weekQueryService.getNumberOfWeekByDate(firstDay);
        int numberOfLastWeek = weekQueryService.getNumberOfWeekByDate(lastDay);
        List<Lesson> allLessons = new ArrayList<>();
        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
            List<Lesson> lessonsOfWeek = getGeneratedLessonsOfWeek(year, i, teacherId);
            allLessons.addAll(lessonsOfWeek);
        }
        return allLessons;
    }

    public List<Lesson> getNextNotPaidLessons(BigDecimal countPaidLessons, Long teacherId, Long studentId) {
        List<Lesson> nextNotPaidLessons = lessonRepository.getNextNotPaidLessons(teacherId, studentId);
        return nextNotPaidLessons.stream()
                .limit(countPaidLessons.longValue())
                .collect(Collectors.toList());
    }

    public LessonPaymentDTO createLessonPaymentDTO(Lesson notPaidLesson) {
        LessonPaymentDTO lessonPaymentDTO = new LessonPaymentDTO();
        lessonPaymentDTO.setId(notPaidLesson.getId());
        lessonPaymentDTO.setDate(notPaidLesson.getDate());
        lessonPaymentDTO.setDayOfWeek(notPaidLesson.getSlot().getDayOfWeek());
        lessonPaymentDTO.setTime(notPaidLesson.getSlot().getTime());
        lessonPaymentDTO.setRebooked(notPaidLesson.isRebooked());
        boolean isRebooked = lessonPaymentDTO.isRebooked();
        if (isRebooked) {
            lessonPaymentDTO.setStudentWhoPays(notPaidLesson.getRebooking().getNotRegularStudent());
        } else {
            lessonPaymentDTO.setStudentWhoPays(notPaidLesson.getSlot().getRegularStudent());
        }
        return lessonPaymentDTO;
    }

    public List<LessonPaymentDTO> getNotPaidLessons(Long teacherId) {
        List<Lesson> notPaidLessons = lessonRepository.findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(teacherId);
        List<LessonPaymentDTO> lessonPaymentDTOS = notPaidLessons.stream()
                .map(this::createLessonPaymentDTO)
                .collect(Collectors.toList());
        return lessonPaymentDTOS;
    }

    public List<LessonPaymentDTO> getNotPaidLessonsByStudent(Long teacherId, Long studentId) {
        List<LessonPaymentDTO> notPaidLessonsByTeacher = getNotPaidLessons(teacherId);
        List<LessonPaymentDTO> notPaidLessonsByStudent = notPaidLessonsByTeacher.stream()
                .filter(lesson -> lesson.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());
        return notPaidLessonsByStudent;
    }
}
