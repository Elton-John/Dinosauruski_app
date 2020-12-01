package pl.dinosauruski.year;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.YearForTeacher;

import java.util.Optional;

@Repository
public interface YearRepository extends JpaRepository<YearForTeacher, Long> {

//    @Query("SELECT y FROM YearForTeacher y WHERE y.year = :year")
//    Optional<YearForTeacher> findByYear(@Param("year") int year);

    @Query("SELECT y FROM YearForTeacher y WHERE y.year = :year AND y.teacher.id = :id")
    Optional<YearForTeacher> findByYearAndTeacherId(@Param("year")int year,
                                                    @Param("id") Long teacherId);
}
