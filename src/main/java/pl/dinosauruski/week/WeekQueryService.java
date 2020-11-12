package pl.dinosauruski.week;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.models.Week;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class WeekQueryService {
    private WeekRepository weekRepository;
    private final LessonQueryService lessonQueryService;

    public Week getOneOrThrow(Integer year, Integer numberOfWeek, Long teacherId) {
        return weekRepository.findByYearAndNumberAndTeacherId(year, numberOfWeek, teacherId).orElseThrow(EntityNotFoundException::new);
    }


    public Boolean checkIsGenerated(Integer year, Integer numberOfWeek, Long teacherId) {
        Week week = getOneOrThrow(year, numberOfWeek, teacherId);
        return week.getGenerated();
    }

    public Boolean checkCurrentWeekIsGenerated(Long teacherId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int weekOfYear = now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return checkIsGenerated(year, weekOfYear, teacherId);
    }


    public List<Week> getAllGeneratedWeeksInFuture(Teacher teacher) {
        LocalDate today = LocalDate.now();
        int weekOfYear = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        int year = today.getYear();
        return weekRepository.findAllGeneratedWeeksByYearAndTeacherId(year, teacher.getId(), weekOfYear);
    }
}