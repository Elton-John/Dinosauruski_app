package pl.dinosauruski.rebooking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Rebooking;
import pl.dinosauruski.rebooking.dto.RebookingDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RebookingQueryService {
    private RebookingRepository rebookingRepository;

    public RebookingDTO getOneDtoOrThrow(Long lessonId) {
        return rebookingRepository.findOneDtoById(lessonId).orElseThrow(EntityNotFoundException::new);
    }

    public Rebooking getOneOrThrow(Long id) {
        return rebookingRepository.findOneById(id).orElseThrow(EntityNotFoundException::new);
    }
}
