package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;

@Service
@Transactional
@AllArgsConstructor
public class WeekCommandService {
    private WeekRepository weekRepository;
    private WeekQueryService weekQueryService;
    private TeacherQueryService teacherQueryService;

    public void generateWeeksOnesInYear(int year, Long teacherId) {
        LocalDate date = LocalDate.of(year, 1, 1);
        int weeksInYear = (int) IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
        for (int i = 1; i <= weeksInYear; i++) {
            Week week = new Week();
            week.setNumberOfWeek(i);
            week.setYear(year);
            week.setMondayDate(weekQueryService.getDateByNumberOfWeekAndDayName(year, i, DayOfWeek.MONDAY.name()));
            week.setTeacher(teacherQueryService.getOneOrThrow(teacherId));
            week.setGenerated(false);
            week.setArchived(false);
            weekRepository.save(week);
        }

    }

    public void setGenerated(Week week) {
        week.setGenerated(true);
        weekRepository.save(week);
    }
}
