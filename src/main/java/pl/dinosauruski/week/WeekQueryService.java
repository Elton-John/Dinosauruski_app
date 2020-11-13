package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.models.Week;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WeekQueryService {
    private WeekRepository weekRepository;

    public Week getOneOrThrow(Integer year, Integer numberOfWeek, Long teacherId) {
        return weekRepository.findByYearAndNumberAndTeacherId(year, numberOfWeek, teacherId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Boolean checkIsGenerated(Integer year, Integer numberOfWeek, Long teacherId) {
        Week week = getOneOrThrow(year, numberOfWeek, teacherId);
        return week.getGenerated();
    }

    public Boolean checkCurrentWeekIsGenerated(Long teacherId) {
        int weekOfYear = getCurrentNumberOfWeek();
        int year = LocalDate.now().getYear();
        return checkIsGenerated(year, weekOfYear, teacherId);
    }

    public List<Week> getAllGeneratedWeeksInFuture(Teacher teacher) {
        return weekRepository.findAllGeneratedInFuture(getCurrentYear(), getCurrentNumberOfWeek(), teacher.getId());
    }

    public int getCurrentNumberOfWeek() {
        LocalDate now = LocalDate.now();
        return now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    public Boolean checkWeekIsGenerated(int year, int nextNumberOfWeek, Long teacherId) {
        Week week = getOneOrThrow(year, nextNumberOfWeek, teacherId);
        return week.getGenerated();
    }

    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public LocalDate getDateByNumberOfWeekAndDayName(int year, int week, String dayName) {
        YearWeek yw = YearWeek.of(year, week);
        return yw.atDay(DayOfWeek.valueOf(dayName));

    }

    public int getNumberOfWeekByDate(LocalDate date) {
        return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    public boolean checkMonthIsGenerated(int year, int month, Long teacherId) {
        boolean result = true;
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        int numberOfFirstWeek = getNumberOfWeekByDate(firstDay);
        int numberOfLastWeek = getNumberOfWeekByDate(lastDay);

        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
            Boolean isGenerated = checkIsGenerated(year, i, teacherId);
            if (!isGenerated) {
                return false;
            }


        }
        return true;
    }
}