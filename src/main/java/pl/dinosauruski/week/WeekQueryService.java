package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;
import pl.dinosauruski.models.Week;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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


    public List<Week> getAllGeneratedWeeksAfterDate(Long teacherId, LocalDate date) {
        YearWeek yw = YearWeek.from(date);
        int numberOfWeek = yw.getWeek();
        int year = yw.getYear();
        LocalDate mondayDate = getDateByNumberOfWeekAndDayName(year, numberOfWeek, DayOfWeek.MONDAY.name());
        return weekRepository.findAllGeneratedWeeksAfterDate(mondayDate, teacherId);
    }


    public int getCurrentNumberOfWeek() {
        LocalDate now = LocalDate.now();
        return now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }


    public LocalDate getDateByNumberOfWeekAndDayName(int year, int week, String dayName) {
        YearWeek yw = YearWeek.of(year, week);
        LocalDate date = yw.atDay(DayOfWeek.valueOf(dayName));
        return date;

    }


    public List<LocalDate> getAllMondaysOfMonth(int year, int month) {
        List<LocalDate> datesOfMonth = getDatesOfMonth(year, month);
        List<LocalDate> mondays = datesOfMonth.stream()
                .filter(localDate -> localDate.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .collect(Collectors.toList());

        LocalDate firstDay = LocalDate.of(year, month, 1);
        if (!firstDay.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            LocalDate m = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            mondays.add(m);
        }
        mondays.sort(LocalDate::compareTo);
        return mondays;
    }


    public List<LocalDate> getDatesOfMonth(int year, int month) {
        boolean isLeap = Year.isLeap(year);
        int length = Month.of(month).length(isLeap);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            LocalDate date = LocalDate.of(year, month, i);
            dates.add(date);
        }
        return dates;
    }


    public Set<Week> getAllWeeksOfMonthYear(Month month, int year, Long teacherId) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        YearWeek fyw = YearWeek.from(firstDay);
        int numberOfFirstWeek = fyw.getWeek();
        int yearOfFirstWeek = fyw.getYear();
        Optional<Week> firstWeek = weekRepository.findByNumberAndByYearAndByTeacherId(numberOfFirstWeek, yearOfFirstWeek, teacherId);
        Set<Week> weeks = new HashSet<>();
        firstWeek.ifPresent(weeks::add);

        List<LocalDate> allMondaysOfMonth = getAllMondaysOfMonth(year, month.getValue());
        allMondaysOfMonth.forEach(localDate -> {
            YearWeek yw = YearWeek.from(localDate);
            int foundNumberOfWeek = yw.getWeek();
            int foundYear = yw.getYear();
            Optional<Week> foundWeek = weekRepository.findByNumberAndByYearAndByTeacherId(foundNumberOfWeek, foundYear, teacherId);
            foundWeek.ifPresent(weeks::add);
        });

        return weeks;

    }


    public boolean checkMonthIsGenerated(int year, int month, Long teacherId) {
        Set<Week> weeks = getAllWeeksOfMonthYear(Month.of(month), year, teacherId);
        if (weeks.size() == 0) {
            return false;
        }
        for (Week week : weeks) {
            if (!week.getGenerated()) {
                return false;
            }
        }
        return true;
    }
}