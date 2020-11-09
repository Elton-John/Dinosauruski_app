package pl.dinosauruski.student.dto;

import lombok.Getter;
import lombok.Setter;
import pl.dinosauruski.models.Teacher;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class StudentSlotsDTO {
    private Long id;
    private Set<Teacher> teachers = new HashSet<>();
}
