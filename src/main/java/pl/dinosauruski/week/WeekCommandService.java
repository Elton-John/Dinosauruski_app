package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.models.YearForTeacher;
import pl.dinosauruski.teacher.TeacherQueryService;
import pl.dinosauruski.year.YearCommandService;
import pl.dinosauruski.year.YearQueryService;

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
    private YearQueryService yearQueryService;
    private YearCommandService yearCommandService;


    public void generateWeeksOnesInYear(int year, Long teacherId) {
        boolean yearExist = yearQueryService.checkYearExist(year, teacherId);
        if (!yearExist) {
            yearCommandService.create(year, teacherId);
        }
        YearForTeacher yearForTeacher = yearQueryService.getYearForTeacher(year, teacherId);
        if (!yearForTeacher.getIsGenerated()) {

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
            yearCommandService.setGenerated(yearForTeacher);
        }

    }


    public void setGenerated(Week week) {
        week.setGenerated(true);
        weekRepository.save(week);
    }
}
