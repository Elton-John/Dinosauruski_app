package pl.dinosauruski.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dinosauruski.lesson.dto.LessonCancellingDTO;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Week;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonDTO(l.id,l.date, l.slot, l.week)" +
            " FROM Lesson l WHERE l.id = :id")
    Optional<LessonDTO> findOneLessonDTO(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonCancellingDTO(l.id, l.cancelledByTeacher, l.cancelledByStudent, l.lastMinuteCancelled)" +
            " FROM Lesson l WHERE l.id = :id")
    Optional<LessonCancellingDTO> findOneCompletionDto(@Param("id") Long id);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.date BETWEEN :thisMondayDate AND :thisSundayDate")
    List<Lesson> findAllThisWeekLessonsByTeacher(@Param("id") Long id,
                                                 @Param("thisMondayDate") LocalDate thisMondayDate,
                                                 @Param("thisSundayDate") LocalDate thisSundayDate);


    @Query("SELECT l FROM Lesson l WHERE l.week=:week AND l.slot.teacher.id = :id")
    List<Lesson> findAllLessonByWeekAndTeacherId(@Param("week") Week week,
                                                 @Param("id") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.regularStudent.id = :studentId OR l.rebooking.notRegularStudent=:studentId AND l.slot.teacher.id = :teacherId AND l.paid = false AND l.cancelledByStudent =false AND l.cancelledByTeacher=false ORDER BY l.date DESC ")
    List<Lesson> getNextNotPaidLessons(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.requiredPayment=true AND l.paid = FALSE order by l.date ASC ")
    List<Lesson> findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(@Param("id") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.slot.regularStudent.id = :studentId AND l.paid = true AND l.date BETWEEN :start ANd :end")
    List<Lesson> findAllByStudentAndTeacherWherePaidIsTrueThisMonth(@Param("start") LocalDate firstDay,
                                                                    @Param("end") LocalDate lastDay,
                                                                    @Param("teacherId") Long teacherId,
                                                                    @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.slot.regularStudent.id = :studentId AND l.paid = false AND l.requiredPayment=true  AND l.date BETWEEN :start ANd :end")
    List<Lesson> findAllByStudentAndTeacherWherePaidIsFalseNextMonth(@Param("start") LocalDate firstDay,
                                                                     @Param("end") LocalDate lastDay,
                                                                     @Param("teacherId") Long teacherId,
                                                                     @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.cancelledByTeacher = false AND l.date <= :end ORDER BY l.date ")
    List<Lesson> findAllByTeacherUntilLastDayOfNextMonth(
                                               @Param("end") LocalDate lastDay,
                                               @Param("teacherId") Long teacherId
                                             );
    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.cancelledByTeacher = false AND l.date BETWEEN :start AND :end ORDER BY l.date")
    List<Lesson> findAllByTeacherInMonth(@Param("start") LocalDate firstDay,
                                         @Param("end") LocalDate lastDay,
                                         @Param("teacherId") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.date = :date")
    List<Lesson> findAllByDateAndTeacherId(@Param("date") LocalDate localDate,
                                     @Param("id") Long teacherId);
}
