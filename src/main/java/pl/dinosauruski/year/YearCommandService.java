package pl.dinosauruski.year;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.YearForTeacher;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class YearCommandService {
    private YearRepository yearRepository;
    private TeacherQueryService teacherQueryService;

    public YearForTeacher create(int year, Long teacherId) {
        YearForTeacher yearForTeacher = new YearForTeacher();
        yearForTeacher.setTeacher(teacherQueryService.getOneOrThrow(teacherId));
        yearForTeacher.setYear(year);
        yearForTeacher.setIsArchived(false);
        yearForTeacher.setIsGenerated(false);
        YearForTeacher saveYear = yearRepository.save(yearForTeacher);
        return saveYear;
    }



    public void setGenerated(YearForTeacher yearForTeacher) {
        yearForTeacher.setIsGenerated(true);
        yearRepository.save(yearForTeacher);
    }
}
