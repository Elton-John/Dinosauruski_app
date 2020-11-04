package pl.dinosauruski.dayName;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.DayName;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@AllArgsConstructor
@Service
public class DayNameService {

    private final DayNameRepository dayNameRepository;

    public void markAsWorkDay(Integer id) {
        DayName dayName = dayNameRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        boolean off = dayName.isDayOff();
        if (off) {
            dayName.setDayOff(false);
            dayNameRepository.save(dayName);
        }
    }

    public List<DayName> showAllDays() {
        return dayNameRepository.findAll();
    }

    public List<DayName> showAllWorkDays() {
        return dayNameRepository.findAllByIsDayOff(false);
    }

//    public void checkIsDayOff(Integer oldDayNameId) {
//        Set<Integer> daysId = dayNameRepository.findAllDaysInUsage();
//        if (!daysId.contains(oldDayNameId)){
//            dayNameRepository.markAsDayOff(oldDayNameId);
//        }
//    }

    public void markAsDayOff(Integer oldDayNameId) {
        DayName dayName = dayNameRepository.findById(oldDayNameId)
                .orElseThrow(EntityNotFoundException::new);
        dayName.setDayOff(true);
        dayNameRepository.save(dayName);
    }

    public DayName getById(int id) {
       return dayNameRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
