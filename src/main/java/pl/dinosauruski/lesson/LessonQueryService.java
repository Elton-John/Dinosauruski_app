package pl.dinosauruski.lesson;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;
import pl.dinosauruski.week.WeekQueryService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LessonQueryService {
    private LessonRepository lessonRepository;
    private WeekQueryService weekQueryService;

    public Lesson getOneOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonDTO getOneLessonDtoOrThrow(Long id) {
        return lessonRepository.findOneLessonDTO(id).orElseThrow(EntityNotFoundException::new);
    }

    public LessonCompletionDTO getOneLessonCompletionDtoOrThrow(Long id) {
        return lessonRepository.findOneCompletionDto(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Lesson> getAllThisWeekLessonsByTeacher(Long id) {
        //  ZoneId zoneId = ZoneId.of("Europe/Warsaw" );
        LocalDate today = LocalDate.now();
        LocalDate thisMondayDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisSundayDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return lessonRepository.findAllThisWeekLessonsByTeacher(id, thisMondayDate, thisSundayDate);
    }

    public List<Lesson> getGeneratedLessonsOfWeek(int year, int nextNumberOfWeek, Long teacherId) {
        Week week = weekQueryService.getOneOrThrow(year, nextNumberOfWeek, teacherId);
        return lessonRepository.findAllLessonByWeekAndTeacherId(week, teacherId);
    }
}
