package pl.dinosauruski.availableSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.AvailableSlot;

import java.util.List;
import java.util.Set;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {

    @Query("select slot.dayName.id FROM AvailableSlot slot")
    Set<Integer> findAllDayNameId();

    @Query("select slot from AvailableSlot slot WHERE slot.isBooked = false ")
    List<AvailableSlot> findAllWhereIsBookedIsFalse();
}
