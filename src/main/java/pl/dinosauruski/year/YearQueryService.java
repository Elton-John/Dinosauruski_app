package pl.dinosauruski.year;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.YearForTeacher;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class YearQueryService {
    private YearRepository yearRepository;


    public boolean checkYearExist(int year, Long teacherId) {
        Optional<YearForTeacher> optionalYear = yearRepository.findByYearAndTeacherId(year, teacherId);
        return optionalYear.isPresent();
    }


    public YearForTeacher getYearForTeacher(int year, Long teacherId) {
        return yearRepository.findByYearAndTeacherId(year, teacherId).orElseThrow(EntityNotFoundException::new);
    }


}
