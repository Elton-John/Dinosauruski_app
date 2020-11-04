package pl.dinosauruski.dayName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.AvailableSlot;
import pl.dinosauruski.models.DayName;

import java.util.List;
import java.util.Set;

@Repository
public interface DayNameRepository extends JpaRepository<DayName, Integer> {

    List<DayName> findAllByIsDayOff(boolean isOff);

//@Query("SELECT dayName.id  from availableSlot.dayName dayName WHERE dayName.id Is Not null")
//    Set<Integer> findAllDaysInUsage();


}
