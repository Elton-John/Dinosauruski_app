package pl.dinosauruski.dayName;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.DayName;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DayNameCommandService {
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
}
