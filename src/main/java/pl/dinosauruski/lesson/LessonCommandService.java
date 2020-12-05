package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.*;
import pl.dinosauruski.rebooking.RebookingCommandService;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.week.WeekCommandService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
  //  private RebookingCommandService rebookingCommandService;
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
                break;
            default:
                result = false;
        }
        lessonRepository.save(lesson);
        studentWhoPaid.ifPresent(student ->
                updatePaymentForStudent(student.getId(), lesson.getSlot().getTeacher().getId()));
        return result;
    }


    public void delete(Long lessonId, Long teacherId) {
        Lesson lesson = lessonQueryService.getOneOrThrow(lessonId);
        Optional<Student> studentWhoPaid = savePaymentForStudentBeforeDeleteOrUpdateLesson(lesson);

        lesson.setSlot(null);
        lesson.setPayment(null);
        lesson.setWeek(null);
        lessonRepository.deleteById(lessonId);
        studentWhoPaid.ifPresent(student ->
                updatePaymentForStudent(student.getId(), teacherId));
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
        slots.stream().filter(Slot::isBooked)
                .forEach(slot -> {
                    Long studentId = slot.getRegularStudent().getId();
                    updatePaymentForStudent(studentId, teacherId);
                });
    }


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
    }




    public Optional<Student> savePaymentForStudentBeforeDeleteOrUpdateLesson(Lesson lesson) {
        Student studentWhoPaid = null;
        if (lesson.isPaid()) {
            if (lesson.isRebooked()) {
                Student notRegularStudent = lesson.getRebooking().getNotRegularStudent();
                BigDecimal overpayment = notRegularStudent.getOverpayment();
                notRegularStudent.setOverpayment(overpayment.add(lesson.getAddedPayment()));

                studentWhoPaid = notRegularStudent;
            } else {
                Student regularStudent = lesson.getSlot().getRegularStudent();
                BigDecimal overpayment = regularStudent.getOverpayment();
                regularStudent.setOverpayment(overpayment.add(lesson.getAddedPayment()));
                studentWhoPaid = regularStudent;
            }
        }
        lesson.setPaid(false);
        lesson.setRequiredPayment(true);
        lesson.setAddedPayment(BigDecimal.valueOf(0));
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
                    lesson.setAddedPayment(priceForOneLesson);
                    overpayment = overpayment.subtract(priceForOneLesson);
                    regularStudent.setOverpayment(overpayment);
                    i++;
                    if (notPaidLessons.size() <= i) {
                        break;
                    }

                }
            }
        }
    }

}
