package pl.dinosauruski.lesson;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.dinosauruski.models.Lesson;

public class LessonConverter implements Converter<String, Lesson> {
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public Lesson convert(String source) {
        return lessonRepository.getOne(Long.parseLong(source));
    }}