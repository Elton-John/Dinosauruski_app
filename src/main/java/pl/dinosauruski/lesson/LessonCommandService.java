package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.*;
import pl.dinosauruski.payment.PaymentCommandService;
import pl.dinosauruski.rebooking.RebookingCommandService;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.week.WeekCommandService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class LessonCommandService {
    private LessonRepository lessonRepository;
    private LessonQueryService lessonQueryService;
    private SlotQueryService slotQueryService;
    private WeekCommandService weekCommandService;
    private WeekQueryService weekQueryService;
    private RebookingCommandService rebookingCommandService;
    private StudentQueryService studentQueryService;


    public void create(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setDate(lessonDTO.getDate());
        lesson.setSlot(lessonDTO.getSlot());
        lesson.setWeek(lessonDTO.getWeek());
        lesson.setCompleted(false);
        lesson.setCancelledByTeacher(false);
        lesson.setCancelledByStudent(false);
        lesson.setLastMinuteCancelled(false);
        lesson.setRebooked(false);
        lesson.setArchived(false);
        lesson.setRequiredPayment(true);
        lessonRepository.save(lesson);
    }

    public boolean cancelling(Long lessonId, String cancel) {
        boolean result = true;
        Lesson lesson = lessonQueryService.getOneOrThrow(lessonId);
        Optional<Student> studentWhoPaid = savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);
        lesson.setCancelledByTeacher(false);
        lesson.setCancelledByStudent(false);
        lesson.setLastMinuteCancelled(false);
        lesson.setRequiredPayment(false);
        switch (cancel) {
            case "byTeacher":
                lesson.setCancelledByTeacher(true);
                break;
            case "byStudent":
                lesson.setCancelledByStudent(true);
                break;
            case "lastMinute":
                lesson.setLastMinuteCancelled(true);
                lesson.setRequiredPayment(true);
            default:
                result = false;
        }
        lessonRepository.save(lesson);
        studentWhoPaid.ifPresent(student ->
                updatePaymentForStudent(student.getId(), lesson.getSlot().getTeacher().getId()));
        return result;
    }


//    public void updateCancelling(LessonCancellingDTO lessonCancellingDTO) {
//        Lesson lesson = lessonQueryService.getOneOrThrow(lessonCancellingDTO.getId());
//        lesson.setCancelledByTeacher(lessonCancellingDTO.isCancelledByTeacher());
//        lesson.setCancelledByStudent(lessonCancellingDTO.isCancelledByStudent());
//        lesson.setLastMinuteCancelled(lessonCancellingDTO.isLastMinuteCancelled());
//        if (lesson.isCancelledByStudent() || lesson.isCancelledByTeacher()) {
//            lesson.setRequiredPayment(false);
//            lesson.setPaid(false);
//
//            Long studentId = lesson.getSlot().getRegularStudent().getId();
//            Long teacherId = lesson.getSlot().getTeacher().getId();
//            Payment payment = lesson.getPayment();
//            if (payment != null) {
//                Optional<Lesson> OptionalLessonToPay = lessonQueryService.getNextNotPaidLesson(teacherId, studentId);
//
//                if (OptionalLessonToPay.isPresent()) {
//                    Lesson lessonToPay = OptionalLessonToPay.get();
//                    lessonToPay.setPayment(payment);
//                    lessonToPay.setPaid(true);
//                    lessonToPay.setRequiredPayment(false);
//                } else {
//                    BigDecimal overPayment = payment.getOverPayment();
//                    payment.setOverPayment(overPayment.add(lesson.getSlot().getRegularStudent().getPriceForOneLesson()));
//                }
//            }
//            lesson.setPayment(null);
//        }
//        lessonRepository.save(lesson);
//    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }

    public void generateWeekLessonsForTeacher(Week week, Long teacherId) {
        List<Slot> slots = slotQueryService.getAllSlotsByTeacherId(teacherId);
        slots.forEach(slot -> {
            if (slot.isBooked()) {
                LessonDTO lessonDTO = new LessonDTO();
                lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(week.getYear(), week.getNumberOfWeek(), slot.getDayOfWeek().name()));
                lessonDTO.setSlot(slot);
                lessonDTO.setWeek(week);
                create(lessonDTO);
            }
        });
        weekCommandService.setGenerated(week);
    }

//    public void generateFutureLessonsForStudent(Slot slot, Long studentId) {
//        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(slot.getTeacher());
//        generateAdditionalLessons(weeks, slot, studentId);
//    }

//    private void generateAdditionalLessons(List<Week> weeks, Slot slot, Long studentId) {
//        weeks.forEach(week -> {
//            LessonDTO lessonDTO = new LessonDTO();
//            lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(
//                    week.getYear(),
//                    week.getNumberOfWeek(),
//                    slot.getDayOfWeek().name()));
//            lessonDTO.setSlot(slot);
//            lessonDTO.setWeek(week);
//            if (lessonDTO.getDate().isAfter(LocalDate.now())) {
//                create(lessonDTO);
//            }
//        });
//    }

