package pl.dinosauruski.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {


}
