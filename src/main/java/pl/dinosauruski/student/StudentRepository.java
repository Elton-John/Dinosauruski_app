package pl.dinosauruski.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.student.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.active = TRUE")
    List<Student> findAllIfActive();

    @Query("SELECT s FROM Student s   JOIN FETCH s.teachers t WHERE t.id = :id")
    List<Student> findAllByTeachersContainsTeacher(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.student.dto.StudentDTO(s.id, s.name, s.surname, s.email ,s.priceForOneLesson, s.overpayment) " +
            "FROM Student s WHERE s.id = :id")
    Optional<StudentDTO> findOneStudentDtoById(@Param("id") Long id);

}
