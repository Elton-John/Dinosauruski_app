package pl.dinosauruski.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dinosauruski.lesson.dto.LessonDTO;
import pl.dinosauruski.models.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


    @Query("SELECT new pl.dinosauruski.lesson.dto.LessonDTO(l.id,l.date, l.slot, l.week)" +
            " FROM Lesson l WHERE l.id = :id")
    Optional<LessonDTO> findOneLessonDTO(@Param("id") Long id);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.requiredPayment=true AND l.paid = FALSE order by l.date ASC ")
    List<Lesson> findAllByTeacherIdWherePaidIsFalseAndCancelledIsFalse(@Param("id") Long teacherId);


    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.cancelledByTeacher = false AND l.date BETWEEN :start AND :end ORDER BY l.date")
    List<Lesson> findAllByTeacherInMonth(@Param("start") LocalDate firstDay,
                                         @Param("end") LocalDate lastDay,
                                         @Param("teacherId") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :id AND l.date = :date")
    List<Lesson> findAllByDateAndTeacherId(@Param("date") LocalDate localDate,
                                           @Param("id") Long teacherId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.id = :id AND l.date >= :date")
    List<Optional<Lesson>> findAllGeneratedLessonsBySlotWhereDateIsAfter(@Param("date") LocalDate date,
                                                                         @Param("id") Long slotId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.slot.id = :id")
    int findAllGeneratedLessonsBySlot(@Param("id") Long slotId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id = :teacherId AND l.slot.regularStudent.id = :studentId AND  l.rebooked = false AND l.paid = true ")
    List<Lesson> findAllPaidLessonsByRegularStudent(@Param("teacherId") Long teacherId,
                                                    @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE  l.slot.teacher.id = :teacherId AND l.rebooked = true AND l.rebooking.notRegularStudent.id = :studentId AND l.paid = true")
    List<Lesson> findAllPaidLessonsByNotRegularStudent(@Param("teacherId") Long teacherId,
                                                       @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.slot.regularStudent.id = :studentId AND l.cancelledByTeacher = false AND l.cancelledByStudent = false AND l.date BETWEEN :start AND :end ORDER BY l.date")
    List<Lesson> findAllNotCancelledByTeacherAndStudentInMonth(@Param("start") LocalDate firstDay,
                                                               @Param("end") LocalDate lastDay,
                                                               @Param("teacherId") Long teacherId,
                                                               @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId  AND l.cancelledByTeacher = false AND  l.rebooked = true AND l.rebooking.notRegularStudent.id = :studentId AND  l.date BETWEEN :start AND :end")
    List<Lesson> findAllRebookedByTeacherAndStudentInMonth(@Param("start") LocalDate firstDay,
                                                           @Param("end") LocalDate lastDay,
                                                           @Param("teacherId") Long teacherId,
                                                           @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId  AND l.cancelledByTeacher = false AND  l.rebooked = true AND l.rebooking.notRegularStudent.id = :studentId AND  l.date >= :start")
    List<Optional<Lesson>> findAllRebookedByTeacherAndStudentWhereDateIsAfter(@Param("start") LocalDate firstDay,
                                                             @Param("teacherId") Long teacherId,
                                                             @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId AND l.slot.regularStudent.id = :studentId AND l.cancelledByTeacher = false AND l.cancelledByStudent = false AND l.date <= :end ORDER BY l.date")
    List<Lesson> findAllNotCancelledByTeacherAndStudentUntilNextMonth(@Param("end") LocalDate lastDay,
                                                                      @Param("teacherId") Long teacherId,
                                                                      @Param("studentId") Long studentId);

    @Query("SELECT l FROM Lesson l WHERE l.slot.teacher.id =:teacherId  AND l.cancelledByTeacher = false AND  l.rebooked = true AND l.rebooking.notRegularStudent.id = :studentId AND  l.date  <= :end")
    List<Lesson> findAllRebookedByTeacherAndStudentUntilNextMonth(@Param("end") LocalDate lastDay,
                                                                  @Param("teacherId") Long teacherId,
                                                                  @Param("studentId") Long studentId);
}
