package pl.dinosauruski.dayName;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.DayName;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@AllArgsConstructor
@Service
public class DayNameQueryService {

    private final DayNameRepository dayNameRepository;



    public List<DayName> showAllDays() {
        return dayNameRepository.findAll();
    }

    public List<DayName> showAllWorkDays() {
        return dayNameRepository.findAllByIsDayOff(false);
    }

    public DayName getById(int id) {
        return dayNameRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
