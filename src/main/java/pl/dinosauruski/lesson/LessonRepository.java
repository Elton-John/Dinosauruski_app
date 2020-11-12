package pl.dinosauruski.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dinosauruski.lesson.dto.LessonCompletionDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonDTO(l.id,l.date, l.slot, l.student, l.week)" +
            " FROM Lesson l WHERE l.id = :id")
    Optional<LessonDTO> findOneLessonDTO(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonCompletionDTO(l.id,l.completed, l.cancelled, l.lastMinuteCancelled, l.transferred)" +
            " FROM Lesson l WHERE l.id = :id")
    Optional<LessonCompletionDTO> findOneCompletionDto(@Param("id") Long id);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.date BETWEEN :thisMondayDate AND :thisSundayDate")
    List<Lesson> findAllThisWeekLessonsByTeacher(@Param("id") Long id,
                                                 @Param("thisMondayDate") LocalDate thisMondayDate,
                                                 @Param("thisSundayDate") LocalDate thisSundayDate);


    @Query("SELECT l FROM Lesson l WHERE l.week=:week AND l.slot.teacher.id = :id")
    List<Lesson> findAllLessonByWeekAndTeacherId(@Param("week") Week week,
                                                 @Param("id") Long teacherId);
}
