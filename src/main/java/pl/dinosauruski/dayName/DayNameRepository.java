package pl.dinosauruski.dayName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.DayName;

import java.util.List;

@Repository
public interface DayNameRepository extends JpaRepository<DayName, Integer> {

    List<DayName> findAllByIsDayOff(boolean isOff);
}
