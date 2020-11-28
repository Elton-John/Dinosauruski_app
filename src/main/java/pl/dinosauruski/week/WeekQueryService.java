package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.models.Week;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoField;
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

    public List<LocalDate> getAllMondaysOfMonth(int year, int month) {
        List<LocalDate> datesOfMonth = getDatesOfMonth(year, month);
        return datesOfMonth.stream()
                .filter(localDate -> localDate.getDayOfWeek().equals(DayOfWeek.MONDAY))
                .collect(Collectors.toList());
    }

    public List<LocalDate> getDatesOfMonth(int year, int month) {
        boolean isLeap = Year.isLeap(year);
        int length = Month.of(month).length(isLeap);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            LocalDate date = LocalDate.of(year, month, i);
            dates.add(date);
            date = date.plusDays(1);
        }
        return dates;
    }

//    public int getIdOfWeekByDate(LocalDate date, Long teacherId) {
//        YearWeek yw = YearWeek.from(date);
//        int weekOfYear = yw.getWeek();
//        int year = yw.getYear();
//        return weekRepository.findByNumberAndByYearAndByTeacherId(weekOfYear, year, teacherId);
//
//    }

    public Set<Week> getAllWeeksOfMonthYear(Month month, int year, Long teacherId) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        YearWeek fyw = YearWeek.from(firstDay);
        int numberOfFirstWeek = fyw.getWeek();
        int yearOfFirstWeek = fyw.getYear();
        Week firstWeek = weekRepository.findByNumberAndByYearAndByTeacherId(numberOfFirstWeek, yearOfFirstWeek, teacherId);
        Set<Week> weeks = new HashSet<>();
        weeks.add(firstWeek);


        List<LocalDate> allMondaysOfMonth = getAllMondaysOfMonth(year, month.getValue());
        allMondaysOfMonth.forEach(localDate -> {
            YearWeek yw = YearWeek.from(localDate);
            int foundNumberOfWeek = yw.getWeek();
            int foundYear = yw.getYear();
            Week foundWeek = weekRepository.findByNumberAndByYearAndByTeacherId(foundNumberOfWeek, foundYear, teacherId);
            weeks.add(foundWeek);
        });

        return weeks;

//        YearMonth ym = YearMonth.of(year, month);
//        LocalDate firstDay = ym.atDay(1);
//        YearWeek fyw = YearWeek.from(firstDay);
//        int firstWeek = fyw.getWeek();
//        int year = yw.getYear();
//
//        LocalDate lastDay = ym.atEndOfMonth();
//        weekRepository.findAllWhere

    }

    public boolean checkMonthIsGenerated(int year, int month, Long teacherId) {
        //boolean result = true;
//        YearMonth ym = YearMonth.of(year, month);
//        LocalDate firstDay = ym.atDay(1);
//        LocalDate lastDay = ym.atEndOfMonth();
//        int numberOfFirstWeek = getIdOfWeekByDate(firstDay, teacherId);
//        int numberOfLastWeek = getIdOfWeekByDate(lastDay, teacherId);
//
//        for (int i = numberOfFirstWeek; i <= numberOfLastWeek; i++) {
//            Boolean isGenerated = checkIsGenerated(year, i, teacherId);
//            if (!isGenerated) {
//                return false;
//            }
//
//
//        }
//        return true;

        Set<Week> weeks = getAllWeeksOfMonthYear(Month.of(month), year, teacherId);
        for (Week week : weeks) {
            if (!week.getIsGenerated()) {
                return false;
            }
        }
        return true;
    }
}