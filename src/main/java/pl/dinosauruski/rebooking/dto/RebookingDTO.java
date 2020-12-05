package pl.dinosauruski.rebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dinosauruski.models.Student;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RebookingDTO {
    private Long id;
    private Student notRegularStudent;
}
