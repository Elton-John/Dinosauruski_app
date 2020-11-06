package pl.dinosauruski.availableSlot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.dinosauruski.models.AvailableSlot;

public class AvailableSlotConverter implements Converter<String, AvailableSlot> {
    @Autowired
    private AvailableSlotRepository availableSlotRepository;

    @Override
    public AvailableSlot convert(String source) {
        return availableSlotRepository.getOne(Long.parseLong(source));
    }
}