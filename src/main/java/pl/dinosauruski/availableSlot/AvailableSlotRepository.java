package pl.dinosauruski.availableSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.AvailableSlot;

import java.util.Set;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {


    @Modifying
    @Query("update AvailableSlot slot set slot.regularStudent = null where slot.id =  :id")
    void deleteStudentReference(@Param("id") Long id);

    @Query("select slot.dayName.id FROM AvailableSlot slot")
    Set<Integer> findAllDayNameId();
}
