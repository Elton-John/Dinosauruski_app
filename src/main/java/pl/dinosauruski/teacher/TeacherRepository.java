package pl.dinosauruski.teacher;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Teacher;
import pl.dinosauruski.teacher.dto.TeacherEditDTO;

import java.util.Optional;

@Repository
 interface TeacherRepository extends JpaRepository<Teacher, Long> {


    Teacher findByEmail(String email);


    @Query("SELECT new pl.dinosauruski.teacher.dto.TeacherEditDTO(t.id,t.name, t.surname, t.nickname) FROM Teacher t WHERE t.id = :id")
    Optional<TeacherEditDTO> findEditableDataById(@Param("id") Long id);

}
