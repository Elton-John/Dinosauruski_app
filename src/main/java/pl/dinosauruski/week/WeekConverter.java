package pl.dinosauruski.week;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.dinosauruski.models.Week;

public class WeekConverter implements Converter<String, Week> {
    @Autowired
    private WeekRepository weekRepository;

    @Override
    public Week convert(String source) {
        return weekRepository.getOne(Long.parseLong(source));
    }
}
