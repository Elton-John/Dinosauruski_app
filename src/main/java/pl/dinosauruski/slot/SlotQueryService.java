package pl.dinosauruski.slot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Slot;
import pl.dinosauruski.slot.dto.SlotDTO;
import pl.dinosauruski.slot.dto.SlotInfoDTO;

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

    public List<Slot> getAllFreeSlotsByTeacher(Long id) {
        return slotRepository.findAllByTeacherWhereIsBookedIsFalse(id);
    }

    public SlotDTO getOneSlotDtoOrThrow(Long id) {
        return slotRepository.findOneSlotDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Slot> getSlots(Long teacherId, Long studentId) {
        return slotRepository.findAllByTeacherIdAndStudentId(teacherId, studentId);
    }

    public List<SlotInfoDTO> getAllBookedSlotInfoDtoByTeacher(Long id) {
        return slotRepository.findAllBookedSlotInfoByTeacherId(id);
    }
}
