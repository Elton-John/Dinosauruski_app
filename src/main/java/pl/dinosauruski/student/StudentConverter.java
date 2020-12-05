package pl.dinosauruski.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.dinosauruski.models.Student;

public class StudentConverter implements Converter<String, Student>  {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student convert(String source) {
        return studentRepository.getOne(Long.parseLong(source));
    }
}
