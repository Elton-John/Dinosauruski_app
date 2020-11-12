package pl.dinosauruski.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.slot.dto.SlotInfoDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :id AND s.isBooked = false ")
    List<Slot> findAllByTeacherWhereIsBookedIsFalse(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :id")
    List<Slot> findAllByTeacherId(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.slot.dto.SlotDTO(s.id, s.time, s.dayOfWeek) " +
            "FROM Slot s WHERE s.id = :id")
    Optional<SlotDTO> findOneSlotDtoById(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :teacherId AND s.regularStudent.id = :studentId")
    List<Slot> findAllByTeacherIdAndStudentId(@Param("teacherId") Long teacherId,
                                              @Param("studentId") Long studentId);

    @Query("SELECT new pl.dinosauruski.slot.dto.SlotInfoDTO(s.id, s.dayOfWeek,s.time,s.regularStudent) " +
            "FROM Slot s WHERE s.teacher.id = :id AND s.isBooked = TRUE")
    List<SlotInfoDTO> findAllBookedSlotInfoByTeacherId(@Param("id") Long id);
}
