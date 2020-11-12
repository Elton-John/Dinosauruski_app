package pl.dinosauruski.week;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Week;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {

    @Query("SELECT w FROM Week w WHERE w.year = :year AND w.numberOfWeek = :number and w.teacher.id = :id")
    Optional<Week> findByYearAndNumberAndTeacherId(@Param("year") Integer year,
                                                   @Param("number") Integer number,
                                                   @Param("id") Long id);

    @Query("SELECT w FROM Week w WHERE w.year = :year AND w.teacher.id = :id AND w.isGenerated = true AND w.numberOfWeek >=:thisWeek")
    List<Week> findAllGeneratedWeeksByYearAndTeacherId(@Param("year") int year,
                                                       @Param("id") Long id,
                                                       @Param("thisWeek") int thisWeek);
}