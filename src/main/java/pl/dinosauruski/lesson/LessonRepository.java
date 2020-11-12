package pl.dinosauruski.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonDTO(l.id,l.date, l.slot, l.teacher, l.student) FROM Lesson l WHERE l.id = :id")
    Optional<LessonDTO> findOneLessonDTO(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonCompletionDTO(l.id,l.completed, l.cancelled, l.lastMinuteCancelled, l.transferred) FROM Lesson l WHERE l.id = :id")
    Optional<LessonCompletionDTO> findOneCompletionDto(@Param("id") Long id);

    @Query("SELECT l FROM Lesson l WHERE l.teacher.id = :id AND l.date BETWEEN :thisMondayDate AND :thisSundayDate")
    List<Lesson> findAllThisWeekLessonsByTeacher(@Param("id") Long id,
                                                 @Param("thisMondayDate") LocalDate thisMondayDate,
                                                 @Param("thisSundayDate") LocalDate thisSundayDate);
}
