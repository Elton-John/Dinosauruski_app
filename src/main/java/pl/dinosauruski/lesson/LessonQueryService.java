package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.*;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.week.WeekQueryService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class LessonQueryService {
    private LessonRepository lessonRepository;
    private WeekQueryService weekQueryService;
    private StudentQueryService studentQueryService;


    public Lesson getOneOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }


    public LessonDTO getOneLessonDtoOrThrow(Long id) {
        return lessonRepository.findOneLessonDTO(id).orElseThrow(EntityNotFoundException::new);
    }


    public LessonsOfWeekDTO getAllThisWeekLessonsByTeacher(Long id) {
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zoneId);
        LocalDate thisMondayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return createLessonsOfWeekDTO(thisMondayDate, id);
    }


    public LessonsOfWeekDTO createLessonsOfWeekDTO(LocalDate monday, Long teacherId) {
        LessonsOfWeekDTO lessonsOfWeekDTO = new LessonsOfWeekDTO();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate day = monday;
        for (int i = 0; i < 7; i++) {
            dates.add(day);
            day = day.plusDays(1);
        }

        lessonsOfWeekDTO.setDates(dates);

        List<LessonsOfDayDTO> lessonsOfDayDTOs = getLessonsOfDayDTOS(dates, teacherId);
        lessonsOfWeekDTO.setLessonsOfDayDTOS(lessonsOfDayDTOs);
        return lessonsOfWeekDTO;
    }


    private List<LessonsOfDayDTO> getLessonsOfDayDTOS(List<LocalDate> dates, Long teacherId) {
        return dates.stream().filter(localDate -> hasLesson(localDate, teacherId))
                .map(localDate -> createLessonsDayDTO(localDate, teacherId))
                .collect(Collectors.toList());
    }


    private LessonsOfDayDTO createLessonsDayDTO(LocalDate localDate, Long teacherId) {
        LessonsOfDayDTO lessonsOfDayDTO = new LessonsOfDayDTO();
        lessonsOfDayDTO.setDate(localDate);
        lessonsOfDayDTO.setDayOfWeek(localDate.getDayOfWeek().ordinal());
        List<LessonViewDTO> lessonViewDTOS = new ArrayList<>();
        List<Lesson> lessons = lessonRepository.findAllByDateAndTeacherId(localDate, teacherId);
        lessons.forEach(lesson -> {
            lessonViewDTOS.add(createLessonViewDTO(lesson));
        });
        //lessonViewDTOS.sort(new LessonTimeComparator());
        lessonViewDTOS.sort(Comparator.comparing(LessonViewDTO::getTime));
        lessonsOfDayDTO.setLessonViewDTOS(lessonViewDTOS);
        return lessonsOfDayDTO;
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
        lessonPaymentDTO.setPaid(lesson.isPaid());
        boolean isRebooked = lessonPaymentDTO.isRebooked();
        if (isRebooked) {
            lessonPaymentDTO.setStudentWhoPays(lesson.getRebooking().getNotRegularStudent());
        } else {
            lessonPaymentDTO.setStudentWhoPays(lesson.getSlot().getRegularStudent());
        }
        return lessonPaymentDTO;
    }


    private LessonViewDTO createLessonViewDTO(Lesson lesson) {
        LessonViewDTO lessonViewDTO = new LessonViewDTO();
        lessonViewDTO.setId(lesson.getId());
        lessonViewDTO.setDate(lesson.getDate());
        lessonViewDTO.setTime(lesson.getSlot().getTime());
        lessonViewDTO.setDayOfWeek(DayOfWeek.of(lesson.getSlot().getDayOfWeek().ordinal() + 1));
        lessonViewDTO.setWeek(lesson.getWeek());
        lessonViewDTO.setStudent(createLessonPaymentDTO(lesson).getStudentWhoPays());
        lessonViewDTO.setCompleted(lesson.isCompleted());
        lessonViewDTO.setCancelledByTeacher(lesson.isCancelledByTeacher());
        lessonViewDTO.setCancelledByStudent(lesson.isCancelledByStudent());
        lessonViewDTO.setLastMinuteCancelled(lesson.isLastMinuteCancelled());
        lessonViewDTO.setRebooked(lesson.isRebooked());
        lessonViewDTO.setRequiredPayment(lesson.isRequiredPayment());
        lessonViewDTO.setPaid(lesson.isPaid());
        return lessonViewDTO;
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


    public List<Lesson> getAllLessonsInMonthByStudent(Long teacherId, Long studentId) {
        Month month = LocalDate.now().getMonth();
        int year = LocalDate.now().getYear();

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        List<Lesson> regularLessons = lessonRepository.findAllNotCancelledByTeacherAndStudentInMonth(firstDay, lastDay, teacherId, studentId);
        List<Lesson> rebookedLessons = lessonRepository.findAllRebookedByTeacherAndStudentInMonth(firstDay, lastDay, teacherId, studentId);
        regularLessons.addAll(rebookedLessons);
        return regularLessons.stream()
                .sorted(Comparator.comparing(Lesson::getDate))
                .collect(Collectors.toList());
    }


    public int countGeneratedLessonThisMonthByStudent(Long teacherId, Long studentId) {
        List<Lesson> lessons = getAllLessonsInMonthByStudent(teacherId, studentId);
        return lessons.size();
    }


    public List<Lesson> getNotPaidLessonsUntilLastDayOfNextMonth(Long teacherId, Long studentId) {
        Month thisMonth = LocalDate.now().getMonth();
        Month nextMonth = thisMonth.plus(1);
        int year = LocalDate.now().getYear();

        if (thisMonth == Month.DECEMBER) {
            nextMonth = Month.JANUARY;
            year++;
        }
        YearMonth ym = YearMonth.of(year, nextMonth);
        LocalDate lastDay = ym.atEndOfMonth();

        List<Lesson> regularLessons = lessonRepository.findAllNotCancelledByTeacherAndStudentUntilNextMonth(lastDay, teacherId, studentId);
        List<Lesson> rebookedLessons = lessonRepository.findAllRebookedByTeacherAndStudentUntilNextMonth(lastDay, teacherId, studentId);
        regularLessons.addAll(rebookedLessons);
        List<Lesson> result = regularLessons.stream()
                .filter(lesson -> !lesson.isPaid())
                .sorted(Comparator.comparing(Lesson::getDate))
                .collect(Collectors.toList());
        return result;
    }


    public int countNotPaidLessonsByStudentNextMonth(Long teacherId, Long studentId) {
        List<Lesson> lessons = getNotPaidLessonsUntilLastDayOfNextMonth(teacherId, studentId);
        return lessons.size();
    }


    public BigDecimal getRequiredPaymentByStudentNextMonth(Long studentId, Long teacherId) {
        int count = countNotPaidLessonsByStudentNextMonth(teacherId, studentId);
        BigDecimal priceForOneLesson = studentQueryService.getOneOrThrow(studentId).getPriceForOneLesson();
        BigDecimal require = priceForOneLesson.multiply(BigDecimal.valueOf(count));
        BigDecimal result = require.subtract((BigDecimal) studentQueryService.getOverPayment(studentId));

        return result;
    }


    public List<LessonsOfWeekDTO> getAllLessonsOfAllWeeksOfMonth(int year, int month, Long teacherId) {
        List<LocalDate> allMondaysOfMonth = weekQueryService.getAllMondaysOfMonth(year, month);
        List<LessonsOfWeekDTO> result = new ArrayList<>();
        allMondaysOfMonth.forEach(localDate -> {
            LessonsOfWeekDTO lessonsOfWeekDTO = createLessonsOfWeekDTO(localDate, teacherId);
            result.add(lessonsOfWeekDTO);
        });
        return result;
    }


    private boolean hasLesson(LocalDate localDate, Long teacherId) {
        List<Lesson> lessons = lessonRepository.findAllByDateAndTeacherId(localDate, teacherId);
        return lessons != null;
    }


    public boolean generatedLessonsWereRebookedOrCancelByTeacherAfterDate(Long slotId, LocalDate date) {
        List<Optional<Lesson>> lessons = getAllGeneratedLessonsBySlotAfterDate(slotId, date);
        List<Lesson> changedLessons = lessons.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(lesson -> lesson.isCancelledByTeacher() || lesson.isRebooked())
                .collect(Collectors.toList());
        return changedLessons.size() > 0;
    }


    public List<Optional<Lesson>> getAllGeneratedLessonsBySlotAfterDate(Long slotId, LocalDate date) {
        return lessonRepository.findAllGeneratedLessonsBySlotWhereDateIsAfter(date, slotId);
    }


    public boolean checkSlotHasGeneratedLessons(Long slotId) {
        int count = lessonRepository.findAllGeneratedLessonsBySlot(slotId);
        return count > 0;
    }


    public List<Lesson> getPaidLessonsByStudent(Long teacherId, Long studentId) {
        List<Lesson> paidLessonsByRegularStudent = lessonRepository.findAllPaidLessonsByRegularStudent(teacherId, studentId);
        List<Lesson> paidLessonsByNotRegularStudent = lessonRepository.findAllPaidLessonsByNotRegularStudent(teacherId, studentId);
        List<Lesson> result = new ArrayList<>(paidLessonsByRegularStudent);
        result.addAll(paidLessonsByNotRegularStudent);
        return result;
    }


    public List<Optional<Lesson>> getAllRebookingLessonByStudentAndTeacherAfterDate(LocalDate date, Long teacherId, Long studentId) {


        return lessonRepository.findAllRebookedByTeacherAndStudentWhereDateIsAfter(date, teacherId, studentId);
    }

}
