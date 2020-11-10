package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.SlotDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SlotQueryService {

    private final SlotRepository slotRepository;

    public List<Slot> showAllSlotsByTeacherId(Long id) {
        return slotRepository.findAllByTeacherId(id);
    }

    public Slot getOneOrThrow(Long id) {
        return slotRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Slot> getAllFreeSlots() {
        return slotRepository.findAllWhereIsBookedIsFalse();
    }

    public SlotDTO getOneSlotDtoOrThrow(Long id) {
        return slotRepository.findOneSlotDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Slot> getSlots(Long teacherId, Long studentId) {
        return slotRepository.findAllByTeacherIdAndStudentId(teacherId, studentId);
    }

}
