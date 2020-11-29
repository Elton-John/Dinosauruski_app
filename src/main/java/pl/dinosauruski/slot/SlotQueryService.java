package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.BookedSlotDTO;
import pl.dinosauruski.slot.dto.FreeSlotDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SlotQueryService {

    private final SlotRepository slotRepository;

    public List<Slot> getAllSlotsByTeacherId(Long id) {
        return slotRepository.findAllByTeacherId(id);
    }

    public List<Slot> getSlots(Long teacherId, Long studentId) {
        return slotRepository.findAllByTeacherIdAndStudentId(teacherId, studentId);
    }

    public List<Slot> getAllFreeSlotsByTeacher(Long id) {
        return slotRepository.findAllByTeacherWhereBookedIsFalse(id);
    }

    public Slot getOneOrThrow(Long id) {
        return slotRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BookedSlotDTO getOneBookedSlotDtoOrThrow(Long id) {
        return slotRepository.findOneBookedSlotDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public FreeSlotDTO getOneFreeSlotDtoOrThrow(Long id) {
        return slotRepository.findOneFreeSlotDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<FreeSlotDTO> getAllFreeSlotDTOsByTeacher(Long teacherId) {
        return slotRepository.findAllFreeSlotDTOByTeacherWhereBookedIsFalse(teacherId);
    }

}
