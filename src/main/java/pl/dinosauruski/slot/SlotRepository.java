package pl.dinosauruski.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.SlotDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Query("select slot.dayName.id FROM Slot slot")
    Set<Integer> findAllDayNameId();

    @Query("select slot from Slot slot WHERE slot.isBooked = false ")
    List<Slot> findAllWhereIsBookedIsFalse();

    List<Slot> findAllByTeacherId(Long id);

    @Query("SELECT new pl.dinosauruski.slot.dto.SlotDTO(s.id, s.dayName,s.time) " +
            "FROM Slot s WHERE s.id = :id")
    Optional<SlotDTO> findOneSlotDtoById(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :teacherId AND s.regularStudent.id = :studentId")
    List<Slot> findAllByTeacherIdAndStudentId(@Param("teacherId") Long teacherId,
                                              @Param("studentId") Long studentId);

}
