package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCancellingDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.payment.PaymentQueryService;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.week.WeekQueryService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
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
    private StudentQueryService studentQueryService;
    private PaymentQueryService paymentQueryService;

    public Lesson getOneOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonDTO getOneLessonDtoOrThrow(Long id) {
        return lessonRepository.findOneLessonDTO(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonCancellingDTO getOneLessonCompletionDtoOrThrow(Long id) {
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


    public Lesson getNextNotPaidLesson(Long teacherId, Long studentId) {
        List<Lesson> lessons = lessonRepository.findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(teacherId);
        if (lessons != null) {
            LessonPaymentDTO firstLessonPaymentDTO = lessons.stream()
                    .map(this::createLessonPaymentDTO)
                    .filter(lessonPaymentDTO -> lessonPaymentDTO.getStudentWhoPays().getId().equals(studentId))
                    .collect(Collectors.toList()).get(0);
            return getOneOrThrow(firstLessonPaymentDTO.getId());
        }
        return null;
    }

    public LessonPaymentDTO createLessonPaymentDTO(Lesson lesson) {
        LessonPaymentDTO lessonPaymentDTO = new LessonPaymentDTO();
        lessonPaymentDTO.setId(lesson.getId());
        lessonPaymentDTO.setDate(lesson.getDate());
        lessonPaymentDTO.setDayOfWeek(lesson.getSlot().getDayOfWeek());
        lessonPaymentDTO.setTime(lesson.getSlot().getTime());
        lessonPaymentDTO.setWeek(lesson.getWeek());
        lessonPaymentDTO.setRebooked(lesson.isRebooked());
        lessonPaymentDTO.setRequiredPayment(lesson.isRequiredPayment());
        boolean isRebooked = lessonPaymentDTO.isRebooked();
        if (isRebooked) {
            lessonPaymentDTO.setStudentWhoPays(lesson.getRebooking().getNotRegularStudent());
        } else {
            lessonPaymentDTO.setStudentWhoPays(lesson.getSlot().getRegularStudent());
        }
        return lessonPaymentDTO;
    }

    public List<LessonPaymentDTO> getNotPaidLessons(Long teacherId) {
        List<Lesson> notPaidLessons = lessonRepository.findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(teacherId);
        return notPaidLessons.stream()
                .map(this::createLessonPaymentDTO)
                .collect(Collectors.toList());
    }

    public List<LessonPaymentDTO> getNotPaidLessonsByStudent(Long teacherId, Long studentId) {
        List<LessonPaymentDTO> notPaidLessonsByTeacher = getNotPaidLessons(teacherId);
        return notPaidLessonsByTeacher.stream()
                .filter(lesson -> lesson.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<LessonPaymentDTO> getPaidLessonsThisMonth(Long teacherId, Long studentId) {
        Month month = LocalDate.now().getMonth();
        int year = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        List<Lesson> lessons = lessonRepository.findAllByTeacherInMonth(firstDay, lastDay, teacherId);
        return lessons.stream()
                .filter(Lesson::isPaid)
                .map(this::createLessonPaymentDTO)
                .filter(lessonPaymentDTO -> lessonPaymentDTO.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());
    }

    public int countPaidLessonsByStudentThisMonth(Long teacherId, Long studentId) {
        List<LessonPaymentDTO> lessons = getPaidLessonsThisMonth(teacherId, studentId);
        return lessons.size();
    }

    public List<LessonPaymentDTO> getNotPaidLessonsUntilLastDayOfNextMonth(Long teacherId, Long studentId) {
        Month thisMonth = LocalDate.now().getMonth();
        Month nextMonth = thisMonth.plus(1);
        int year = LocalDate.now().getYear();

        if (thisMonth == Month.DECEMBER) {
            nextMonth = Month.JANUARY;
            year++;
        }
        YearMonth ym = YearMonth.of(year, nextMonth);
        LocalDate lastDay = ym.atEndOfMonth();
        List<Lesson> lessons = lessonRepository.findAllByTeacherUntilLastDayOfNextMonth(lastDay, teacherId);
        return lessons.stream()
                .filter(Lesson::isRequiredPayment)
                .map(this::createLessonPaymentDTO)
                .filter(lessonPaymentDTO -> lessonPaymentDTO.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());

    }

    public BigDecimal countRequiredPaymentAfterAddingOverPayment(Long studentId, Long teacherId) {
        List<LessonPaymentDTO> notPaidLessons = getNotPaidLessonsUntilLastDayOfNextMonth(teacherId, studentId);
        BigDecimal overPaymentByStudent = paymentQueryService.getOverPayment(studentId, teacherId);
        BigDecimal priceForOneLesson = studentQueryService.getOneOrThrow(studentId).getPriceForOneLesson();
        int quantityLessonsCanBePaid = overPaymentByStudent.divide(priceForOneLesson, 2, RoundingMode.HALF_UP).intValue();
        if (overPaymentByStudent.equals(BigDecimal.valueOf(0))) {
            return priceForOneLesson.multiply(BigDecimal.valueOf(notPaidLessons.size()));
        } else if (quantityLessonsCanBePaid < notPaidLessons.size()) {
            int difference = notPaidLessons.size() - quantityLessonsCanBePaid;
            int lessonsRequiredPayment = notPaidLessons.size() - difference;
            return priceForOneLesson.multiply(BigDecimal.valueOf(lessonsRequiredPayment));
        } else {
            return BigDecimal.valueOf(0);

        }
    }

    public int countNotPaidLessonsByStudentNextMonth(Long teacherId, Long studentId) {
        List<LessonPaymentDTO> lessons = getNotPaidLessonsUntilLastDayOfNextMonth(teacherId, studentId);
        return lessons.size();
    }


    public List<LessonPaymentDTO> getGeneratedLessonInMonthByStudent(Long teacherId, Long studentId) {
        Month month = LocalDate.now().getMonth();
        int year = LocalDate.now().getYear();

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        List<Lesson> lessons = lessonRepository.findAllByTeacherInMonth(firstDay, lastDay, teacherId);
        return lessons.stream()
                .map(this::createLessonPaymentDTO)
                .filter(lessonPaymentDTO -> lessonPaymentDTO.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());
    }

    public Object countGeneratedLessonThisMonthByStudent(Long teacherId, Long studentId) {
        List<LessonPaymentDTO> lessons = getGeneratedLessonInMonthByStudent(teacherId, studentId);
        return lessons.size();
    }
}