//    public void removeGeneratedLessons(Slot slot) {
//        LocalDate today = LocalDate.now();
//        Student student = slot.getRegularStudent();
//        Teacher teacher = slot.getTeacher();
//        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(teacher);
//
//        weeks.forEach(week -> {
//            week.getLessons().stream()
//                    .filter(lesson -> lesson.getSlot().getId().equals(slot.getId()))
//                    .filter(lesson -> lesson.getSlot().getRegularStudent().getId().equals(slot.getRegularStudent().getId()))
//                    .filter(lesson -> lesson.getDate().isAfter(today))
//                    .filter(lesson -> !lesson.isRebooked())
//                    .forEach(lesson -> delete(lesson.getId()));
//        });
//
//    }

    public void generateMonthLessonsForTeacher(int year, int month, Long teacherId) {
        weekCommandService.generateWeeksOnesInYear(year, teacherId);
        if (month == 12) {
            int newYear = year + 1;
            weekCommandService.generateWeeksOnesInYear(newYear, teacherId);
        }
        if (month == 1) {
            int previousYear = year - 1;
            weekCommandService.generateWeeksOnesInYear(previousYear, teacherId);
        }
        Set<Week> weeks = weekQueryService.getAllWeeksOfMonthYear(Month.of(month), year, teacherId);
        for (Week week : weeks) {
            if (!week.getGenerated()) {
                generateWeekLessonsForTeacher(week, teacherId);
            }
        }

//
//        YearMonth ym = YearMonth.of(year, month);
//        LocalDate firstDay = ym.atDay(1);
//        LocalDate lastDay = ym.atEndOfMonth();
//        int numberOfFirstWeek = weekQueryService.getIdOfWeekByDate(firstDay,teacherId);
//        int numberOfLastWeek = weekQueryService.getIdOfWeekByDate(lastDay,teacherId);
//
//        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
//            Boolean isGenerated = weekQueryService.checkIsGenerated(year, i, teacherId);
//            if (!isGenerated) {
//                generateWeekLessonsForTeacher(year, i, teacherId);
//            }
//        }
    }

    public void cancelBookingOnceFreeLesson(Long id) {
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        lesson.setRebooked(false);
        Rebooking rebooking = lesson.getRebooking();
        rebookingCommandService.delete(rebooking);
        lesson.setRequiredPayment(false);
        lessonRepository.save(lesson);
    }

    public void saveLesson(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public Optional<Student> savePaymentForStudentBeforeDeleteOrUpdateLesson(Lesson lesson) {
        Student studentWhoPaid = null;
        if (lesson.isPaid()) {
            if (lesson.isRebooked()) {
                Student notRegularStudent = lesson.getRebooking().getNotRegularStudent();
                notRegularStudent.setOverpayment(notRegularStudent.getPriceForOneLesson());
                studentWhoPaid = notRegularStudent;
            } else {
                Student regularStudent = lesson.getSlot().getRegularStudent();
                regularStudent.setOverpayment(regularStudent.getPriceForOneLesson());
                studentWhoPaid = regularStudent;
            }
        }
        lesson.setPaid(false);
        lesson.setRequiredPayment(true);
        // lesson.setPayment(null);
        lessonRepository.save(lesson);
        return Optional.ofNullable(studentWhoPaid);
    }

    public void generateLessonsBySlotForWeeks(Long slotId, List<Week> weeks, Long teacherId) {
        Slot slot = slotQueryService.getOneOrThrow(slotId);
        weeks.forEach(week -> {
            Lesson lesson = new Lesson();
            LocalDate date = weekQueryService.getDateByNumberOfWeekAndDayName(week.getYear(), week.getNumberOfWeek(), slot.getDayOfWeek().name());
            lesson.setDate(date);
            lesson.setSlot(slot);
            lesson.setWeek(week);
            lesson.setCompleted(false);
            lesson.setCancelledByTeacher(false);
            lesson.setCancelledByStudent(false);
            lesson.setLastMinuteCancelled(false);
            lesson.setRebooked(false);
            lesson.setArchived(false);
            lesson.setRequiredPayment(true);
            lessonRepository.save(lesson);
        });

        updatePaymentForStudent(slot.getRegularStudent().getId(), teacherId);
    }

    public void updatePaymentForStudent(Long studentId, Long teacherId) {
        Student regularStudent = studentQueryService.getOneOrThrow(studentId);
        BigDecimal overpayment = regularStudent.getOverpayment();
        BigDecimal priceForOneLesson = regularStudent.getPriceForOneLesson();

        if (overpayment.compareTo(priceForOneLesson) >= 0) {
            List<LessonPaymentDTO> notPaidLessons = lessonQueryService.getNotPaidLessonsByStudent(teacherId, regularStudent.getId());
            if (notPaidLessons.size() > 0) {
                int i = 0;
                while (overpayment.compareTo(priceForOneLesson) >= 0) {
                    Lesson lesson = lessonQueryService.getOneOrThrow(notPaidLessons.get(i).getId());
                    lesson.setPaid(true);
                    lesson.setRequiredPayment(false);
                    overpayment = overpayment.subtract(priceForOneLesson);
                    regularStudent.setOverpayment(overpayment);
                    i++;
                }

            }
        }
    }


}
