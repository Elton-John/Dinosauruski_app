package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.*;
import pl.dinosauruski.rebooking.RebookingCommandService;
import pl.dinosauruski.slot.SlotQueryService;
import pl.dinosauruski.week.WeekCommandService;
import pl.dinosauruski.week.WeekQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

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

    public void updateCompletion(LessonCompletionDTO lessonCompletionDTO) {
        Lesson lesson = lessonQueryService.getOneOrThrow(lessonCompletionDTO.getId());
        lesson.setCancelledByTeacher(lessonCompletionDTO.isCancelledByTeacher());
        lesson.setCancelledByStudent(lessonCompletionDTO.isCancelledByStudent());
        lesson.setLastMinuteCancelled(lessonCompletionDTO.isLastMinuteCancelled());
        if (lesson.isCancelledByStudent() || lesson.isCancelledByTeacher()) {
            lesson.setRequiredPayment(false);
        }
        lessonRepository.save(lesson);
    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }

    public void generateWeekLessonsForTeacher(int year, int week, Long teacherId) {
        List<Slot> slots = slotQueryService.showAllSlotsByTeacherId(teacherId);
        slots.forEach(slot -> {
            if (slot.getBooked()) {
                LessonDTO lessonDTO = new LessonDTO();
                lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(year, week, slot.getDayOfWeek().name()));
                lessonDTO.setSlot(slot);
                lessonDTO.setWeek(weekQueryService.getOneOrThrow(year, week, teacherId));
                create(lessonDTO);
            }
        });
        weekCommandService.setGenerated(week, year, teacherId);
    }

    public void generateFutureLessonsForStudent(Slot slot, Long studentId) {
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(slot.getTeacher());
        generateAdditionalLessons(weeks, slot, studentId);
    }

    private void generateAdditionalLessons(List<Week> weeks, Slot slot, Long studentId) {
        weeks.forEach(week -> {
            LessonDTO lessonDTO = new LessonDTO();
            lessonDTO.setDate(weekQueryService.getDateByNumberOfWeekAndDayName(
                    week.getYear(),
                    week.getNumberOfWeek(),
                    slot.getDayOfWeek().name()));
            lessonDTO.setSlot(slot);
            lessonDTO.setWeek(week);
            if (lessonDTO.getDate().isAfter(LocalDate.now())) {
                create(lessonDTO);
            }
        });
    }

    public void removeGeneratedLessons(Slot slot) {
        LocalDate today = LocalDate.now();
        Student student = slot.getRegularStudent();
        Teacher teacher = slot.getTeacher();
        List<Week> weeks = weekQueryService.getAllGeneratedWeeksInFuture(teacher);

        weeks.forEach(week -> {
            week.getLessons().stream()
                    .filter(lesson -> lesson.getSlot().getId().equals(slot.getId()))
                    .filter(lesson -> lesson.getSlot().getRegularStudent().getId().equals(slot.getRegularStudent().getId()))
                    .filter(lesson -> lesson.getDate().isAfter(today))
                    .filter(lesson -> !lesson.isRebooked())
                    .forEach(lesson -> delete(lesson.getId()));
        });

    }

    public void generateMonthLessonsForTeacher(int year, int month, Long teacherId) {

        if (month == 12) {
            int newYear = year + 1;
            weekCommandService.generateWeeksOnesInYear(newYear, teacherId);
        }
//        if (month==1){
//
//        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        int numberOfFirstWeek = weekQueryService.getNumberOfWeekByDate(firstDay);
        int numberOfLastWeek = weekQueryService.getNumberOfWeekByDate(lastDay);

        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
            Boolean isGenerated = weekQueryService.checkIsGenerated(year, i, teacherId);
            if (!isGenerated) {
                generateWeekLessonsForTeacher(year, i, teacherId);
            }
        }
    }

    public void cancelBookingOnceFreeLesson(Long id) {
        Lesson lesson = lessonQueryService.getOneOrThrow(id);
        lesson.setRebooked(false);
        Rebooking rebooking = lesson.getRebooking();
        rebookingCommandService.delete(rebooking);
        lesson.setRequiredPayment(false);
        lessonRepository.save(lesson);
    }

    public void addPayment(Payment payment, Long teacherId) {
        Student student = payment.getStudent();
        BigDecimal priceForOneLesson = student.getPriceForOneLesson();
        BigDecimal sum = payment.getSum();
        BigDecimal countPaidLessons = sum.divide(priceForOneLesson, 2, RoundingMode.HALF_UP);
        int count = countPaidLessons.intValue();
        List<LessonPaymentDTO> notPaidLessons = lessonQueryService.getNotPaidLessonsByStudent(teacherId, student.getId());
        for (int i = 0; i < count; i++) {
            LessonPaymentDTO lessonPaymentDTO = notPaidLessons.get(i);
            Lesson lesson = lessonQueryService.getOneOrThrow(lessonPaymentDTO.getId());
            lesson.setPayment(payment);
            lesson.setPaid(true);
            lesson.setRequiredPayment(false);
            lessonRepository.save(lesson);
        }
    }
}
