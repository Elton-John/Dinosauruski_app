package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.*;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.payment.PaymentQueryService;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.week.WeekQueryService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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

    public LessonsOfWeekDTO getAllThisWeekLessonsByTeacher(Long id) {
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zoneId);
        LocalDate thisMondayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisSundayDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
//        List<Lesson> lessons = lessonRepository.findAllThisWeekLessonsByTeacher(id, thisMondayDate, thisSundayDate);
//       lessons.stream().map(lesson -> c)
        LessonsOfWeekDTO lessonsOfWeekDTO = createLessonsOfWeekDTO(thisMondayDate, id);
        return lessonsOfWeekDTO;
    }

    public List<Lesson> getGeneratedLessonsOfWeek(int year, int nextNumberOfWeek, Long teacherId) {
        Week week = weekQueryService.getOneOrThrow(year, nextNumberOfWeek, teacherId);
        return lessonRepository.findAllLessonByWeekAndTeacherId(week, teacherId);
    }

//    public List<Lesson> getAllMonthYearLessonsByTeacher(int year, int month, Long teacherId) {
//        YearMonth ym = YearMonth.of(year, month);
//        LocalDate firstDay = ym.atDay(1);
//        LocalDate lastDay = ym.atEndOfMonth();
//        int numberOfFirstWeek = weekQueryService.getIdOfWeekByDate(firstDay,teacherId);
//        int numberOfLastWeek = weekQueryService.getIdOfWeekByDate(lastDay,teacherId);
//        List<Lesson> allLessons = new ArrayList<>();
//        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
//            List<Lesson> lessonsOfWeek = getGeneratedLessonsOfWeek(year, i, teacherId);
//            allLessons.addAll(lessonsOfWeek);
//        }
//        return allLessons;
//    }


    public Optional<Lesson> getNextNotPaidLesson(Long teacherId, Long studentId) {
        List<Lesson> allLessonsByTeacher = lessonRepository.findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(teacherId);

        List<LessonPaymentDTO> allLessonsByStudent = allLessonsByTeacher.stream()
                .map(this::createLessonPaymentDTO)
                .filter(lessonPaymentDTO -> lessonPaymentDTO.getStudentWhoPays().getId().equals(studentId))
                .collect(Collectors.toList());


        if (allLessonsByStudent.size() > 0) {
            LessonPaymentDTO firstLessonPaymentDTO = allLessonsByStudent.get(0);
            return Optional.ofNullable(getOneOrThrow(firstLessonPaymentDTO.getId()));
        }
        return Optional.empty();
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
        return lessonViewDTO;
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


    public LessonsOfWeekDTO createLessonsOfWeekDTO(LocalDate monday, Long teacherId) {
        LessonsOfWeekDTO lessonsOfWeekDTO = new LessonsOfWeekDTO();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate day = monday;
        for (int i = 0; i < 7; i++) {
            dates.add(day);
            day = day.plusDays(1);
        }

        lessonsOfWeekDTO.setDates(dates);   //sort

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
        lessonViewDTOS.sort(new LessonTimeComparator());
        lessonsOfDayDTO.setLessonViewDTOS(lessonViewDTOS);
        return lessonsOfDayDTO;
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
        return lessonRepository.findAllGeneratedLessonsBySlotWhereDateIsAfter( date, slotId);
    }

    public boolean checkSlotHasGeneratedLessons(Long slotId) {
       int count =  lessonRepository.findAllGeneratedLessonsBySlot(slotId);
        return count > 0;
    }

//    public Table<Long, LocalDate, LessonViewDTO> createOneMonthSchedule(int year, Month month, Long teacherId) {
//        Table<Long, LocalDate, LessonViewDTO> schedule = TreeBasedTable.create();
//        List<Lesson> allMonthLessons = getAllMonthYearLessonsByTeacher(year, month.ordinal(), teacherId);
//        allMonthLessons.forEach(lesson -> {
//            LessonViewDTO lessonViewDTO = createLessonViewDTO(lesson);
//            schedule.put(lessonViewDTO.getWeek().getId(), lessonViewDTO.getDate(), lessonViewDTO);
//        });
//        return schedule;
//    }


//    private Set<Long > getAllWeeksIdByTeacher(int year, Month month, Long teacherId) {
//       // List<Lesson> lessons = lessonRepository.findAllLessonByWeekAndTeacherId(week, teacherId);
//        return createOneMonthSchedule(year, month, teacherId).columnKeySet();
//
//    }
//
//
//    public Map<String, String> getWeekDaysForLessonViewDTO(int year, int month, Long teacherId) {
//        Table< LocalTime, LocalDate,LessonViewDTO> schedule = createSchedule(year, month, teacherId);
//        Map<LocalDate, Map<LocalTime, LessonViewDTO>> courseKeyUniversitySeatMap
//                = schedule.columnMap();
//
//
//        //Table, String, Integer > universityCourseSeatTable = TreeBasedTable.create();
//
//        lessonRepository.findAllLessonByWeekAndTeacherId(teacherId);
//    }


}
