package pl.dinosauruski.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.slot.dto.FreeSlotDTO;
import pl.dinosauruski.slot.dto.SlotInfoDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :id AND s.archived=false")
    List<Slot> findAllByTeacherId(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :id AND s.booked = false AND s.archived=false ")
    List<Slot> findAllByTeacherWhereBookedIsFalse(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.teacher.id = :teacherId AND s.regularStudent.id = :studentId AND s.archived=false")
    List<Slot> findAllByTeacherIdAndStudentId(@Param("teacherId") Long teacherId,
                                              @Param("studentId") Long studentId);

    @Query("SELECT new pl.dinosauruski.slot.dto.FreeSlotDTO(s.id, s.time, s.dayOfWeek) " +
            "FROM Slot s WHERE s.id = :id")
    Optional<FreeSlotDTO> findOneFreeSlotDtoById(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.slot.dto.BookedSlotDTO(s.id, s.time, s.dayOfWeek) " +
            "FROM Slot s WHERE s.id = :id")
    Optional<BookedSlotDTO> findOneBookedSlotDtoById(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.slot.dto.SlotInfoDTO(s.id, s.dayOfWeek,s.time,s.regularStudent) " +
            "FROM Slot s WHERE s.teacher.id = :id AND s.booked = TRUE ")
    List<SlotInfoDTO> findAllBookedSlotInfoByTeacherId(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.slot.dto.FreeSlotDTO(s.id, s.time, s.dayOfWeek) FROM Slot s WHERE s.teacher.id = :id AND s.archived = false ")
    List<FreeSlotDTO> findAllFreeSlotDTOByTeacherWhereBookedIsFalse(@Param("id") Long teacherId);

    @Query("SELECT COUNT (s) FROM Slot s where s.regularStudent.id = :id AND s.archived = false")
    int findAllByStudent(@Param("id") Long studentId);
}
