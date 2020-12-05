package pl.dinosauruski.slot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.dinosauruski.models.Slot;

public class SlotConverter implements Converter<String, Slot> {
    @Autowired
    private SlotRepository slotRepository;

    @Override
    public Slot convert(String source) {
        return slotRepository.getOne(Long.parseLong(source));
    }
}